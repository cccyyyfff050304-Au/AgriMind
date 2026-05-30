package com.agrimind.indicator.service;

import com.agrimind.common.exception.BusinessException;
import com.agrimind.crop.entity.CropInfo;
import com.agrimind.crop.mapper.CropInfoMapper;
import com.agrimind.field.entity.FieldInfo;
import com.agrimind.field.mapper.FieldInfoMapper;
import com.agrimind.indicator.entity.SoilIndicatorResult;
import com.agrimind.indicator.entity.SoilIndicatorStandard;
import com.agrimind.indicator.mapper.SoilIndicatorResultMapper;
import com.agrimind.indicator.mapper.SoilIndicatorStandardMapper;
import com.agrimind.indicator.vo.SoilEvaluationVO;
import com.agrimind.soil.entity.SoilTestRecord;
import com.agrimind.soil.mapper.SoilTestRecordMapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SoilEvaluationServiceTest {

    private static final Long CURRENT_USER_ID = 10L;

    @Mock
    private SoilTestRecordMapper soilTestRecordMapper;

    @Mock
    private SoilIndicatorStandardMapper standardMapper;

    @Mock
    private SoilIndicatorResultMapper resultMapper;

    @Mock
    private FieldInfoMapper fieldInfoMapper;

    @Mock
    private CropInfoMapper cropInfoMapper;

    private SoilEvaluationService evaluationService;

    @BeforeEach
    void setUp() {
        evaluationService = new SoilEvaluationService(
                soilTestRecordMapper,
                standardMapper,
                resultMapper,
                fieldInfoMapper,
                cropInfoMapper
        );
    }

    @Test
    void evaluateShouldSaveResultsAndUpdateRiskLevel() {
        SoilTestRecord record = record();
        when(soilTestRecordMapper.selectById(1L)).thenReturn(record);
        when(fieldInfoMapper.selectOne(anyFieldWrapper())).thenReturn(field(), field());
        when(cropInfoMapper.selectById(2L)).thenReturn(crop());
        when(standardMapper.selectList(anyStandardWrapper())).thenReturn(standards());

        SoilEvaluationVO result = evaluationService.evaluate(CURRENT_USER_ID, 1L);

        ArgumentCaptor<SoilIndicatorResult> resultCaptor = ArgumentCaptor.forClass(SoilIndicatorResult.class);
        verify(resultMapper, times(6)).insert(resultCaptor.capture());
        verify(soilTestRecordMapper).updateById(record);
        assertThat(record.getRiskLevel()).isEqualTo("MEDIUM");
        assertThat(result.getRiskLevel()).isEqualTo("MEDIUM");
        assertThat(result.getResults()).hasSize(6);
        assertThat(resultCaptor.getAllValues())
                .extracting(SoilIndicatorResult::getResultLevel)
                .contains("LOW", "HIGH", "NORMAL");
    }

    @Test
    void evaluateShouldRejectMissingStandard() {
        when(soilTestRecordMapper.selectById(1L)).thenReturn(record());
        when(fieldInfoMapper.selectOne(anyFieldWrapper())).thenReturn(field(), field());
        when(cropInfoMapper.selectById(2L)).thenReturn(crop());
        when(standardMapper.selectList(anyStandardWrapper())).thenReturn(standards().subList(0, 5));

        assertThatThrownBy(() -> evaluationService.evaluate(CURRENT_USER_ID, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("缺少指标标准");
    }

    @Test
    void evaluateShouldRejectRecordWithoutCrop() {
        SoilTestRecord record = record();
        record.setCropId(null);
        when(soilTestRecordMapper.selectById(1L)).thenReturn(record);
        when(fieldInfoMapper.selectOne(anyFieldWrapper())).thenReturn(field(), field());

        assertThatThrownBy(() -> evaluationService.evaluate(CURRENT_USER_ID, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("土壤检测记录未关联作物");
    }

    @Test
    void getEvaluationShouldReturnSavedResults() {
        SoilTestRecord record = record();
        record.setRiskLevel("LOW");
        when(soilTestRecordMapper.selectById(1L)).thenReturn(record);
        when(fieldInfoMapper.selectOne(anyFieldWrapper())).thenReturn(field(), field());
        when(cropInfoMapper.selectById(2L)).thenReturn(crop());
        when(resultMapper.selectList(anyResultWrapper())).thenReturn(savedResults());

        SoilEvaluationVO result = evaluationService.getEvaluation(CURRENT_USER_ID, 1L);

        assertThat(result.getRiskLevel()).isEqualTo("LOW");
        assertThat(result.getResults()).hasSize(6);
    }

    @Test
    void getEvaluationShouldRejectNoSavedResults() {
        when(soilTestRecordMapper.selectById(1L)).thenReturn(record());
        when(fieldInfoMapper.selectOne(anyFieldWrapper())).thenReturn(field(), field());
        when(cropInfoMapper.selectById(2L)).thenReturn(crop());
        when(resultMapper.selectList(anyResultWrapper())).thenReturn(List.of());

        assertThatThrownBy(() -> evaluationService.getEvaluation(CURRENT_USER_ID, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("该检测记录尚未执行规则判断");
    }

    private SoilTestRecord record() {
        SoilTestRecord record = new SoilTestRecord();
        record.setId(1L);
        record.setFieldId(1L);
        record.setCropId(2L);
        record.setSampleTime(LocalDateTime.of(2026, 5, 30, 9, 30));
        record.setPhValue(new BigDecimal("6.80"));
        record.setNitrogenMgKg(new BigDecimal("70.00"));
        record.setPhosphorusMgKg(new BigDecimal("35.00"));
        record.setPotassiumMgKg(new BigDecimal("190.00"));
        record.setOrganicMatterGKg(new BigDecimal("25.00"));
        record.setMoisturePercent(new BigDecimal("20.00"));
        record.setDeleted(0);
        return record;
    }

    private FieldInfo field() {
        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setId(1L);
        fieldInfo.setOwnerUserId(CURRENT_USER_ID);
        fieldInfo.setFieldName("North Field");
        fieldInfo.setDeleted(0);
        return fieldInfo;
    }

    private CropInfo crop() {
        CropInfo cropInfo = new CropInfo();
        cropInfo.setId(2L);
        cropInfo.setCropName("Wheat");
        cropInfo.setDeleted(0);
        return cropInfo;
    }

    private List<SoilIndicatorStandard> standards() {
        return List.of(
                standard(11L, "PH", "ph", "pH", "6.00", "7.50", null),
                standard(12L, "NITROGEN", "nitrogen", "有效氮", "80.00", "120.00", "mg/kg"),
                standard(13L, "PHOSPHORUS", "phosphorus", "有效磷", "20.00", "40.00", "mg/kg"),
                standard(14L, "POTASSIUM", "potassium", "速效钾", "100.00", "180.00", "mg/kg"),
                standard(15L, "ORGANIC_MATTER", "organicMatter", "有机质", "15.00", "30.00", "g/kg"),
                standard(16L, "MOISTURE", "moisture", "含水率", "12.00", "25.00", "%")
        );
    }

    private List<SoilIndicatorResult> savedResults() {
        return standards().stream()
                .map(standard -> {
                    SoilIndicatorResult result = new SoilIndicatorResult();
                    result.setRecordId(1L);
                    result.setStandardId(standard.getId());
                    result.setIndicatorCode(standard.getIndicatorCode());
                    result.setIndicatorName(standard.getIndicatorName());
                    result.setMeasuredValue(standard.getMinValue());
                    result.setMinValue(standard.getMinValue());
                    result.setMaxValue(standard.getMaxValue());
                    result.setUnit(standard.getUnit());
                    result.setResultLevel("NORMAL");
                    result.setRiskLevel("LOW");
                    result.setResultText("正常");
                    return result;
                })
                .toList();
    }

    private SoilIndicatorStandard standard(Long id,
                                           String code,
                                           String name,
                                           String label,
                                           String minValue,
                                           String maxValue,
                                           String unit) {
        SoilIndicatorStandard standard = new SoilIndicatorStandard();
        standard.setId(id);
        standard.setIndicatorCode(code);
        standard.setIndicatorName(name);
        standard.setCropId(2L);
        standard.setLevelCode("NORMAL");
        standard.setMinValue(new BigDecimal(minValue));
        standard.setMaxValue(new BigDecimal(maxValue));
        standard.setUnit(unit);
        standard.setSuggestion(label + "建议");
        standard.setStatus(1);
        standard.setDeleted(0);
        return standard;
    }

    @SuppressWarnings("unchecked")
    private Wrapper<FieldInfo> anyFieldWrapper() {
        return any(Wrapper.class);
    }

    @SuppressWarnings("unchecked")
    private Wrapper<SoilIndicatorStandard> anyStandardWrapper() {
        return any(Wrapper.class);
    }

    @SuppressWarnings("unchecked")
    private Wrapper<SoilIndicatorResult> anyResultWrapper() {
        return any(Wrapper.class);
    }
}
