package com.agrimind.indicator.vo;

import com.agrimind.indicator.entity.SoilIndicatorResult;
import com.agrimind.indicator.enums.SoilIndicator;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class IndicatorResultVO {

    private String indicatorName;

    private String indicatorLabel;

    private BigDecimal value;

    private BigDecimal minValue;

    private BigDecimal maxValue;

    private String unit;

    private String resultStatus;

    private String riskLevel;

    private String message;

    public static IndicatorResultVO from(SoilIndicatorResult result) {
        SoilIndicator indicator = SoilIndicator.fromName(result.getIndicatorName());
        return new IndicatorResultVO(
                result.getIndicatorName(),
                indicator.getLabel(),
                result.getMeasuredValue(),
                result.getMinValue(),
                result.getMaxValue(),
                result.getUnit(),
                result.getResultLevel(),
                result.getRiskLevel(),
                result.getResultText()
        );
    }
}
