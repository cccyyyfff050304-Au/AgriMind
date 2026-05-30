package com.agrimind.field.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FieldSaveRequest {

    @NotBlank(message = "地块名称不能为空")
    @Size(max = 100, message = "地块名称长度不能超过100个字符")
    private String fieldName;

    @Size(max = 50, message = "地块编码长度不能超过50个字符")
    private String fieldCode;

    @Size(max = 255, message = "地块位置长度不能超过255个字符")
    private String location;

    @DecimalMin(value = "0.01", message = "地块面积必须大于0")
    private BigDecimal areaMu;

    @Size(max = 50, message = "土壤类型长度不能超过50个字符")
    private String soilType;

    @DecimalMin(value = "-180.000000", message = "经度不能小于-180")
    @DecimalMax(value = "180.000000", message = "经度不能大于180")
    private BigDecimal longitude;

    @DecimalMin(value = "-90.000000", message = "纬度不能小于-90")
    @DecimalMax(value = "90.000000", message = "纬度不能大于90")
    private BigDecimal latitude;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
