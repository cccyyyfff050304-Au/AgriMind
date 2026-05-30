package com.agrimind.indicator.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class IndicatorStandardPageQuery {

    @Min(value = 1, message = "页码必须大于等于1")
    private Long pageNum = 1L;

    @Min(value = 1, message = "每页条数必须大于等于1")
    @Max(value = 100, message = "每页条数不能超过100")
    private Long pageSize = 10L;

    @Min(value = 1, message = "作物ID必须大于0")
    private Long cropId;

    @Size(max = 100, message = "指标名称长度不能超过100个字符")
    private String indicatorName;
}
