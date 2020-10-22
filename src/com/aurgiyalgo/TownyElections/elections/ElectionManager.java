package com.aurgiyalgo.TownyElections.elections;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.data.DataHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;

public class ElectionManager {

	private List<TownElection> townElections;
	private List<NationElection> nationElections;
	private DataHandler dataHandler;
	private Gson gson;

	public ElectionManager(TownyElections instance, File dataFolder) {
		townElections = new ArrayList<TownElection>();
		nationElections = new ArrayList<NationElection>();
		
		dataHandler = new DataHandler(dataFolder, "elections.json");
		gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

		new BukkitRunnable() {

			@Override
			public void run() {
				Iterator<TownElection> it1 = townElections.iterator();
				while (it1.hasNext()) {
					TownElection e = it1.next();
					if (System.currentTimeMillis() >= e.getEndTime()) {
						e.finishElection();
						it1.remove();
					}
				}
				Iterator<NationElection> it2 = nationElections.iterator();
				while (it2.hasNext()) {
					NationElection e = it2.next();
					if (System.currentTimeMillis() >= e.getEndTime()) {
						e.finishElection();
						it2.remove();
					}
				}
			}

		}.runTaskTimer(instance, 0, 100);
	}

	public void loadElections() {
		List<JSONObject> jsonArray;
		jsonArray = dataHandler.getDataList("townElections");
		if (jsonArray == null) return;
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject currentObject = jsonArray.get(i);
			TownElection election = gson.fromJson(currentObject.toJSONString(), TownElection.class);
			election.setup();
			townElections.add(election);
		}

		jsonArray = dataHandler.getDataList("nationElections");
		if (jsonArray == null) return;
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject currentObject = jsonArray.get(i);
			NationElection election = gson.fromJson(currentObject.toJSONString(), NationElection.class);
			election.setup();
			nationElections.add(election);
		}
		
