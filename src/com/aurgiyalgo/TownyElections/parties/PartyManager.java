package com.aurgiyalgo.TownyElections.parties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.data.DataHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PartyManager {

	private final List<Party> parties;
	private final DataHandler dataHandler;
	private final Gson gson;

	public PartyManager() {
		parties = new ArrayList<>();
		dataHandler = new DataHandler(TownyElections.getInstance().getDataFolder(), "parties.json");
		gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
	}

	public void addParty(Party party) {
		if (!parties.contains(party))
			parties.add(party);
	}

	public void removeParty(Party party) {
		parties.remove(party);
	}

	public List<TownParty> getTownParties() {
		return parties.stream()
				.filter(TownParty.class::isInstance)
				.map(TownParty.class::cast)
				.collect(Collectors.toList());
	}

	public List<NationParty> getNationParties() {
		return parties.stream()
				.filter(NationParty.class::isInstance)
				.map(NationParty.class::cast)
				.collect(Collectors.toList());
	}

	public List<TownParty> getPartiesForTown(String townName) {
		return parties.stream()
				.filter(TownParty.class::isInstance)
				.map(TownParty.class::cast)
				.filter(townParty -> townParty.getTown().getName().equals(townName))
				.collect(Collectors.toList());
	}
	
	public List<NationParty> getPartiesForNation(String nationName) {
		return parties.stream()
				.filter(NationParty.class::isInstance)
				.map(NationParty.class::cast)
				.filter(nationParty -> nationParty.getNation().getName().equals(nationName))
				.collect(Collectors.toList());
	}
	
	public TownParty getPlayerTownParty(UUID playerUuid) {
		return parties.stream()
				.filter(TownParty.class::isInstance)
				.map(TownParty.class::cast)
				.filter(townParty -> townParty.getMembers().contains(playerUuid))
				.findFirst().orElse(null);
	}

	public NationParty getPlayerNationParty(UUID playerUuid) {
		return parties.stream()
				.filter(NationParty.class::isInstance)
				.map(NationParty.class::cast)
				.filter(nationParty -> nationParty.getMembers().contains(playerUuid))
				.findFirst().orElse(null);
	}

	public void loadData() {
		List<JSONObject> jsonArray = dataHandler.getDataList("parties");
		if (jsonArray == null)
			return;
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject currentObject = jsonArray.get(i);
			Party party = gson.fromJson(currentObject.toJSONString(),
					Party.PartyType.getPartyType(currentObject.get("partyType").toString()).getClassType());
			party.setup();
			parties.add(party);
		}
	}

	public void saveData() {
		List<JSONObject> jsonArray = new ArrayList<JSONObject>();
		for (Party party : parties) {
			try {
				JSONObject jsonObject = (JSONObject) new JSONParser().parse(gson.toJson(party));
				jsonArray.add(jsonObject);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		dataHandler.addDataList("parties", jsonArray);
		
		dataHandler.saveData();
	}

}
