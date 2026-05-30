package com.agrimind.crop.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CropSaveRequest {

    @NotBlank(message = "作物名称不能为空")
    @Size(max = 100, message = "作物名称长度不能超过100个字符")
    private String cropName;

    @Size(max = 50, message = "作物编码长度不能超过50个字符")
    private String cropCode;

    @Size(max = 50, message = "作物分类长度不能超过50个字符")
    private String cropCategory;

    @Min(value = 1, message = "生长周期天数必须大于0")
    @Max(value = 10000, message = "生长周期天数不能超过10000")
    private Integer growthCycleDays;

    @DecimalMin(value = "0.00", message = "适宜pH最小值不能小于0")
    @DecimalMax(value = "14.00", message = "适宜pH最小值不能大于14")
    private BigDecimal suitablePhMin;

    @DecimalMin(value = "0.00", message = "适宜pH最大值不能小于0")
    @DecimalMax(value = "14.00", message = "适宜pH最大值不能大于14")
    private BigDecimal suitablePhMax;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
