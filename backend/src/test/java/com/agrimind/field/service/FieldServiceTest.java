package com.agrimind.field.service;

import com.agrimind.common.exception.BusinessException;
import com.agrimind.common.result.PageResult;
import com.agrimind.field.dto.FieldPageQuery;
import com.agrimind.field.dto.FieldSaveRequest;
import com.agrimind.field.entity.FieldInfo;
import com.agrimind.field.mapper.FieldInfoMapper;
import com.agrimind.field.vo.FieldOptionVO;
import com.agrimind.field.vo.FieldVO;
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
class FieldServiceTest {

    private static final Long CURRENT_USER_ID = 10L;

    @Mock
    private FieldInfoMapper fieldInfoMapper;

    private FieldService fieldService;

    @BeforeEach
    void setUp() {
        fieldService = new FieldService(fieldInfoMapper);
    }

    @Test
    void createShouldSaveCurrentUserField() {
        FieldSaveRequest request = request("North Field");
        when(fieldInfoMapper.selectCount(anyFieldWrapper())).thenReturn(0L);
        when(fieldInfoMapper.insert(any(FieldInfo.class))).thenAnswer(invocation -> {
            FieldInfo fieldInfo = invocation.getArgument(0);
            fieldInfo.setId(1L);
            return 1;
        });

        FieldVO result = fieldService.create(CURRENT_USER_ID, request);

        ArgumentCaptor<FieldInfo> fieldCaptor = ArgumentCaptor.forClass(FieldInfo.class);
        verify(fieldInfoMapper).insert(fieldCaptor.capture());
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFieldName()).isEqualTo("North Field");
        assertThat(fieldCaptor.getValue().getOwnerUserId()).isEqualTo(CURRENT_USER_ID);
        assertThat(fieldCaptor.getValue().getDeleted()).isZero();
    }

    @Test
    void createShouldRejectDuplicateFieldCode() {
        FieldSaveRequest request = request("North Field");
        when(fieldInfoMapper.selectCount(anyFieldWrapper())).thenReturn(1L);

        assertThatThrownBy(() -> fieldService.create(CURRENT_USER_ID, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("地块编码已存在");
    }

    @Test
    void updateShouldModifyOwnedField() {
        FieldSaveRequest request = request("Updated Field");
        when(fieldInfoMapper.selectOne(anyFieldWrapper())).thenReturn(field(1L, "Old Field"));
        when(fieldInfoMapper.selectCount(anyFieldWrapper())).thenReturn(0L);

        FieldVO result = fieldService.update(CURRENT_USER_ID, 1L, request);

        ArgumentCaptor<FieldInfo> fieldCaptor = ArgumentCaptor.forClass(FieldInfo.class);
        verify(fieldInfoMapper).updateById(fieldCaptor.capture());
        assertThat(result.getFieldName()).isEqualTo("Updated Field");
        assertThat(fieldCaptor.getValue().getFieldName()).isEqualTo("Updated Field");
    }

    @Test
    void detailShouldRejectMissingField() {
        when(fieldInfoMapper.selectOne(anyFieldWrapper())).thenReturn(null);

        assertThatThrownBy(() -> fieldService.detail(CURRENT_USER_ID, 999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("地块不存在");
    }

    @Test
    void deleteShouldUseMapperLogicalDelete() {
        when(fieldInfoMapper.selectOne(anyFieldWrapper())).thenReturn(field(1L, "North Field"));

        fieldService.delete(CURRENT_USER_ID, 1L);

        verify(fieldInfoMapper).deleteById(1L);
    }

    @Test
    void pageShouldReturnFieldVoPage() {
        FieldPageQuery query = new FieldPageQuery();
        Page<FieldInfo> page = Page.of(1, 10, 1);
        page.setRecords(List.of(field(1L, "North Field")));
        when(fieldInfoMapper.selectPage(anyFieldPage(), anyFieldWrapper())).thenReturn(page);

        PageResult<FieldVO> result = fieldService.page(CURRENT_USER_ID, query);

        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getRecords()).hasSize(1);
        assertThat(result.getRecords().get(0).getFieldName()).isEqualTo("North Field");
    }

    @Test
    void listShouldReturnFieldOptions() {
        when(fieldInfoMapper.selectList(anyFieldWrapper())).thenReturn(List.of(field(1L, "North Field")));

        List<FieldOptionVO> result = fieldService.list(CURRENT_USER_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getFieldName()).isEqualTo("North Field");
    }

    private FieldSaveRequest request(String fieldName) {
        FieldSaveRequest request = new FieldSaveRequest();
        request.setFieldName(fieldName);
        request.setFieldCode("FIELD001");
        request.setLocation("Greenhouse A");
        request.setAreaMu(new BigDecimal("12.50"));
        request.setSoilType("loam");
        request.setLongitude(new BigDecimal("116.391000"));
        request.setLatitude(new BigDecimal("39.907000"));
        request.setRemark("demo");
        return request;
    }

    private FieldInfo field(Long id, String fieldName) {
        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setId(id);
        fieldInfo.setOwnerUserId(CURRENT_USER_ID);
        fieldInfo.setFieldName(fieldName);
        fieldInfo.setFieldCode("FIELD001");
        fieldInfo.setLocation("Greenhouse A");
        fieldInfo.setAreaMu(new BigDecimal("12.50"));
        fieldInfo.setSoilType("loam");
        fieldInfo.setDeleted(0);
        return fieldInfo;
    }

    @SuppressWarnings("unchecked")
    private Wrapper<FieldInfo> anyFieldWrapper() {
        return any(Wrapper.class);
    }

    @SuppressWarnings("unchecked")
    private Page<FieldInfo> anyFieldPage() {
        return any(Page.class);
    }
}
