package com.agrimind.crop.vo;

import com.agrimind.crop.entity.CropInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CropOptionVO {

    private Long id;

    private String cropName;

    public static CropOptionVO from(CropInfo cropInfo) {
        return new CropOptionVO(cropInfo.getId(), cropInfo.getCropName());
    }
}
