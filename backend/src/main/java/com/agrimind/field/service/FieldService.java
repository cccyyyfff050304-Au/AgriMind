package com.agrimind.field.service;

import com.agrimind.common.exception.BusinessException;
import com.agrimind.common.result.PageResult;
import com.agrimind.field.dto.FieldPageQuery;
import com.agrimind.field.dto.FieldSaveRequest;
import com.agrimind.field.entity.FieldInfo;
import com.agrimind.field.mapper.FieldInfoMapper;
import com.agrimind.field.vo.FieldOptionVO;
import com.agrimind.field.vo.FieldVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class FieldService {

    private final FieldInfoMapper fieldInfoMapper;

    public FieldService(FieldInfoMapper fieldInfoMapper) {
        this.fieldInfoMapper = fieldInfoMapper;
    }

    @Transactional
    public FieldVO create(Long currentUserId, FieldSaveRequest request) {
        String fieldCode = trimToNull(request.getFieldCode());
        checkFieldCodeUnique(fieldCode, null);

        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setOwnerUserId(currentUserId);
        applyRequest(fieldInfo, request);
        fieldInfo.setDeleted(0);
        fieldInfoMapper.insert(fieldInfo);
        return FieldVO.from(fieldInfo);
    }

    @Transactional
    public FieldVO update(Long currentUserId, Long id, FieldSaveRequest request) {
        FieldInfo fieldInfo = getOwnedField(currentUserId, id);
        String fieldCode = trimToNull(request.getFieldCode());
        checkFieldCodeUnique(fieldCode, id);

        applyRequest(fieldInfo, request);
        fieldInfoMapper.updateById(fieldInfo);
        return FieldVO.from(fieldInfo);
    }

    @Transactional
    public void delete(Long currentUserId, Long id) {
        getOwnedField(currentUserId, id);
        fieldInfoMapper.deleteById(id);
    }

    public FieldVO detail(Long currentUserId, Long id) {
        return FieldVO.from(getOwnedField(currentUserId, id));
    }

    public PageResult<FieldVO> page(Long currentUserId, FieldPageQuery query) {
        Page<FieldInfo> page = Page.of(query.getPageNum(), query.getPageSize());
        Page<FieldInfo> result = fieldInfoMapper.selectPage(page, buildPageWrapper(currentUserId, query));
        Page<FieldVO> voPage = Page.of(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(FieldVO::from).toList());
        return PageResult.from(voPage);
    }

    public List<FieldOptionVO> list(Long currentUserId) {
        return fieldInfoMapper.selectList(Wrappers.<FieldInfo>lambdaQuery()
                        .eq(FieldInfo::getOwnerUserId, currentUserId)
                        .orderByDesc(FieldInfo::getCreateTime))
                .stream()
                .map(FieldOptionVO::from)
                .toList();
    }

    private FieldInfo getOwnedField(Long currentUserId, Long id) {
        FieldInfo fieldInfo = fieldInfoMapper.selectOne(Wrappers.<FieldInfo>lambdaQuery()
                .eq(FieldInfo::getId, id)
                .eq(FieldInfo::getOwnerUserId, currentUserId));
        if (fieldInfo == null) {
            throw new BusinessException(404, "地块不存在");
        }
        return fieldInfo;
    }

    private LambdaQueryWrapper<FieldInfo> buildPageWrapper(Long currentUserId, FieldPageQuery query) {
        return Wrappers.<FieldInfo>lambdaQuery()
                .eq(FieldInfo::getOwnerUserId, currentUserId)
                .like(StringUtils.hasText(query.getFieldName()), FieldInfo::getFieldName, query.getFieldName())
                .like(StringUtils.hasText(query.getLocation()), FieldInfo::getLocation, query.getLocation())
                .eq(StringUtils.hasText(query.getSoilType()), FieldInfo::getSoilType, query.getSoilType())
                .orderByDesc(FieldInfo::getCreateTime);
    }

    private void applyRequest(FieldInfo fieldInfo, FieldSaveRequest request) {
        fieldInfo.setFieldName(request.getFieldName().trim());
        fieldInfo.setFieldCode(trimToNull(request.getFieldCode()));
        fieldInfo.setLocation(trimToNull(request.getLocation()));
        fieldInfo.setAreaMu(request.getAreaMu());
        fieldInfo.setSoilType(trimToNull(request.getSoilType()));
        fieldInfo.setLongitude(request.getLongitude());
        fieldInfo.setLatitude(request.getLatitude());
        fieldInfo.setRemark(trimToNull(request.getRemark()));
    }

    private void checkFieldCodeUnique(String fieldCode, Long currentId) {
        if (!StringUtils.hasText(fieldCode)) {
            return;
        }
        LambdaQueryWrapper<FieldInfo> wrapper = Wrappers.<FieldInfo>lambdaQuery()
                .eq(FieldInfo::getFieldCode, fieldCode)
                .ne(currentId != null, FieldInfo::getId, currentId);
        Long count = fieldInfoMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new BusinessException(400, "地块编码已存在");
        }
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
