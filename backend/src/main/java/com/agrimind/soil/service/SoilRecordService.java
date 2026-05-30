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
public class SoilRecordService {

    private final SoilTestRecordMapper soilTestRecordMapper;
    private final FieldInfoMapper fieldInfoMapper;
    private final CropInfoMapper cropInfoMapper;

    public SoilRecordService(SoilTestRecordMapper soilTestRecordMapper,
                             FieldInfoMapper fieldInfoMapper,
                             CropInfoMapper cropInfoMapper) {
        this.soilTestRecordMapper = soilTestRecordMapper;
        this.fieldInfoMapper = fieldInfoMapper;
        this.cropInfoMapper = cropInfoMapper;
    }

    @Transactional
    public SoilRecordVO create(Long currentUserId, SoilRecordSaveRequest request) {
        FieldInfo fieldInfo = getOwnedField(currentUserId, request.getFieldId());
        CropInfo cropInfo = getCropIfPresent(request.getCropId());
        String testNo = trimToNull(request.getTestNo());
        checkTestNoUnique(testNo, null);

        SoilTestRecord record = new SoilTestRecord();
        applyRequest(record, request);
        record.setDeleted(0);
        soilTestRecordMapper.insert(record);
        return SoilRecordVO.from(record, fieldInfo.getFieldName(), cropName(cropInfo));
    }

    @Transactional
    public SoilRecordVO update(Long currentUserId, Long id, SoilRecordSaveRequest request) {
        SoilTestRecord record = getOwnedRecord(currentUserId, id);
        FieldInfo fieldInfo = getOwnedField(currentUserId, request.getFieldId());
        CropInfo cropInfo = getCropIfPresent(request.getCropId());
        String testNo = trimToNull(request.getTestNo());
        checkTestNoUnique(testNo, id);

        applyRequest(record, request);
        soilTestRecordMapper.updateById(record);
        return SoilRecordVO.from(record, fieldInfo.getFieldName(), cropName(cropInfo));
    }

    @Transactional
    public void delete(Long currentUserId, Long id) {
        getOwnedRecord(currentUserId, id);
        soilTestRecordMapper.deleteById(id);
    }

    public SoilRecordVO detail(Long currentUserId, Long id) {
        SoilTestRecord record = getOwnedRecord(currentUserId, id);
        FieldInfo fieldInfo = getOwnedField(currentUserId, record.getFieldId());
        CropInfo cropInfo = getCropIfPresent(record.getCropId());
        return SoilRecordVO.from(record, fieldInfo.getFieldName(), cropName(cropInfo));
    }

    public PageResult<SoilRecordVO> page(Long currentUserId, SoilRecordPageQuery query) {
        validateTimeRange(query);

        List<FieldInfo> scopedFields = getScopedFields(currentUserId, query.getFieldId());
        if (scopedFields.isEmpty()) {
            return emptyPage(query);
        }

        Page<SoilTestRecord> page = Page.of(query.getPageNum(), query.getPageSize());
        Page<SoilTestRecord> result = soilTestRecordMapper.selectPage(page, buildPageWrapper(query, scopedFields));
        Map<Long, String> fieldNames = toFieldNameMap(scopedFields);
        Map<Long, String> cropNames = toCropNameMap(result.getRecords());

        Page<SoilRecordVO> voPage = Page.of(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(record -> SoilRecordVO.from(record, fieldNames.get(record.getFieldId()), cropNames.get(record.getCropId())))
                .toList());
        return PageResult.from(voPage);
    }

    private LambdaQueryWrapper<SoilTestRecord> buildPageWrapper(SoilRecordPageQuery query, List<FieldInfo> scopedFields) {
        List<Long> fieldIds = scopedFields.stream().map(FieldInfo::getId).toList();
        return Wrappers.<SoilTestRecord>lambdaQuery()
                .in(SoilTestRecord::getFieldId, fieldIds)
                .eq(query.getCropId() != null, SoilTestRecord::getCropId, query.getCropId())
                .eq(StringUtils.hasText(query.getRiskLevel()), SoilTestRecord::getRiskLevel, query.getRiskLevel())
                .ge(query.getStartTime() != null, SoilTestRecord::getSampleTime, query.getStartTime())
                .le(query.getEndTime() != null, SoilTestRecord::getSampleTime, query.getEndTime())
                .orderByDesc(SoilTestRecord::getSampleTime)
                .orderByDesc(SoilTestRecord::getCreateTime);
    }

