package com.agrimind.indicator.service;

import com.agrimind.common.exception.BusinessException;
import com.agrimind.crop.entity.CropInfo;
import com.agrimind.crop.mapper.CropInfoMapper;
import com.agrimind.field.entity.FieldInfo;
import com.agrimind.field.mapper.FieldInfoMapper;
import com.agrimind.indicator.entity.SoilIndicatorResult;
import com.agrimind.indicator.entity.SoilIndicatorStandard;
import com.agrimind.indicator.enums.SoilIndicator;
import com.agrimind.indicator.mapper.SoilIndicatorResultMapper;
import com.agrimind.indicator.mapper.SoilIndicatorStandardMapper;
import com.agrimind.indicator.vo.IndicatorResultVO;
import com.agrimind.indicator.vo.SoilEvaluationVO;
import com.agrimind.soil.entity.SoilTestRecord;
import com.agrimind.soil.mapper.SoilTestRecordMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SoilEvaluationService {

    private static final String RESULT_LOW = "LOW";
    private static final String RESULT_NORMAL = "NORMAL";
    private static final String RESULT_HIGH = "HIGH";
    private static final String RISK_LOW = "LOW";
    private static final String RISK_MEDIUM = "MEDIUM";
    private static final String RISK_HIGH = "HIGH";

    private final SoilTestRecordMapper soilTestRecordMapper;
    private final SoilIndicatorStandardMapper standardMapper;
    private final SoilIndicatorResultMapper resultMapper;
    private final FieldInfoMapper fieldInfoMapper;
    private final CropInfoMapper cropInfoMapper;

    public SoilEvaluationService(SoilTestRecordMapper soilTestRecordMapper,
                                 SoilIndicatorStandardMapper standardMapper,
                                 SoilIndicatorResultMapper resultMapper,
                                 FieldInfoMapper fieldInfoMapper,
                                 CropInfoMapper cropInfoMapper) {
        this.soilTestRecordMapper = soilTestRecordMapper;
        this.standardMapper = standardMapper;
        this.resultMapper = resultMapper;
        this.fieldInfoMapper = fieldInfoMapper;
        this.cropInfoMapper = cropInfoMapper;
    }

    @Transactional
    public SoilEvaluationVO evaluate(Long currentUserId, Long recordId) {
        SoilTestRecord record = getOwnedRecord(currentUserId, recordId);
        FieldInfo fieldInfo = getOwnedField(currentUserId, record.getFieldId());
        CropInfo cropInfo = getRecordCrop(record);
        Map<String, SoilIndicatorStandard> standards = getRequiredStandards(cropInfo.getId());

        resultMapper.delete(Wrappers.<SoilIndicatorResult>lambdaQuery()
                .eq(SoilIndicatorResult::getRecordId, recordId));

        List<SoilIndicatorResult> results = SoilIndicator.allowedNames().stream()
                .map(SoilIndicator::fromName)
                .map(indicator -> evaluateIndicator(record, standards.get(indicator.getName()), indicator))
                .toList();
        results.forEach(resultMapper::insert);

        String overallRiskLevel = calculateOverallRiskLevel(results);
        record.setRiskLevel(overallRiskLevel);
        soilTestRecordMapper.updateById(record);

        return toEvaluationVO(record.getId(), fieldInfo.getFieldName(), cropInfo.getCropName(), overallRiskLevel, results);
    }

    public SoilEvaluationVO getEvaluation(Long currentUserId, Long recordId) {
        SoilTestRecord record = getOwnedRecord(currentUserId, recordId);
        FieldInfo fieldInfo = getOwnedField(currentUserId, record.getFieldId());
        CropInfo cropInfo = getRecordCrop(record);
        List<SoilIndicatorResult> results = getSavedResults(recordId);
        if (results.isEmpty()) {
            throw new BusinessException(404, "该检测记录尚未执行规则判断");
        }
        return toEvaluationVO(record.getId(), fieldInfo.getFieldName(), cropInfo.getCropName(), record.getRiskLevel(), results);
    }

    private SoilIndicatorResult evaluateIndicator(SoilTestRecord record,
                                                  SoilIndicatorStandard standard,
                                                  SoilIndicator indicator) {
        BigDecimal value = indicator.getValue(record);
        if (value == null) {
            throw new BusinessException(400, "检测记录缺少指标值: " + indicator.getName());
        }

        String resultStatus = compare(value, standard.getMinValue(), standard.getMaxValue());
        SoilIndicatorResult result = new SoilIndicatorResult();
        result.setRecordId(record.getId());
        result.setStandardId(standard.getId());
        result.setIndicatorCode(indicator.getCode());
        result.setIndicatorName(indicator.getName());
        result.setMeasuredValue(value);
        result.setMinValue(standard.getMinValue());
        result.setMaxValue(standard.getMaxValue());
        result.setUnit(standard.getUnit());
        result.setResultLevel(resultStatus);
        result.setRiskLevel(indicatorRiskLevel(resultStatus));
        result.setResultText(message(indicator, resultStatus));
        result.setSuggestion(standard.getSuggestion());
        result.setDeleted(0);
        return result;
    }

    private String compare(BigDecimal value, BigDecimal minValue, BigDecimal maxValue) {
        if (value.compareTo(minValue) < 0) {
            return RESULT_LOW;
        }
        if (value.compareTo(maxValue) > 0) {
            return RESULT_HIGH;
        }
        return RESULT_NORMAL;
    }

    private String calculateOverallRiskLevel(List<SoilIndicatorResult> results) {
        long abnormalCount = results.stream()
                .filter(result -> !RESULT_NORMAL.equals(result.getResultLevel()))
                .count();
        if (abnormalCount == 0) {
            return RISK_LOW;
        }
        if (abnormalCount <= 2) {
            return RISK_MEDIUM;
        }
        return RISK_HIGH;
    }

    private String indicatorRiskLevel(String resultStatus) {
        return RESULT_NORMAL.equals(resultStatus) ? RISK_LOW : RISK_MEDIUM;
    }

    private String message(SoilIndicator indicator, String resultStatus) {
        return switch (resultStatus) {
            case RESULT_LOW -> indicator.getLabel() + "偏低";
            case RESULT_HIGH -> indicator.getLabel() + "偏高";
            default -> indicator.getLabel() + "正常";
        };
    }

    private Map<String, SoilIndicatorStandard> getRequiredStandards(Long cropId) {
        List<SoilIndicatorStandard> standards = standardMapper.selectList(Wrappers.<SoilIndicatorStandard>lambdaQuery()
                .eq(SoilIndicatorStandard::getCropId, cropId)
                .eq(SoilIndicatorStandard::getStatus, 1));
        Map<String, SoilIndicatorStandard> standardMap = standards.stream()
                .collect(Collectors.toMap(SoilIndicatorStandard::getIndicatorName, standard -> standard, (first, second) -> first));
        List<String> missingIndicators = SoilIndicator.allowedNames().stream()
                .filter(indicatorName -> !standardMap.containsKey(indicatorName))
                .toList();
        if (!missingIndicators.isEmpty()) {
            throw new BusinessException(400, "缺少指标标准: " + String.join(", ", missingIndicators));
        }
        standardMap.values().forEach(this::validateStandardRange);
        return standardMap;
    }

    private void validateStandardRange(SoilIndicatorStandard standard) {
        if (standard.getMinValue() == null || standard.getMaxValue() == null) {
            throw new BusinessException(400, "指标标准范围不完整: " + standard.getIndicatorName());
        }
    }

    private List<SoilIndicatorResult> getSavedResults(Long recordId) {
        return resultMapper.selectList(Wrappers.<SoilIndicatorResult>lambdaQuery()
                .eq(SoilIndicatorResult::getRecordId, recordId))
                .stream()
                .sorted(Comparator.comparingInt(this::indicatorOrder))
                .toList();
    }

    private int indicatorOrder(SoilIndicatorResult result) {
        return SoilIndicator.allowedNames().indexOf(result.getIndicatorName());
    }

    private SoilEvaluationVO toEvaluationVO(Long recordId,
                                            String fieldName,
                                            String cropName,
                                            String riskLevel,
                                            List<SoilIndicatorResult> results) {
        List<IndicatorResultVO> resultVOs = results.stream()
                .sorted(Comparator.comparingInt(this::indicatorOrder))
                .map(IndicatorResultVO::from)
                .toList();
        return new SoilEvaluationVO(recordId, fieldName, cropName, riskLevel, resultVOs);
    }

    private SoilTestRecord getOwnedRecord(Long currentUserId, Long recordId) {
        SoilTestRecord record = soilTestRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException(404, "土壤检测记录不存在");
        }
        getOwnedField(currentUserId, record.getFieldId());
        return record;
    }

    private FieldInfo getOwnedField(Long currentUserId, Long fieldId) {
        FieldInfo fieldInfo = fieldInfoMapper.selectOne(Wrappers.<FieldInfo>lambdaQuery()
                .eq(FieldInfo::getId, fieldId)
                .eq(FieldInfo::getOwnerUserId, currentUserId));
        if (fieldInfo == null) {
            throw new BusinessException(404, "地块不存在");
        }
        return fieldInfo;
    }

    private CropInfo getRecordCrop(SoilTestRecord record) {
        if (record.getCropId() == null) {
            throw new BusinessException(400, "土壤检测记录未关联作物");
        }
        CropInfo cropInfo = cropInfoMapper.selectById(record.getCropId());
        if (cropInfo == null) {
            throw new BusinessException(404, "作物不存在");
        }
        return cropInfo;
    }
}
