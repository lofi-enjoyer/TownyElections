package com.aurgiyalgo.TownyElections.parties;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.aurgiyalgo.TownyElections.data.DataHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PartyManager {

	private List<Party> _parties;
	private DataHandler _dataHandler;
	private Gson _gson;

	public PartyManager(File dataFolder) {
		_parties = new ArrayList<Party>();
		_dataHandler = new DataHandler(dataFolder, "parties.json");
		_gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
	}

	public void addParty(Party party) {
		if (!_parties.contains(party))
			_parties.add(party);
	}

	public void removeParty(Party party) {
		_parties.remove(party);
	}

	public void removeParty(String partyName) {
		Iterator<Party> iterator = _parties.iterator();
		while (iterator.hasNext()) {
			Party party = iterator.next();
			if (!party.getName().equals(partyName))
				continue;
			iterator.remove();
			return;
		}
	}

	public List<TownParty> getTownParties() {
		List<TownParty> townParties = new ArrayList<TownParty>();
		for (Party party : _parties) {
			if (party instanceof TownParty)
				townParties.add((TownParty) party);
		}
		return null;
	}

	public List<TownParty> getPartiesForTown(String town) {
		List<TownParty> townParties = new ArrayList<TownParty>();
		for (Party party : _parties) {
			if (!(party instanceof TownParty))
				continue;
			if (!((TownParty) party).getTown().getName().equals(town))
				continue;
			townParties.add((TownParty) party);
		}
		return townParties;
	}
	
	public List<NationParty> getPartiesForNation(String nation) {
		List<NationParty> nationParties = new ArrayList<NationParty>();
		for (Party party : _parties) {
			if (!(party instanceof NationParty)) continue;
			if (!((NationParty) party).getNation().getName().equals(nation)) continue;
			nationParties.add((NationParty) party);
		}
		return nationParties;
	}
	
	public TownParty getPlayerTownParty(UUID player) {
		for (Party party : _parties) {
			if (!(party instanceof TownParty)) continue;
			if (party.members.contains(player)) return (TownParty) party;
		}
		return null;
	}
	
	public NationParty getPlayerNationParty(UUID player) {
		for (Party party : _parties) {
			if (!(party instanceof NationParty)) continue;
			if (party.members.contains(player)) return (NationParty) party;
		}
		return null;
	}

	public void loadData() {
		List<JSONObject> jsonArray = _dataHandler.getDataList("parties");
		if (jsonArray == null)
			return;
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject currentObject = jsonArray.get(i);
			Party party = _gson.fromJson(currentObject.toJSONString(),
					Party.PartyType.getPartyType(currentObject.get("partyType").toString()).getClassType());
			party.setup();
			_parties.add(party);
		}
	}

	public void saveData() {
		List<JSONObject> jsonArray = new ArrayList<JSONObject>();
		for (Party party : _parties) {
			try {
				JSONObject jsonObject = (JSONObject) new JSONParser().parse(_gson.toJson(party));
				jsonArray.add(jsonObject);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		_dataHandler.addDataList("parties", jsonArray);
	}

}
