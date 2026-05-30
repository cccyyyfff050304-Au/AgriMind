package com.agrimind.indicator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("soil_indicator_standard")
public class SoilIndicatorStandard {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String indicatorCode;

    private String indicatorName;

    private Long cropId;

    private String soilType;

    private String levelCode;

    private BigDecimal minValue;

    private BigDecimal maxValue;

    private String unit;

    private String suggestion;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
