package com.agrimind.field.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FieldPageQuery {

    @Min(value = 1, message = "页码必须大于等于1")
    private Long pageNum = 1L;

    @Min(value = 1, message = "每页条数必须大于等于1")
    @Max(value = 100, message = "每页条数不能超过100")
    private Long pageSize = 10L;

    @Size(max = 100, message = "地块名称长度不能超过100个字符")
    private String fieldName;

    @Size(max = 255, message = "地块位置长度不能超过255个字符")
    private String location;

    @Size(max = 50, message = "土壤类型长度不能超过50个字符")
    private String soilType;
}
