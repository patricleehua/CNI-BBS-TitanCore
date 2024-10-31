package com.titancore.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum PointsStatus implements IEnum<String> {
    EARNED("earned"),
    USED("used"),
    EXPIRED("expired"),
    REVOKED("revoked");
    private final String value;
    public static PointsStatus valueOfAll(String value) {
        for (PointsStatus pointsStatus : PointsStatus.values()) {
            if(Objects.equals(value,pointsStatus.getValue())){
                return pointsStatus;
            }

        }
        return null;
    }

    public static PointsStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (PointsStatus pointsStatus : PointsStatus.values()) {
            if (pointsStatus.value.equalsIgnoreCase(value)) {
                return pointsStatus;
            }
        }
        return null;
    }
}
