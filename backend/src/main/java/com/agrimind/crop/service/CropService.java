package com.agrimind.crop.service;

import com.agrimind.common.exception.BusinessException;
import com.agrimind.common.result.PageResult;
import com.agrimind.crop.dto.CropPageQuery;
import com.agrimind.crop.dto.CropSaveRequest;
import com.agrimind.crop.entity.CropInfo;
import com.agrimind.crop.mapper.CropInfoMapper;
import com.agrimind.crop.vo.CropOptionVO;
import com.agrimind.crop.vo.CropVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CropService {

    private final CropInfoMapper cropInfoMapper;

    public CropService(CropInfoMapper cropInfoMapper) {
        this.cropInfoMapper = cropInfoMapper;
    }

    @Transactional
    public CropVO create(CropSaveRequest request) {
        validatePhRange(request.getSuitablePhMin(), request.getSuitablePhMax());
        String cropCode = trimToNull(request.getCropCode());
        checkCropCodeUnique(cropCode, null);

        CropInfo cropInfo = new CropInfo();
        applyRequest(cropInfo, request);
        cropInfo.setDeleted(0);
        cropInfoMapper.insert(cropInfo);
        return CropVO.from(cropInfo);
    }

    @Transactional
    public CropVO update(Long id, CropSaveRequest request) {
        validatePhRange(request.getSuitablePhMin(), request.getSuitablePhMax());
        CropInfo cropInfo = getCrop(id);
        String cropCode = trimToNull(request.getCropCode());
        checkCropCodeUnique(cropCode, id);

        applyRequest(cropInfo, request);
        cropInfoMapper.updateById(cropInfo);
        return CropVO.from(cropInfo);
    }

    @Transactional
    public void delete(Long id) {
        getCrop(id);
        cropInfoMapper.deleteById(id);
    }

    public CropVO detail(Long id) {
        return CropVO.from(getCrop(id));
    }

    public PageResult<CropVO> page(CropPageQuery query) {
        Page<CropInfo> page = Page.of(query.getPageNum(), query.getPageSize());
        Page<CropInfo> result = cropInfoMapper.selectPage(page, buildPageWrapper(query));
        Page<CropVO> voPage = Page.of(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(CropVO::from).toList());
        return PageResult.from(voPage);
    }

    public List<CropOptionVO> list() {
        return cropInfoMapper.selectList(Wrappers.<CropInfo>lambdaQuery()
                        .orderByDesc(CropInfo::getCreateTime))
                .stream()
                .map(CropOptionVO::from)
                .toList();
    }

    private CropInfo getCrop(Long id) {
        CropInfo cropInfo = cropInfoMapper.selectById(id);
        if (cropInfo == null) {
            throw new BusinessException(404, "作物不存在");
        }
        return cropInfo;
    }

    private LambdaQueryWrapper<CropInfo> buildPageWrapper(CropPageQuery query) {
        return Wrappers.<CropInfo>lambdaQuery()
                .like(StringUtils.hasText(query.getCropName()), CropInfo::getCropName, query.getCropName())
                .eq(StringUtils.hasText(query.getGrowthStage()), CropInfo::getCropCategory, query.getGrowthStage())
                .orderByDesc(CropInfo::getCreateTime);
    }

    private void applyRequest(CropInfo cropInfo, CropSaveRequest request) {
        cropInfo.setCropName(request.getCropName().trim());
        cropInfo.setCropCode(trimToNull(request.getCropCode()));
        cropInfo.setCropCategory(trimToNull(request.getCropCategory()));
        cropInfo.setGrowthCycleDays(request.getGrowthCycleDays());
        cropInfo.setSuitablePhMin(request.getSuitablePhMin());
        cropInfo.setSuitablePhMax(request.getSuitablePhMax());
        cropInfo.setRemark(trimToNull(request.getRemark()));
    }

    private void checkCropCodeUnique(String cropCode, Long currentId) {
        if (!StringUtils.hasText(cropCode)) {
            return;
        }
        LambdaQueryWrapper<CropInfo> wrapper = Wrappers.<CropInfo>lambdaQuery()
                .eq(CropInfo::getCropCode, cropCode)
                .ne(currentId != null, CropInfo::getId, currentId);
        Long count = cropInfoMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new BusinessException(400, "作物编码已存在");
        }
    }

    private void validatePhRange(BigDecimal min, BigDecimal max) {
        if (min != null && max != null && min.compareTo(max) > 0) {
            throw new BusinessException(400, "适宜pH最小值不能大于最大值");
        }
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
