package com.aurgiyalgo.TownyElections.government;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.data.DataHandler;
import com.aurgiyalgo.TownyElections.parties.Party;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;
import java.util.stream.Collectors;

public class GovernmentManager {

    private List<GovernmentData> governmentDataList;
    private DataHandler dataHandler;
    private final Gson gson;

    public GovernmentManager() {
        governmentDataList = new ArrayList<>();
        dataHandler = new DataHandler(TownyElections.getInstance().getDataFolder(), "governments.json");
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }

    public void addGovernmentData(Nation nation) {
        governmentDataList.add(new GovernmentData(nation.getUUID(), GovernmentType.MONARCHY));
    }

    public void removeGovernmentData(UUID uuid) {
        governmentDataList = governmentDataList.stream().
                filter(governmentData -> !governmentData.getUuid().equals(uuid))
                .collect(Collectors.toList());
    }

    public void loadData() {
        List<JSONObject> jsonArray = dataHandler.getDataList("govdata");

        if (jsonArray == null || jsonArray.size() == 0) {
            for (Nation nation : TownyUniverse.getInstance().getNations()) {
                governmentDataList.add(new GovernmentData(nation.getUUID(), GovernmentType.MONARCHY));
            }
            return;
        }

        for (JSONObject currentObject : jsonArray) {
            GovernmentData governmentData = gson.fromJson(currentObject.toJSONString(), GovernmentData.class);
            governmentDataList.add(governmentData);
        }
    }

    public void saveData() {
        List<JSONObject> jsonArray = new ArrayList<>();
        for (GovernmentData governmentData : governmentDataList) {
            try {
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(gson.toJson(governmentData));
                jsonArray.add(jsonObject);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        dataHandler.addDataList("govdata", jsonArray);

        dataHandler.saveData();
    }

    public List<GovernmentData> getGovernmentDataList() {
        return governmentDataList;
    }
}
