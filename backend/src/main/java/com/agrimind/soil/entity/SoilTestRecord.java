package com.agrimind.soil.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("soil_test_record")
public class SoilTestRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String testNo;

    private Long fieldId;

    private Long cropId;

    private LocalDateTime sampleTime;

    private BigDecimal sampleDepthCm;

    private BigDecimal phValue;

    private BigDecimal organicMatterGKg;

    private BigDecimal nitrogenMgKg;

    private BigDecimal phosphorusMgKg;

    private BigDecimal potassiumMgKg;

    private BigDecimal moisturePercent;

    private BigDecimal temperatureCelsius;

    private BigDecimal conductivityUsCm;

    private String testerName;

    private String dataSource;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
