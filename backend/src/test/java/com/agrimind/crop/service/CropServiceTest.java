package com.agrimind.crop.service;

import com.agrimind.common.exception.BusinessException;
import com.agrimind.common.result.PageResult;
import com.agrimind.crop.dto.CropPageQuery;
import com.agrimind.crop.dto.CropSaveRequest;
import com.agrimind.crop.entity.CropInfo;
import com.agrimind.crop.mapper.CropInfoMapper;
import com.agrimind.crop.vo.CropOptionVO;
import com.agrimind.crop.vo.CropVO;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CropServiceTest {

    @Mock
    private CropInfoMapper cropInfoMapper;

    private CropService cropService;

    @BeforeEach
    void setUp() {
        cropService = new CropService(cropInfoMapper);
    }

    @Test
    void createShouldSaveCrop() {
        CropSaveRequest request = request("Tomato");
        when(cropInfoMapper.selectCount(anyCropWrapper())).thenReturn(0L);
        when(cropInfoMapper.insert(any(CropInfo.class))).thenAnswer(invocation -> {
            CropInfo cropInfo = invocation.getArgument(0);
            cropInfo.setId(1L);
            return 1;
        });

        CropVO result = cropService.create(request);

        ArgumentCaptor<CropInfo> cropCaptor = ArgumentCaptor.forClass(CropInfo.class);
        verify(cropInfoMapper).insert(cropCaptor.capture());
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCropName()).isEqualTo("Tomato");
        assertThat(cropCaptor.getValue().getDeleted()).isZero();
    }

    @Test
    void createShouldRejectDuplicateCropCode() {
        CropSaveRequest request = request("Tomato");
        when(cropInfoMapper.selectCount(anyCropWrapper())).thenReturn(1L);

        assertThatThrownBy(() -> cropService.create(request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void updateShouldModifyExistingCrop() {
        CropSaveRequest request = request("Updated Tomato");
        when(cropInfoMapper.selectById(1L)).thenReturn(crop(1L, "Tomato"));
        when(cropInfoMapper.selectCount(anyCropWrapper())).thenReturn(0L);

        CropVO result = cropService.update(1L, request);

        ArgumentCaptor<CropInfo> cropCaptor = ArgumentCaptor.forClass(CropInfo.class);
        verify(cropInfoMapper).updateById(cropCaptor.capture());
        assertThat(result.getCropName()).isEqualTo("Updated Tomato");
        assertThat(cropCaptor.getValue().getCropName()).isEqualTo("Updated Tomato");
    }

    @Test
    void updateShouldRejectInvalidPhRange() {
        CropSaveRequest request = request("Tomato");
        request.setSuitablePhMin(new BigDecimal("7.50"));
        request.setSuitablePhMax(new BigDecimal("6.00"));

        assertThatThrownBy(() -> cropService.update(1L, request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void detailShouldRejectMissingCrop() {
        when(cropInfoMapper.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> cropService.detail(999L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void deleteShouldUseMapperLogicalDelete() {
        when(cropInfoMapper.selectById(1L)).thenReturn(crop(1L, "Tomato"));

        cropService.delete(1L);

        verify(cropInfoMapper).deleteById(1L);
    }

    @Test
    void pageShouldReturnCropVoPage() {
        CropPageQuery query = new CropPageQuery();
        Page<CropInfo> page = Page.of(1, 10, 1);
        page.setRecords(List.of(crop(1L, "Tomato")));
        when(cropInfoMapper.selectPage(anyCropPage(), anyCropWrapper())).thenReturn(page);

        PageResult<CropVO> result = cropService.page(query);

        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getRecords()).hasSize(1);
        assertThat(result.getRecords().get(0).getCropName()).isEqualTo("Tomato");
    }

    @Test
    void listShouldReturnCropOptions() {
        when(cropInfoMapper.selectList(anyCropWrapper())).thenReturn(List.of(crop(1L, "Tomato")));

        List<CropOptionVO> result = cropService.list();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getCropName()).isEqualTo("Tomato");
    }

    private CropSaveRequest request(String cropName) {
        CropSaveRequest request = new CropSaveRequest();
        request.setCropName(cropName);
        request.setCropCode("CROP001");
        request.setCropCategory("vegetable");
        request.setGrowthCycleDays(90);
        request.setSuitablePhMin(new BigDecimal("6.00"));
        request.setSuitablePhMax(new BigDecimal("7.50"));
        request.setRemark("demo");
        return request;
    }

    private CropInfo crop(Long id, String cropName) {
        CropInfo cropInfo = new CropInfo();
        cropInfo.setId(id);
        cropInfo.setCropName(cropName);
        cropInfo.setCropCode("CROP001");
        cropInfo.setCropCategory("vegetable");
        cropInfo.setGrowthCycleDays(90);
        cropInfo.setSuitablePhMin(new BigDecimal("6.00"));
        cropInfo.setSuitablePhMax(new BigDecimal("7.50"));
        cropInfo.setDeleted(0);
        return cropInfo;
    }

    @SuppressWarnings("unchecked")
    private Wrapper<CropInfo> anyCropWrapper() {
        return any(Wrapper.class);
    }

    @SuppressWarnings("unchecked")
    private Page<CropInfo> anyCropPage() {
        return any(Page.class);
    }
}
