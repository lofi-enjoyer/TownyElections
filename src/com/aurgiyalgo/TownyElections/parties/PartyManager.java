package com.aurgiyalgo.TownyElections.parties;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;

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
		if (!_parties.contains(party)) _parties.add(party);
	}
	
	public void removeParty(Party party) {
		_parties.remove(party);
	}
	
	public void removeParty(String partyName) {
		Iterator<Party> iterator = _parties.iterator();
		while (iterator.hasNext()) {
			Party party = iterator.next();
			if (!party.getName().equals(partyName)) continue;
			iterator.remove();
			return;
		}
	}
	
	public void loadData() {
		List<JSONObject> jsonArray = _dataHandler.getDataList("parties");
		if (jsonArray == null) return;
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject currentObject = jsonArray.get(i);
			Party party = _gson.fromJson(currentObject.toJSONString(), Party.PartyType.getPartyType(currentObject.get("partyType").toString()));
//			War war = _gson.fromJson(currentObject.toJSONString(), WarType.getWarType(currentObject.get("_type").toString()).getClassType());
//			war.setupWar();
//			_wars.add(war);
		}
	}

}
