package com.agrimind.soil.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SoilRecordSaveRequest {

    @NotBlank(message = "检测编号不能为空")
    @Size(max = 64, message = "检测编号长度不能超过64个字符")
    private String testNo;

    @NotNull(message = "地块ID不能为空")
    @Min(value = 1, message = "地块ID必须大于0")
    private Long fieldId;

    @Min(value = 1, message = "作物ID必须大于0")
    private Long cropId;

    @NotNull(message = "检测时间不能为空")
    private LocalDateTime testTime;

    @DecimalMin(value = "0.00", message = "采样深度不能小于0")
    private BigDecimal sampleDepthCm;

    @DecimalMin(value = "0.00", message = "pH值不能小于0")
    @DecimalMax(value = "14.00", message = "pH值不能大于14")
    private BigDecimal ph;

    @DecimalMin(value = "0.00", message = "有机质不能小于0")
    private BigDecimal organicMatter;

    @DecimalMin(value = "0.00", message = "有效氮不能小于0")
    private BigDecimal nitrogen;

    @DecimalMin(value = "0.00", message = "有效磷不能小于0")
    private BigDecimal phosphorus;

    @DecimalMin(value = "0.00", message = "速效钾不能小于0")
    private BigDecimal potassium;

    @DecimalMin(value = "0.00", message = "含水率不能小于0")
    @DecimalMax(value = "100.00", message = "含水率不能大于100")
    private BigDecimal moisture;

    @DecimalMin(value = "-100.00", message = "土壤温度不能小于-100")
    @DecimalMax(value = "100.00", message = "土壤温度不能大于100")
    private BigDecimal temperature;

    @DecimalMin(value = "0.00", message = "电导率不能小于0")
    private BigDecimal conductivity;

    @Size(max = 50, message = "检测人员长度不能超过50个字符")
    private String testerName;

    @Pattern(regexp = "MANUAL|DEVICE|IMPORT", message = "数据来源只能是 MANUAL、DEVICE 或 IMPORT")
    private String dataSource = "MANUAL";

    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
