package com.resturant.management.ResturantManagementSystem.enums;


public enum YesNo {
    YES("Y"),
    NO("N");

    private final String value;

    YesNo(String value) {
        this.value = value;
    }

    public static YesNo fromValue(String value) {
        for (YesNo yesNo : YesNo.values()) {
            if (yesNo.value.equals(value)) {
                return yesNo;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

    public String getValue() {
        return value;
    }
}

