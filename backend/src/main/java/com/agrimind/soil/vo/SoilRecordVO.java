package com.agrimind.soil.vo;

import com.agrimind.soil.entity.SoilTestRecord;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SoilRecordVO {

    public static final String DEFAULT_RISK_LEVEL = "UNKNOWN";

    private Long id;

    private String testNo;

    private Long fieldId;

    private String fieldName;

    private Long cropId;

    private String cropName;

    private BigDecimal ph;

    private BigDecimal nitrogen;

    private BigDecimal phosphorus;

    private BigDecimal potassium;

    private BigDecimal organicMatter;

    private BigDecimal moisture;

    private BigDecimal temperature;

    private BigDecimal sampleDepthCm;

    private BigDecimal conductivity;

    private String testerName;

    private String dataSource;

    private String riskLevel;

    private LocalDateTime testTime;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public static SoilRecordVO from(SoilTestRecord record, String fieldName, String cropName) {
        return new SoilRecordVO(
                record.getId(),
                record.getTestNo(),
                record.getFieldId(),
                fieldName,
                record.getCropId(),
                cropName,
                record.getPhValue(),
                record.getNitrogenMgKg(),
                record.getPhosphorusMgKg(),
                record.getPotassiumMgKg(),
                record.getOrganicMatterGKg(),
                record.getMoisturePercent(),
                record.getTemperatureCelsius(),
                record.getSampleDepthCm(),
                record.getConductivityUsCm(),
                record.getTesterName(),
                record.getDataSource(),
                DEFAULT_RISK_LEVEL,
                record.getSampleTime(),
                record.getRemark(),
                record.getCreateTime(),
                record.getUpdateTime()
        );
    }
}