    private List<FieldInfo> getScopedFields(Long currentUserId, Long fieldId) {
        LambdaQueryWrapper<FieldInfo> wrapper = Wrappers.<FieldInfo>lambdaQuery()
                .eq(FieldInfo::getOwnerUserId, currentUserId)
                .eq(fieldId != null, FieldInfo::getId, fieldId);
        return fieldInfoMapper.selectList(wrapper);
    }

    private SoilTestRecord getOwnedRecord(Long currentUserId, Long id) {
        SoilTestRecord record = soilTestRecordMapper.selectById(id);
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

    private CropInfo getCropIfPresent(Long cropId) {
        if (cropId == null) {
            return null;
        }
        CropInfo cropInfo = cropInfoMapper.selectById(cropId);
        if (cropInfo == null) {
            throw new BusinessException(404, "作物不存在");
        }
        return cropInfo;
    }

    private void checkTestNoUnique(String testNo, Long currentId) {
        LambdaQueryWrapper<SoilTestRecord> wrapper = Wrappers.<SoilTestRecord>lambdaQuery()
                .eq(SoilTestRecord::getTestNo, testNo)
                .ne(currentId != null, SoilTestRecord::getId, currentId);
        Long count = soilTestRecordMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new BusinessException(400, "检测编号已存在");
        }
    }

    private void applyRequest(SoilTestRecord record, SoilRecordSaveRequest request) {
        record.setTestNo(request.getTestNo().trim());
        record.setFieldId(request.getFieldId());
        record.setCropId(request.getCropId());
        record.setSampleTime(request.getTestTime());
        record.setSampleDepthCm(request.getSampleDepthCm());
        record.setPhValue(request.getPh());
        record.setOrganicMatterGKg(request.getOrganicMatter());
        record.setNitrogenMgKg(request.getNitrogen());
        record.setPhosphorusMgKg(request.getPhosphorus());
        record.setPotassiumMgKg(request.getPotassium());
        record.setMoisturePercent(request.getMoisture());
        record.setTemperatureCelsius(request.getTemperature());
        record.setConductivityUsCm(request.getConductivity());
        record.setTesterName(trimToNull(request.getTesterName()));
        record.setDataSource(defaultDataSource(request.getDataSource()));
        record.setRiskLevel(SoilRecordVO.DEFAULT_RISK_LEVEL);
        record.setRemark(trimToNull(request.getRemark()));
    }

    private void validateTimeRange(SoilRecordPageQuery query) {
        if (query.getStartTime() != null
                && query.getEndTime() != null
                && query.getStartTime().isAfter(query.getEndTime())) {
            throw new BusinessException(400, "开始时间不能晚于结束时间");
        }
    }

    private PageResult<SoilRecordVO> emptyPage(SoilRecordPageQuery query) {
        Page<SoilRecordVO> page = Page.of(query.getPageNum(), query.getPageSize(), 0);
        page.setRecords(Collections.emptyList());
        return PageResult.from(page);
    }

    private Map<Long, String> toFieldNameMap(List<FieldInfo> fields) {
        return fields.stream().collect(Collectors.toMap(FieldInfo::getId, FieldInfo::getFieldName));
    }

    private Map<Long, String> toCropNameMap(List<SoilTestRecord> records) {
        Set<Long> cropIds = records.stream()
                .map(SoilTestRecord::getCropId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (cropIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return cropInfoMapper.selectByIds(cropIds).stream()
                .collect(Collectors.toMap(CropInfo::getId, CropInfo::getCropName));
    }

    private String cropName(CropInfo cropInfo) {
        return cropInfo == null ? null : cropInfo.getCropName();
    }

    private String defaultDataSource(String value) {
        String dataSource = trimToNull(value);
        return dataSource == null ? "MANUAL" : dataSource;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
