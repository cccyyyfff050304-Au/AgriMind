package com.agrimind.field.vo;

import com.agrimind.field.entity.FieldInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FieldOptionVO {

    private Long id;

    private String fieldName;

    public static FieldOptionVO from(FieldInfo fieldInfo) {
        return new FieldOptionVO(fieldInfo.getId(), fieldInfo.getFieldName());
    }
}
