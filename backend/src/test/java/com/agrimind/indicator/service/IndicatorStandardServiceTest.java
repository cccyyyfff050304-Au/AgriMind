package com.agrimind.indicator.service;

import com.agrimind.common.exception.BusinessException;
import com.agrimind.common.result.PageResult;
import com.agrimind.crop.entity.CropInfo;
import com.agrimind.crop.mapper.CropInfoMapper;
import com.agrimind.indicator.dto.IndicatorStandardPageQuery;
import com.agrimind.indicator.dto.IndicatorStandardSaveRequest;
import com.agrimind.indicator.entity.SoilIndicatorStandard;
import com.agrimind.indicator.mapper.SoilIndicatorStandardMapper;
import com.agrimind.indicator.vo.IndicatorStandardVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndicatorStandardServiceTest {

    @Mock
    private SoilIndicatorStandardMapper standardMapper;

    @Mock
    private CropInfoMapper cropInfoMapper;

    private IndicatorStandardService standardService;

    @BeforeEach
    void setUp() {
        standardService = new IndicatorStandardService(standardMapper, cropInfoMapper);
    }

    @Test
    void createShouldSaveStandard() {
        IndicatorStandardSaveRequest request = request("nitrogen");
        when(cropInfoMapper.selectById(1L)).thenReturn(crop());
        when(standardMapper.selectCount(anyStandardWrapper())).thenReturn(0L);
        when(standardMapper.insert(any(SoilIndicatorStandard.class))).thenAnswer(invocation -> {
            SoilIndicatorStandard standard = invocation.getArgument(0);
            standard.setId(1L);
            return 1;
        });

        IndicatorStandardVO result = standardService.create(request);

        ArgumentCaptor<SoilIndicatorStandard> standardCaptor = ArgumentCaptor.forClass(SoilIndicatorStandard.class);
        verify(standardMapper).insert(standardCaptor.capture());
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getIndicatorName()).isEqualTo("nitrogen");
        assertThat(result.getIndicatorLabel()).isEqualTo("有效氮");
        assertThat(standardCaptor.getValue().getIndicatorCode()).isEqualTo("NITROGEN");
        assertThat(standardCaptor.getValue().getLevelCode()).isEqualTo("NORMAL");
        assertThat(standardCaptor.getValue().getDeleted()).isZero();
    }

    @Test
    void createShouldRejectInvalidIndicatorName() {
        IndicatorStandardSaveRequest request = request("salt");

        assertThatThrownBy(() -> standardService.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("指标名称只能是");
    }

    @Test
    void createShouldRejectInvalidRange() {
        IndicatorStandardSaveRequest request = request("ph");
        request.setMinValue(new BigDecimal("7.50"));
        request.setMaxValue(new BigDecimal("6.00"));

        assertThatThrownBy(() -> standardService.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("标准最小值必须小于标准最大值");
    }

    @Test
    void createShouldRejectDuplicateStandard() {
        IndicatorStandardSaveRequest request = request("ph");
        when(cropInfoMapper.selectById(1L)).thenReturn(crop());
        when(standardMapper.selectCount(anyStandardWrapper())).thenReturn(1L);

        assertThatThrownBy(() -> standardService.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("该作物下指标标准已存在");
    }

    @Test
    void updateShouldModifyStandard() {
        IndicatorStandardSaveRequest request = request("ph");
        when(standardMapper.selectById(1L)).thenReturn(standard(1L, "ph"));
        when(cropInfoMapper.selectById(1L)).thenReturn(crop());
        when(standardMapper.selectCount(anyStandardWrapper())).thenReturn(0L);

        IndicatorStandardVO result = standardService.update(1L, request);

        verify(standardMapper).updateById(any(SoilIndicatorStandard.class));
        assertThat(result.getIndicatorName()).isEqualTo("ph");
    }

    @Test
    void deleteShouldUseMapperLogicalDelete() {
        when(standardMapper.selectById(1L)).thenReturn(standard(1L, "ph"));

        standardService.delete(1L);

        verify(standardMapper).deleteById(1L);
    }

    @Test
    void pageShouldReturnStandards() {
        IndicatorStandardPageQuery query = new IndicatorStandardPageQuery();
        Page<SoilIndicatorStandard> page = Page.of(1, 10, 1);
        page.setRecords(List.of(standard(1L, "ph")));
        when(standardMapper.selectPage(anyStandardPage(), anyStandardWrapper())).thenReturn(page);
        when(cropInfoMapper.selectByIds(anyCollection())).thenReturn(List.of(crop()));

        PageResult<IndicatorStandardVO> result = standardService.page(query);

        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getRecords().get(0).getCropName()).isEqualTo("Wheat");
    }

    @Test
    void listShouldReturnStandardsByCrop() {
        when(cropInfoMapper.selectById(1L)).thenReturn(crop());
        when(standardMapper.selectList(anyStandardWrapper())).thenReturn(List.of(standard(1L, "ph")));
        when(cropInfoMapper.selectByIds(anyCollection())).thenReturn(List.of(crop()));

        List<IndicatorStandardVO> result = standardService.list(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIndicatorName()).isEqualTo("ph");
    }

    private IndicatorStandardSaveRequest request(String indicatorName) {
        IndicatorStandardSaveRequest request = new IndicatorStandardSaveRequest();
        request.setCropId(1L);
        request.setIndicatorName(indicatorName);
        request.setMinValue(new BigDecimal("6.00"));
        request.setMaxValue(new BigDecimal("7.50"));
        request.setUnit("mg/kg");
        request.setSuggestion("demo");
        request.setStatus(1);
        return request;
    }

    private SoilIndicatorStandard standard(Long id, String indicatorName) {
        SoilIndicatorStandard standard = new SoilIndicatorStandard();
        standard.setId(id);
        standard.setCropId(1L);
        standard.setIndicatorCode(indicatorName.toUpperCase());
        standard.setIndicatorName(indicatorName);
        standard.setLevelCode("NORMAL");
        standard.setMinValue(new BigDecimal("6.00"));
        standard.setMaxValue(new BigDecimal("7.50"));
        standard.setUnit("mg/kg");
        standard.setStatus(1);
        standard.setDeleted(0);
        return standard;
    }

    private CropInfo crop() {
        CropInfo cropInfo = new CropInfo();
        cropInfo.setId(1L);
        cropInfo.setCropName("Wheat");
        cropInfo.setDeleted(0);
        return cropInfo;
    }

    @SuppressWarnings("unchecked")
    private Wrapper<SoilIndicatorStandard> anyStandardWrapper() {
        return any(Wrapper.class);
    }

    @SuppressWarnings("unchecked")
    private Page<SoilIndicatorStandard> anyStandardPage() {
        return any(Page.class);
    }
}
