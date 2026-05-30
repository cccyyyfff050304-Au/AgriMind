package com.agrimind.indicator.vo;

import com.agrimind.indicator.entity.SoilIndicatorStandard;
import com.agrimind.indicator.enums.SoilIndicator;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class IndicatorStandardVO {

    private Long id;

    private Long cropId;

    private String cropName;

    private String indicatorName;

    private String indicatorLabel;

    private BigDecimal minValue;

    private BigDecimal maxValue;

    private String unit;

    private String suggestion;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public static IndicatorStandardVO from(SoilIndicatorStandard standard, String cropName) {
        SoilIndicator indicator = SoilIndicator.fromName(standard.getIndicatorName());
        return new IndicatorStandardVO(
                standard.getId(),
                standard.getCropId(),
                cropName,
                standard.getIndicatorName(),
                indicator.getLabel(),
                standard.getMinValue(),
                standard.getMaxValue(),
                standard.getUnit(),
                standard.getSuggestion(),
                standard.getStatus(),
                standard.getCreateTime(),
                standard.getUpdateTime()
        );
    }
}
