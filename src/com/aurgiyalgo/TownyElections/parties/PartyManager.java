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

	private List<Party> parties;
	private DataHandler dataHandler;
	private Gson gson;

	public PartyManager() {
		parties = new ArrayList<Party>();
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

	public void removeParty(String partyName) {
		parties.removeIf(party -> party.getName().equals(partyName));
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

	public List<TownParty> getPartiesForTown(String town) {
		return parties.stream()
				.filter(TownParty.class::isInstance)
				.map(TownParty.class::cast)
				.filter(townParty -> townParty.getTown().getName().equals(town))
				.collect(Collectors.toList());
	}
	
	public List<NationParty> getPartiesForNation(String nation) {
		return parties.stream()
				.filter(NationParty.class::isInstance)
				.map(NationParty.class::cast)
				.filter(nationParty -> nationParty.getNation().getName().equals(nation))
				.collect(Collectors.toList());
	}
	
	public TownParty getPlayerTownParty(UUID player) {
		return parties.stream()
				.filter(TownParty.class::isInstance)
				.map(TownParty.class::cast)
				.filter(townParty -> townParty.getMembers().contains(player))
				.findFirst().orElse(null);
	}
	
	public NationParty getPlayerNationParty(UUID player) {
		return parties.stream()
				.filter(NationParty.class::isInstance)
				.map(NationParty.class::cast)
				.filter(nationParty -> nationParty.getMembers().contains(player))
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
