package com.aurgiyalgo.TownyElections.government;

import java.util.Arrays;

public enum GovernmentType {

    MONARCHY("MONARCHY");

    private String name;

    GovernmentType(String name) {
        this.name = name;
    }

    public static GovernmentType getByName(String name) {
        return Arrays.stream(values())
                .filter(governmentType -> governmentType.name.equalsIgnoreCase(name))
                .findFirst()
                .orElse(GovernmentType.MONARCHY);
    }

}