//		File townsFile = new File(dataFolder, "elections.json");
//		if (townsFile.exists()) {
//			JSONParser parser = new JSONParser();
//
//			Object obj = parser.parse(new FileReader(townsFile));
//			JSONObject jsonFile = (JSONObject) obj;
//
//			JSONArray electionsArray = (JSONArray) jsonFile.get("elections");
//
//			for (int i = 0; i < electionsArray.size(); i++) {
//				JSONObject election = (JSONObject) electionsArray.get(i);
//
//				long endTime = (long) election.get("endtime");
//				Town town = TownyUniverse.getInstance().getDataSource().getTown((String) election.get("town"));
//				TownElection e = new TownElection(endTime, town);
//				
//				JSONArray candidates = (JSONArray) election.get("votes");
//				for (int j = 0; j < candidates.size(); j++) {
//					e.addCandidate(UUID.fromString((String) candidates.get(j)));
//				}
//				
//				JSONArray votes = (JSONArray) election.get("votes");
//				for (int j = 0; j < votes.size(); j++) {
//					JSONObject vote = (JSONObject) votes.get(j);
//					UUID key = UUID.fromString((String) vote.get(vote.keySet().toArray()[0]));
//					UUID candidate = (UUID) vote.get(key.toString());
//					e.addVote(key, candidate);
//				}
//				_townElections.add(e);
//			}
//		}
//
//		File nationsFile = new File(dataFolder, "nationElections.json");
//		if (nationsFile.exists()) {
//			JSONParser parser = new JSONParser();
//
//			Object obj = parser.parse(new FileReader(nationsFile));
//			JSONObject jsonFile = (JSONObject) obj;
//
//			JSONArray electionsArray = (JSONArray) jsonFile.get("elections");
//
//			for (int i = 0; i < electionsArray.size(); i++) {
//				JSONObject election = (JSONObject) electionsArray.get(i);
//
//				long endTime = (long) election.get("endtime");
//				Nation nation = TownyUniverse.getInstance().getDataSource().getNation((String) election.get("nation"));
//				NationElection e = new NationElection(nation, endTime);
//				
//				JSONArray candidates = (JSONArray) election.get("candidates");
//				for (int j = 0; j < candidates.size(); j++) {
//					e.addCandidate(UUID.fromString((String) candidates.get(j)));
//				}
//				
//				JSONArray votes = (JSONArray) election.get("votes");
//				for (int j = 0; j < votes.size(); j++) {
//					JSONObject vote = (JSONObject) votes.get(j);
//					UUID key = UUID.fromString((String) vote.get(vote.keySet().toArray()[0]));
//					UUID candidate = (UUID) vote.get(key.toString());
//					e.addVote(key, candidate);
//				}
//				nationElections.add(e);
//			}
//		}
//		
//
//
//		File townDecisionsFile = new File(dataFolder, "townDecisions.json");
//		if (townDecisionsFile.exists()) {
//			JSONParser parser = new JSONParser();
//
//			Object obj = parser.parse(new FileReader(townDecisionsFile));
//			JSONObject jsonFile = (JSONObject) obj;
//
//			JSONArray electionsArray = (JSONArray) jsonFile.get("decisions");
//
//			for (int i = 0; i < electionsArray.size(); i++) {
//				JSONObject election = (JSONObject) electionsArray.get(i);
//
//				long endTime = (long) election.get("endtime");
//				String type = (String) election.get("type");
//				Town town = TownyUniverse.getInstance().getDataSource().getTown((String) election.get("town"));
//				TownDecision d = new TownDecision(town, endTime, type);
//				
//				JSONArray votes = (JSONArray) election.get("votes");
//				for (int j = 0; j < votes.size(); j++) {
//					JSONObject vote = (JSONObject) votes.get(j);
//					UUID key = UUID.fromString((String) vote.get(vote.keySet().toArray()[0]));
//					boolean candidate = (boolean) vote.get(key.toString());
//					d.addVote(key, candidate);
//				}
//				townDecisions.add(d);
//			}
//		}
		
	}

	public void saveElections() {
		List<JSONObject> jsonArray;
		jsonArray = new ArrayList<JSONObject>();
		for (TownElection w : townElections) {
			try {
				JSONObject jsonObject = (JSONObject) new JSONParser().parse(gson.toJson(w));
				jsonArray.add(jsonObject);
			} catch (ParseException e) {e.printStackTrace();}
		}
		dataHandler.addDataList("townElections", jsonArray);

		jsonArray = new ArrayList<JSONObject>();
		for (NationElection w : nationElections) {
			try {
				JSONObject jsonObject = (JSONObject) new JSONParser().parse(gson.toJson(w));
				jsonArray.add(jsonObject);
			} catch (ParseException e) {e.printStackTrace();}
		}
		dataHandler.addDataList("nationElections", jsonArray);
		
		dataHandler.saveData();
		
//		if (!dataFolder.exists()) {
//			dataFolder.mkdir();
//		}
//		File townsFile = new File(dataFolder, "elections.json");
//		if (townsFile.exists()) {
//			townsFile.delete();
//			townsFile.createNewFile();
//
//			JSONObject jsonFile = new JSONObject();
//
//			JSONArray electionsArray = new JSONArray();
//
//			for (int i = 0; i < _townElections.size(); i++) {
//				JSONObject electionObject = new JSONObject();
//				electionObject.put("endtime", _townElections.get(i).getEndTime());
//				electionObject.put("town", _townElections.get(i).getTown().getName());
//				
//				JSONArray candidates = new JSONArray();
//				for (UUID player : _townElections.get(i).getCandidates()) {
//					candidates.add(player);
//				}
//				electionObject.put("candidates", candidates);
//				
//				JSONArray votes = new JSONArray();
//				for (Map.Entry<UUID, UUID> entry : _townElections.get(i).getVotes().entrySet()) {
//					JSONObject vote = new JSONObject();
//					vote.put(entry.getKey().toString(), entry.getValue().toString());
//					votes.add(vote);
//				}
//				electionObject.put("votes", votes);
//
//				electionsArray.add(electionObject);
//			}
//
//			jsonFile.put("elections", electionsArray);
//
//			FileWriter fw = new FileWriter(townsFile);
//			fw.write(jsonFile.toString());
//			fw.close();
//		}
//		
//
//		File nationsFile = new File(dataFolder, "nationElections.json");
//		if (nationsFile.exists()) {
//			nationsFile.delete();
//			nationsFile.createNewFile();
//
//			JSONObject jsonFile = new JSONObject();
//
//			JSONArray electionsArray = new JSONArray();
//
//			for (int i = 0; i < nationElections.size(); i++) {
//				JSONObject electionObject = new JSONObject();
//				electionObject.put("endtime", nationElections.get(i).getEndTime());
//				electionObject.put("nation", nationElections.get(i).getNation().getName());
//				
//				JSONArray candidates = new JSONArray();
//				for (UUID player : nationElections.get(i).getCandidates()) {
//					candidates.add(player);
//				}
//				electionObject.put("candidates", candidates);
//				
//				JSONArray votes = new JSONArray();
//				for (Map.Entry<UUID, UUID> entry : nationElections.get(i).getVotes().entrySet()) {
//					JSONObject vote = new JSONObject();
//					vote.put(entry.getKey().toString(), entry.getValue().toString());
//					votes.add(vote);
//				}
//				electionObject.put("votes", votes);
//
//				electionsArray.add(electionObject);
//			}
//
//			jsonFile.put("elections", electionsArray);
//
//			FileWriter fw = new FileWriter(nationsFile);
//			fw.write(jsonFile.toString());
//			fw.close();
//		}
//		
//
//		File townDecisionsFile = new File(dataFolder, "townDecisions.json");
//		if (townDecisionsFile.exists()) {
//			townDecisionsFile.delete();
//			townDecisionsFile.createNewFile();
//
//			JSONObject jsonFile = new JSONObject();
//
//			JSONArray electionsArray = new JSONArray();
//
//			for (int i = 0; i < townDecisions.size(); i++) {
//				JSONObject electionObject = new JSONObject();
//				electionObject.put("endtime", townDecisions.get(i).getEndTime());
//				electionObject.put("town", townDecisions.get(i).getTown().getName());
//				
//				JSONArray votes = new JSONArray();
//				for (Map.Entry<UUID, Boolean> entry : townDecisions.get(i).getVotes().entrySet()) {
//					JSONObject vote = new JSONObject();
//					vote.put(entry.getKey().toString(), entry.getValue());
//					votes.add(vote);
//				}
//				electionObject.put("votes", votes);
//				electionObject.put("type", townDecisions.get(i).getType());
//
//				electionsArray.add(electionObject);
//			}
//
//			jsonFile.put("decisions", electionsArray);
//
//			FileWriter fw = new FileWriter(townDecisionsFile);
//			fw.write(jsonFile.toString());
//			fw.close();
//		}
	}

	public void removeTownElection(TownElection e) {
		e.finishElection();
		townElections.remove(e);
	}

	public void removeNationElection(NationElection e) {
		e.finishElection();
		nationElections.remove(e);
	}

	public void addTownElection(TownElection e) {
		townElections.add(e);
	}

	public void addNationElection(NationElection e) {
		nationElections.add(e);
	}

	public TownElection getTownElection(Player p) {
		Town t;
		try {
			t = TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown();
		} catch (NotRegisteredException e1) {
			return null;
		}
		for (TownElection e : townElections) {
			if (e.getTown() == t) {
				return e;
			}
		}
		return null;
	}
	
	public NationElection getNationElection(Player p) {
		Nation n;
		try {
			n = TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown().getNation();
		} catch (NotRegisteredException e) {
			return null;
		}
		for (NationElection e : nationElections) {
			if (e.getNation() == n) {
				return e;
			}
		}
		return null;
	}

}
