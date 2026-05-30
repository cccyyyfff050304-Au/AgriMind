package com.agrimind.crop.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CropPageQuery {

    @Min(value = 1, message = "页码必须大于等于1")
    private Long pageNum = 1L;

    @Min(value = 1, message = "每页条数必须大于等于1")
    @Max(value = 100, message = "每页条数不能超过100")
    private Long pageSize = 10L;

    @Size(max = 100, message = "作物名称长度不能超过100个字符")
    private String cropName;

    @Size(max = 50, message = "生长阶段长度不能超过50个字符")
    private String growthStage;
}
