package com.agrimind.indicator.service;

import com.agrimind.common.exception.BusinessException;
import com.agrimind.common.result.PageResult;
import com.agrimind.crop.entity.CropInfo;
import com.agrimind.crop.mapper.CropInfoMapper;
import com.agrimind.indicator.dto.IndicatorStandardPageQuery;
import com.agrimind.indicator.dto.IndicatorStandardSaveRequest;
import com.agrimind.indicator.entity.SoilIndicatorStandard;
import com.agrimind.indicator.enums.SoilIndicator;
import com.agrimind.indicator.mapper.SoilIndicatorStandardMapper;
import com.agrimind.indicator.vo.IndicatorStandardVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class IndicatorStandardService {

    private static final String NORMAL_LEVEL_CODE = "NORMAL";

    private final SoilIndicatorStandardMapper standardMapper;
    private final CropInfoMapper cropInfoMapper;

    public IndicatorStandardService(SoilIndicatorStandardMapper standardMapper, CropInfoMapper cropInfoMapper) {
        this.standardMapper = standardMapper;
        this.cropInfoMapper = cropInfoMapper;
    }

    @Transactional
    public IndicatorStandardVO create(IndicatorStandardSaveRequest request) {
        SoilIndicator indicator = SoilIndicator.fromName(request.getIndicatorName());
        validateRange(request);
        CropInfo cropInfo = getCrop(request.getCropId());
        checkUnique(request.getCropId(), indicator.getName(), null);

        SoilIndicatorStandard standard = new SoilIndicatorStandard();
        applyRequest(standard, request, indicator);
        standard.setDeleted(0);
        standardMapper.insert(standard);
        return IndicatorStandardVO.from(standard, cropInfo.getCropName());
    }

    @Transactional
    public IndicatorStandardVO update(Long id, IndicatorStandardSaveRequest request) {
        SoilIndicatorStandard standard = getStandard(id);
        SoilIndicator indicator = SoilIndicator.fromName(request.getIndicatorName());
        validateRange(request);
        CropInfo cropInfo = getCrop(request.getCropId());
        checkUnique(request.getCropId(), indicator.getName(), id);

        applyRequest(standard, request, indicator);
        standardMapper.updateById(standard);
        return IndicatorStandardVO.from(standard, cropInfo.getCropName());
    }

    @Transactional
    public void delete(Long id) {
        getStandard(id);
        standardMapper.deleteById(id);
    }

    public IndicatorStandardVO detail(Long id) {
        SoilIndicatorStandard standard = getStandard(id);
        CropInfo cropInfo = getCrop(standard.getCropId());
        return IndicatorStandardVO.from(standard, cropInfo.getCropName());
    }

    public PageResult<IndicatorStandardVO> page(IndicatorStandardPageQuery query) {
        SoilIndicator indicator = parseIndicatorIfPresent(query.getIndicatorName());
        Page<SoilIndicatorStandard> page = Page.of(query.getPageNum(), query.getPageSize());
        Page<SoilIndicatorStandard> result = standardMapper.selectPage(page, buildPageWrapper(query, indicator));
        Map<Long, String> cropNames = toCropNameMap(result.getRecords());

        Page<IndicatorStandardVO> voPage = Page.of(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(standard -> IndicatorStandardVO.from(standard, cropNames.get(standard.getCropId())))
                .toList());
        return PageResult.from(voPage);
    }

    public List<IndicatorStandardVO> list(Long cropId) {
        if (cropId != null) {
            getCrop(cropId);
        }
        List<SoilIndicatorStandard> standards = standardMapper.selectList(Wrappers.<SoilIndicatorStandard>lambdaQuery()
                .eq(cropId != null, SoilIndicatorStandard::getCropId, cropId)
                .orderByAsc(SoilIndicatorStandard::getIndicatorName));
        Map<Long, String> cropNames = toCropNameMap(standards);
        return standards.stream()
                .map(standard -> IndicatorStandardVO.from(standard, cropNames.get(standard.getCropId())))
                .toList();
    }

    private LambdaQueryWrapper<SoilIndicatorStandard> buildPageWrapper(IndicatorStandardPageQuery query,
                                                                       SoilIndicator indicator) {
        return Wrappers.<SoilIndicatorStandard>lambdaQuery()
                .eq(query.getCropId() != null, SoilIndicatorStandard::getCropId, query.getCropId())
                .eq(indicator != null, SoilIndicatorStandard::getIndicatorName, indicator == null ? null : indicator.getName())
                .orderByDesc(SoilIndicatorStandard::getCreateTime);
    }

    private void applyRequest(SoilIndicatorStandard standard,
                              IndicatorStandardSaveRequest request,
                              SoilIndicator indicator) {
        standard.setIndicatorCode(indicator.getCode());
        standard.setIndicatorName(indicator.getName());
        standard.setCropId(request.getCropId());
        standard.setSoilType(null);
        standard.setLevelCode(NORMAL_LEVEL_CODE);
        standard.setMinValue(request.getMinValue());
        standard.setMaxValue(request.getMaxValue());
        standard.setUnit(resolveUnit(request.getUnit(), indicator));
        standard.setSuggestion(trimToNull(request.getSuggestion()));
        standard.setStatus(request.getStatus() == null ? 1 : request.getStatus());
    }

    private SoilIndicatorStandard getStandard(Long id) {
        SoilIndicatorStandard standard = standardMapper.selectById(id);
        if (standard == null) {
            throw new BusinessException(404, "指标标准不存在");
        }
        return standard;
    }

    private CropInfo getCrop(Long cropId) {
        CropInfo cropInfo = cropInfoMapper.selectById(cropId);
        if (cropInfo == null) {
            throw new BusinessException(404, "作物不存在");
        }
        return cropInfo;
    }

    private void validateRange(IndicatorStandardSaveRequest request) {
        if (request.getMinValue().compareTo(request.getMaxValue()) >= 0) {
            throw new BusinessException(400, "标准最小值必须小于标准最大值");
        }
    }

    private void checkUnique(Long cropId, String indicatorName, Long currentId) {
        LambdaQueryWrapper<SoilIndicatorStandard> wrapper = Wrappers.<SoilIndicatorStandard>lambdaQuery()
                .eq(SoilIndicatorStandard::getCropId, cropId)
                .eq(SoilIndicatorStandard::getIndicatorName, indicatorName)
                .ne(currentId != null, SoilIndicatorStandard::getId, currentId);
        Long count = standardMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new BusinessException(400, "该作物下指标标准已存在");
        }
    }

    private SoilIndicator parseIndicatorIfPresent(String indicatorName) {
        if (!StringUtils.hasText(indicatorName)) {
            return null;
        }
        return SoilIndicator.fromName(indicatorName);
    }

    private Map<Long, String> toCropNameMap(List<SoilIndicatorStandard> standards) {
        Set<Long> cropIds = standards.stream()
                .map(SoilIndicatorStandard::getCropId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (cropIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return cropInfoMapper.selectByIds(cropIds).stream()
                .collect(Collectors.toMap(CropInfo::getId, CropInfo::getCropName));
    }

    private String resolveUnit(String requestUnit, SoilIndicator indicator) {
        String unit = trimToNull(requestUnit);
        return unit == null ? indicator.getDefaultUnit() : unit;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
