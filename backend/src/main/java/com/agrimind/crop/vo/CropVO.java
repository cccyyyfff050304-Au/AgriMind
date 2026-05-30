package com.agrimind.crop.vo;

import com.agrimind.crop.entity.CropInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CropVO {

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

    public static CropVO from(CropInfo cropInfo) {
        return new CropVO(
                cropInfo.getId(),
                cropInfo.getCropName(),
                cropInfo.getCropCode(),
                cropInfo.getCropCategory(),
                cropInfo.getGrowthCycleDays(),
                cropInfo.getSuitablePhMin(),
                cropInfo.getSuitablePhMax(),
                cropInfo.getRemark(),
                cropInfo.getCreateTime(),
                cropInfo.getUpdateTime()
        );
    }
}
