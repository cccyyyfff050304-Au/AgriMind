package com.agrimind.indicator.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SoilEvaluationVO {

    private Long recordId;

    private String fieldName;

    private String cropName;

    private String riskLevel;

    private List<IndicatorResultVO> results;
}
