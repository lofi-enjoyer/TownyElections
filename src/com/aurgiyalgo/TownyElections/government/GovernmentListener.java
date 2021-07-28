package com.aurgiyalgo.TownyElections.government;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.palmergames.bukkit.towny.event.DeleteNationEvent;
import com.palmergames.bukkit.towny.event.NewNationEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GovernmentListener implements Listener {

    private GovernmentManager governmentManager;

    public GovernmentListener() {
        governmentManager = TownyElections.getInstance().getGovernmentManager();
    }

    @EventHandler
    public void onNationCreate(NewNationEvent event) {
        governmentManager.addGovernmentData(event.getNation());
    }

    @EventHandler
    public void onNationRemove(DeleteNationEvent event) {
        governmentManager.removeGovernmentData(event.getNationUUID());
    }

}
