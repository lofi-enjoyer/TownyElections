package me.lofienjoyer.TownyElections.government;

import com.google.gson.annotations.Expose;

import java.util.UUID;

public class GovernmentData {

    @Expose
    private UUID uuid;
    @Expose
    private GovernmentType governmentType;

    public GovernmentData(UUID uuid, GovernmentType governmentType) {
        this.uuid = uuid;
        this.governmentType = governmentType;
    }

    public UUID getUuid() {
        return uuid;
    }

    public GovernmentType getGovernmentType() {
        return governmentType;
    }

}
