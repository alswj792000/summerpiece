package com.summerroot.summerpiece.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class BooleanToYNConverter implements AttributeConverter<Boolean, String> {

    // Boolean 값을 Y/N으로 변경
    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        return (attribute != null && attribute) ? "Y" : "N";
    }

    // Y/N을 Boolean 값으로 변경
    @Override
    public Boolean convertToEntityAttribute(String yn) {
        return "Y".equals(yn);
    }
}
