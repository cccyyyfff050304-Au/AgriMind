package com.agrimind.indicator.enums;

import com.agrimind.common.exception.BusinessException;
import com.agrimind.soil.entity.SoilTestRecord;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum SoilIndicator {

    PH("PH", "ph", "pH", null, SoilTestRecord::getPhValue),
    NITROGEN("NITROGEN", "nitrogen", "有效氮", "mg/kg", SoilTestRecord::getNitrogenMgKg),
    PHOSPHORUS("PHOSPHORUS", "phosphorus", "有效磷", "mg/kg", SoilTestRecord::getPhosphorusMgKg),
    POTASSIUM("POTASSIUM", "potassium", "速效钾", "mg/kg", SoilTestRecord::getPotassiumMgKg),
    ORGANIC_MATTER("ORGANIC_MATTER", "organicMatter", "有机质", "g/kg", SoilTestRecord::getOrganicMatterGKg),
    MOISTURE("MOISTURE", "moisture", "含水率", "%", SoilTestRecord::getMoisturePercent);

    private final String code;
    private final String name;
    private final String label;
    private final String defaultUnit;
    private final Function<SoilTestRecord, BigDecimal> valueGetter;

    SoilIndicator(String code,
                  String name,
                  String label,
                  String defaultUnit,
                  Function<SoilTestRecord, BigDecimal> valueGetter) {
        this.code = code;
        this.name = name;
        this.label = label;
        this.defaultUnit = defaultUnit;
        this.valueGetter = valueGetter;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public String getDefaultUnit() {
        return defaultUnit;
    }

    public BigDecimal getValue(SoilTestRecord record) {
        return valueGetter.apply(record);
    }

    public static SoilIndicator fromName(String indicatorName) {
        return Arrays.stream(values())
                .filter(indicator -> indicator.name.equals(indicatorName))
                .findFirst()
                .orElseThrow(() -> new BusinessException(400, "指标名称只能是: " + allowedNamesText()));
    }

    public static List<String> allowedNames() {
        return Arrays.stream(values()).map(SoilIndicator::getName).toList();
    }

    public static String allowedNamesText() {
        return Arrays.stream(values())
                .map(SoilIndicator::getName)
                .collect(Collectors.joining(", "));
    }
}
