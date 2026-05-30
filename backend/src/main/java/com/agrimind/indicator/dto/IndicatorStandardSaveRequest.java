package com.agrimind.indicator.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class IndicatorStandardSaveRequest {

    @NotNull(message = "作物ID不能为空")
    @Min(value = 1, message = "作物ID必须大于0")
    private Long cropId;

    @NotBlank(message = "指标名称不能为空")
    @Size(max = 100, message = "指标名称长度不能超过100个字符")
    private String indicatorName;

    @NotNull(message = "标准最小值不能为空")
    @DecimalMin(value = "0.0000", message = "标准最小值不能小于0")
    private BigDecimal minValue;

    @NotNull(message = "标准最大值不能为空")
    @DecimalMin(value = "0.0000", message = "标准最大值不能小于0")
    private BigDecimal maxValue;

    @Size(max = 30, message = "单位长度不能超过30个字符")
    private String unit;

    @Size(max = 500, message = "建议说明长度不能超过500个字符")
    private String suggestion;

    @Min(value = 0, message = "状态只能是0或1")
    @Max(value = 1, message = "状态只能是0或1")
    private Integer status = 1;
}
