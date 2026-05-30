package com.agrimind.crop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("crop_info")
public class CropInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String cropName;

    private String cropCode;

    private String cropCategory;

    private Integer growthCycleDays;

    private BigDecimal suitablePhMin;

    private BigDecimal suitablePhMax;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
