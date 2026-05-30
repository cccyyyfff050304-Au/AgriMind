package com.agrimind.field.vo;

import com.agrimind.field.entity.FieldInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FieldVO {

    private Long id;

    private String fieldName;

    private String fieldCode;

    private String location;

    private BigDecimal areaMu;

    private String soilType;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public static FieldVO from(FieldInfo fieldInfo) {
        return new FieldVO(
                fieldInfo.getId(),
                fieldInfo.getFieldName(),
                fieldInfo.getFieldCode(),
                fieldInfo.getLocation(),
                fieldInfo.getAreaMu(),
                fieldInfo.getSoilType(),
                fieldInfo.getLongitude(),
                fieldInfo.getLatitude(),
                fieldInfo.getRemark(),
                fieldInfo.getCreateTime(),
                fieldInfo.getUpdateTime()
        );
    }
}
