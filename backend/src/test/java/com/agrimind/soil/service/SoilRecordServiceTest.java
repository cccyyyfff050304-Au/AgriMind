package com.agrimind.soil.service;

import com.agrimind.common.exception.BusinessException;
import com.agrimind.common.result.PageResult;
import com.agrimind.crop.entity.CropInfo;
import com.agrimind.crop.mapper.CropInfoMapper;
import com.agrimind.field.entity.FieldInfo;
import com.agrimind.field.mapper.FieldInfoMapper;
import com.agrimind.soil.dto.SoilRecordPageQuery;
import com.agrimind.soil.dto.SoilRecordSaveRequest;
import com.agrimind.soil.entity.SoilTestRecord;
import com.agrimind.soil.mapper.SoilTestRecordMapper;
import com.agrimind.soil.vo.SoilRecordVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SoilRecordServiceTest {

    private static final Long CURRENT_USER_ID = 10L;

    @Mock
    private SoilTestRecordMapper soilTestRecordMapper;

    @Mock
    private FieldInfoMapper fieldInfoMapper;

    @Mock
    private CropInfoMapper cropInfoMapper;

    private SoilRecordService soilRecordService;

    @BeforeEach
    void setUp() {
        soilRecordService = new SoilRecordService(soilTestRecordMapper, fieldInfoMapper, cropInfoMapper);
    }

    @Test
    void createShouldSaveOwnedFieldRecord() {
        SoilRecordSaveRequest request = request();
        when(fieldInfoMapper.selectOne(anyFieldWrapper())).thenReturn(field());
        when(cropInfoMapper.selectById(2L)).thenReturn(crop());
        when(soilTestRecordMapper.selectCount(anySoilWrapper())).thenReturn(0L);
        when(soilTestRecordMapper.insert(any(SoilTestRecord.class))).thenAnswer(invocation -> {
            SoilTestRecord record = invocation.getArgument(0);
            record.setId(1L);
            return 1;
        });

        SoilRecordVO result = soilRecordService.create(CURRENT_USER_ID, request);

        ArgumentCaptor<SoilTestRecord> recordCaptor = ArgumentCaptor.forClass(SoilTestRecord.class);
        verify(soilTestRecordMapper).insert(recordCaptor.capture());
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFieldName()).isEqualTo("North Field");
        assertThat(result.getCropName()).isEqualTo("Tomato");
        assertThat(result.getRiskLevel()).isEqualTo(SoilRecordVO.DEFAULT_RISK_LEVEL);
        assertThat(recordCaptor.getValue().getPhValue()).isEqualByComparingTo("6.80");
        assertThat(recordCaptor.getValue().getDeleted()).isZero();
    }

    @Test
    void createShouldRejectMissingField() {
        when(fieldInfoMapper.selectOne(anyFieldWrapper())).thenReturn(null);

        assertThatThrownBy(() -> soilRecordService.create(CURRENT_USER_ID, request()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("地块不存在");
    }

    @Test
    void createShouldRejectMissingCrop() {
        when(fieldInfoMapper.selectOne(anyFieldWrapper())).thenReturn(field());
        when(cropInfoMapper.selectById(2L)).thenReturn(null);

        assertThatThrownBy(() -> soilRecordService.create(CURRENT_USER_ID, request()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("作物不存在");
    }

    @Test
    void updateShouldModifyOwnedRecord() {
        SoilRecordSaveRequest request = request();
        request.setPh(new BigDecimal("7.10"));
        when(soilTestRecordMapper.selectById(1L)).thenReturn(record());
        when(fieldInfoMapper.selectOne(anyFieldWrapper())).thenReturn(field(), field());
        when(cropInfoMapper.selectById(2L)).thenReturn(crop());
        when(soilTestRecordMapper.selectCount(anySoilWrapper())).thenReturn(0L);

        SoilRecordVO result = soilRecordService.update(CURRENT_USER_ID, 1L, request);

        ArgumentCaptor<SoilTestRecord> recordCaptor = ArgumentCaptor.forClass(SoilTestRecord.class);
        verify(soilTestRecordMapper).updateById(recordCaptor.capture());
        assertThat(result.getPh()).isEqualByComparingTo("7.10");
        assertThat(recordCaptor.getValue().getPhValue()).isEqualByComparingTo("7.10");
    }

    @Test
    void detailShouldRejectMissingRecord() {
        when(soilTestRecordMapper.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> soilRecordService.detail(CURRENT_USER_ID, 999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("土壤检测记录不存在");
    }

    @Test
    void deleteShouldUseMapperLogicalDelete() {
        when(soilTestRecordMapper.selectById(1L)).thenReturn(record());
        when(fieldInfoMapper.selectOne(anyFieldWrapper())).thenReturn(field());

        soilRecordService.delete(CURRENT_USER_ID, 1L);

        verify(soilTestRecordMapper).deleteById(1L);
    }

    @Test
    void pageShouldReturnSoilRecordVoPage() {
        SoilRecordPageQuery query = new SoilRecordPageQuery();
        Page<SoilTestRecord> page = Page.of(1, 10, 1);
        page.setRecords(List.of(record()));
        when(fieldInfoMapper.selectList(anyFieldWrapper())).thenReturn(List.of(field()));
        when(soilTestRecordMapper.selectPage(anySoilPage(), anySoilWrapper())).thenReturn(page);
        when(cropInfoMapper.selectByIds(anyCollection())).thenReturn(List.of(crop()));

        PageResult<SoilRecordVO> result = soilRecordService.page(CURRENT_USER_ID, query);

        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getRecords()).hasSize(1);
        assertThat(result.getRecords().get(0).getFieldName()).isEqualTo("North Field");
        assertThat(result.getRecords().get(0).getCropName()).isEqualTo("Tomato");
    }

    @Test
    void pageShouldReturnEmptyForUnsupportedRiskLevel() {
        SoilRecordPageQuery query = new SoilRecordPageQuery();
        query.setRiskLevel("HIGH");

        PageResult<SoilRecordVO> result = soilRecordService.page(CURRENT_USER_ID, query);

        assertThat(result.getTotal()).isZero();
        assertThat(result.getRecords()).isEmpty();
    }

    @Test
    void pageShouldRejectInvalidTimeRange() {
        SoilRecordPageQuery query = new SoilRecordPageQuery();
        query.setStartTime(LocalDateTime.of(2026, 5, 30, 12, 0));
        query.setEndTime(LocalDateTime.of(2026, 5, 30, 10, 0));

        assertThatThrownBy(() -> soilRecordService.page(CURRENT_USER_ID, query))
                .isInstanceOf(BusinessException.class)
                .hasMessage("开始时间不能晚于结束时间");
    }

    private SoilRecordSaveRequest request() {
        SoilRecordSaveRequest request = new SoilRecordSaveRequest();
        request.setTestNo("SOIL20260530001");
        request.setFieldId(1L);
        request.setCropId(2L);
        request.setTestTime(LocalDateTime.of(2026, 5, 30, 9, 30));
        request.setSampleDepthCm(new BigDecimal("20.00"));
        request.setPh(new BigDecimal("6.80"));
        request.setOrganicMatter(new BigDecimal("25.50"));
        request.setNitrogen(new BigDecimal("120.00"));
        request.setPhosphorus(new BigDecimal("35.00"));
        request.setPotassium(new BigDecimal("180.00"));
        request.setMoisture(new BigDecimal("28.00"));
        request.setTemperature(new BigDecimal("22.50"));
        request.setConductivity(new BigDecimal("450.00"));
        request.setTesterName("tester");
        request.setDataSource("MANUAL");
        request.setRemark("demo");
        return request;
    }

    private SoilTestRecord record() {
        SoilTestRecord record = new SoilTestRecord();
        record.setId(1L);
        record.setTestNo("SOIL20260530001");
        record.setFieldId(1L);
        record.setCropId(2L);
        record.setSampleTime(LocalDateTime.of(2026, 5, 30, 9, 30));
        record.setSampleDepthCm(new BigDecimal("20.00"));
        record.setPhValue(new BigDecimal("6.80"));
        record.setOrganicMatterGKg(new BigDecimal("25.50"));
        record.setNitrogenMgKg(new BigDecimal("120.00"));
        record.setPhosphorusMgKg(new BigDecimal("35.00"));
        record.setPotassiumMgKg(new BigDecimal("180.00"));
        record.setMoisturePercent(new BigDecimal("28.00"));
        record.setTemperatureCelsius(new BigDecimal("22.50"));
        record.setConductivityUsCm(new BigDecimal("450.00"));
        record.setTesterName("tester");
        record.setDataSource("MANUAL");
        record.setRemark("demo");
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
        cropInfo.setCropName("Tomato");
        cropInfo.setDeleted(0);
        return cropInfo;
    }

    @SuppressWarnings("unchecked")
    private Wrapper<SoilTestRecord> anySoilWrapper() {
        return any(Wrapper.class);
    }

    @SuppressWarnings("unchecked")
    private Page<SoilTestRecord> anySoilPage() {
        return any(Page.class);
    }

    @SuppressWarnings("unchecked")
    private Wrapper<FieldInfo> anyFieldWrapper() {
        return any(Wrapper.class);
    }
}
