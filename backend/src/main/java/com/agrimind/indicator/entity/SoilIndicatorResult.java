package com.agrimind.indicator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("soil_indicator_result")
public class SoilIndicatorResult {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long recordId;

    private Long standardId;

    private String indicatorCode;

    private String indicatorName;

    private BigDecimal measuredValue;

    private BigDecimal minValue;

    private BigDecimal maxValue;

    private String unit;

    private String resultLevel;

    private String riskLevel;

    private String resultText;

    private String suggestion;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
