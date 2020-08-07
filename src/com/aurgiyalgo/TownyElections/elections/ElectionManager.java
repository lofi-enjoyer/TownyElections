package com.aurgiyalgo.TownyElections.elections;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;

public class ElectionManager {

	private TownyElections instance;
	private List<TownElection> townElections;
	private List<NationElection> nationElections;
	private List<TownDecision> townDecisions;
	private List<NationDecision> nationDecisions;

	public ElectionManager(TownyElections instance) {
		this.instance = instance;

		townElections = new ArrayList<TownElection>();
		nationElections = new ArrayList<NationElection>();
		townDecisions = new ArrayList<TownDecision>();
		nationDecisions = new ArrayList<NationDecision>();

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
				Iterator<TownDecision> it3 = townDecisions.iterator();
				while (it3.hasNext()) {
					TownDecision d = it3.next();
					if (System.currentTimeMillis() >= d.getEndTime()) {
						d.finishDecision();
						it3.remove();
					}
				}
				Iterator<NationDecision> it4 = nationDecisions.iterator();
				while (it4.hasNext()) {
					NationDecision d = it4.next();
					if (System.currentTimeMillis() >= d.getEndTime()) {
						d.finishDecision();
						it4.remove();
					}
				}
			}

		}.runTaskTimer(instance, 0, 100);
	}

	public void loadElections(File dataFolder) throws Exception {
		if (!dataFolder.exists()) {
			return;
		}
		File townsFile = new File(dataFolder, "elections.json");
		if (townsFile.exists()) {
			JSONParser parser = new JSONParser();

			Object obj = parser.parse(new FileReader(townsFile));
			JSONObject jsonFile = (JSONObject) obj;

			JSONArray electionsArray = (JSONArray) jsonFile.get("elections");

			for (int i = 0; i < electionsArray.size(); i++) {
				JSONObject election = (JSONObject) electionsArray.get(i);

				long endTime = (long) election.get("endtime");
				Town town = TownyUniverse.getInstance().getDataSource().getTown((String) election.get("town"));
				TownElection e = new TownElection(endTime, town);
				
				JSONArray candidates = (JSONArray) election.get("votes");
				for (int j = 0; j < candidates.size(); j++) {
					e.addCandidate(UUID.fromString((String) candidates.get(j)));
				}
				
				JSONArray votes = (JSONArray) election.get("votes");
				for (int j = 0; j < votes.size(); j++) {
					JSONObject vote = (JSONObject) votes.get(j);
					UUID key = UUID.fromString((String) vote.get(vote.keySet().toArray()[0]));
					UUID candidate = (UUID) vote.get(key.toString());
					e.addVote(key, candidate);
				}
				townElections.add(e);
			}
		}

		File nationsFile = new File(dataFolder, "nationElections.json");
		if (nationsFile.exists()) {
			JSONParser parser = new JSONParser();

			Object obj = parser.parse(new FileReader(nationsFile));
			JSONObject jsonFile = (JSONObject) obj;

			JSONArray electionsArray = (JSONArray) jsonFile.get("elections");

			for (int i = 0; i < electionsArray.size(); i++) {
				JSONObject election = (JSONObject) electionsArray.get(i);

				long endTime = (long) election.get("endtime");
				Nation nation = TownyUniverse.getInstance().getDataSource().getNation((String) election.get("nation"));
				NationElection e = new NationElection(nation, endTime);
				
				JSONArray candidates = (JSONArray) election.get("candidates");
				for (int j = 0; j < candidates.size(); j++) {
					e.addCandidate(UUID.fromString((String) candidates.get(j)));
				}
				
				JSONArray votes = (JSONArray) election.get("votes");
				for (int j = 0; j < votes.size(); j++) {
					JSONObject vote = (JSONObject) votes.get(j);
					UUID key = UUID.fromString((String) vote.get(vote.keySet().toArray()[0]));
					UUID candidate = (UUID) vote.get(key.toString());
					e.addVote(key, candidate);
				}
				nationElections.add(e);
			}
		}
		


		File townDecisionsFile = new File(dataFolder, "townDecisions.json");
		if (townDecisionsFile.exists()) {
			JSONParser parser = new JSONParser();

			Object obj = parser.parse(new FileReader(townDecisionsFile));
			JSONObject jsonFile = (JSONObject) obj;

			JSONArray electionsArray = (JSONArray) jsonFile.get("decisions");

			for (int i = 0; i < electionsArray.size(); i++) {
				JSONObject election = (JSONObject) electionsArray.get(i);

				long endTime = (long) election.get("endtime");
				String type = (String) election.get("type");
				Town town = TownyUniverse.getInstance().getDataSource().getTown((String) election.get("town"));
				TownDecision d = new TownDecision(town, endTime, type);
				
				JSONArray votes = (JSONArray) election.get("votes");
				for (int j = 0; j < votes.size(); j++) {
					JSONObject vote = (JSONObject) votes.get(j);
					UUID key = UUID.fromString((String) vote.get(vote.keySet().toArray()[0]));
					boolean candidate = (boolean) vote.get(key.toString());
					d.addVote(key, candidate);
				}
				townDecisions.add(d);
			}
		}
		
	}

	public void saveElections(File dataFolder) throws Exception {
		if (!dataFolder.exists()) {
			dataFolder.mkdir();
		}
		File townsFile = new File(dataFolder, "elections.json");
		if (townsFile.exists()) {
			townsFile.delete();
			townsFile.createNewFile();

			JSONObject jsonFile = new JSONObject();

			JSONArray electionsArray = new JSONArray();

			for (int i = 0; i < townElections.size(); i++) {
				JSONObject electionObject = new JSONObject();
				electionObject.put("endtime", townElections.get(i).getEndTime());
				electionObject.put("town", townElections.get(i).getTown().getName());
				
				JSONArray candidates = new JSONArray();
				for (UUID player : townElections.get(i).getCandidates()) {
					candidates.add(player);
				}
				electionObject.put("candidates", candidates);
				
				JSONArray votes = new JSONArray();
				for (Map.Entry<UUID, UUID> entry : townElections.get(i).getVotes().entrySet()) {
					JSONObject vote = new JSONObject();
					vote.put(entry.getKey().toString(), entry.getValue().toString());
					votes.add(vote);
				}
				electionObject.put("votes", votes);

				electionsArray.add(electionObject);
			}

			jsonFile.put("elections", electionsArray);

			FileWriter fw = new FileWriter(townsFile);
			fw.write(jsonFile.toString());
			fw.close();
		}
		

		File nationsFile = new File(dataFolder, "nationElections.json");
		if (nationsFile.exists()) {
			nationsFile.delete();
			nationsFile.createNewFile();

			JSONObject jsonFile = new JSONObject();

			JSONArray electionsArray = new JSONArray();

			for (int i = 0; i < nationElections.size(); i++) {
				JSONObject electionObject = new JSONObject();
				electionObject.put("endtime", nationElections.get(i).getEndTime());
				electionObject.put("nation", nationElections.get(i).getNation().getName());
				
				JSONArray candidates = new JSONArray();
				for (UUID player : nationElections.get(i).getCandidates()) {
					candidates.add(player);
				}
				electionObject.put("candidates", candidates);
				
				JSONArray votes = new JSONArray();
				for (Map.Entry<UUID, UUID> entry : nationElections.get(i).getVotes().entrySet()) {
					JSONObject vote = new JSONObject();
					vote.put(entry.getKey().toString(), entry.getValue().toString());
					votes.add(vote);
				}
				electionObject.put("votes", votes);

				electionsArray.add(electionObject);
			}

			jsonFile.put("elections", electionsArray);

			FileWriter fw = new FileWriter(nationsFile);
			fw.write(jsonFile.toString());
			fw.close();
		}
		

		File townDecisionsFile = new File(dataFolder, "townDecisions.json");
		if (townDecisionsFile.exists()) {
			townDecisionsFile.delete();
			townDecisionsFile.createNewFile();

			JSONObject jsonFile = new JSONObject();

			JSONArray electionsArray = new JSONArray();

			for (int i = 0; i < townDecisions.size(); i++) {
				JSONObject electionObject = new JSONObject();
				electionObject.put("endtime", townDecisions.get(i).getEndTime());
				electionObject.put("town", townDecisions.get(i).getTown().getName());
				
				JSONArray votes = new JSONArray();
				for (Map.Entry<UUID, Boolean> entry : townDecisions.get(i).getVotes().entrySet()) {
					JSONObject vote = new JSONObject();
					vote.put(entry.getKey().toString(), entry.getValue());
					votes.add(vote);
				}
				electionObject.put("votes", votes);
				electionObject.put("type", townDecisions.get(i).getType());

				electionsArray.add(electionObject);
			}

			jsonFile.put("decisions", electionsArray);

			FileWriter fw = new FileWriter(townDecisionsFile);
			fw.write(jsonFile.toString());
			fw.close();
		}
	}

	public void removeTownElection(TownElection e) {
		e.finishElection();
		townElections.remove(e);
	}

	public void removeNationElection(NationElection e) {
		e.finishElection();
		nationElections.remove(e);
	}
	
	public void removeTownDecision(TownDecision d) {
		d.finishDecision();
		townDecisions.remove(d);
	}
	
	public void removeNationDecision(NationDecision d) {
		d.finishDecision();
		nationDecisions.remove(d);
	}

	public void addTownElection(TownElection e) {
		townElections.add(e);
	}

	public void addNationElection(NationElection e) {
		nationElections.add(e);
	}
	
	public void addTownDecision(TownDecision d) {
		townDecisions.add(d);
	}
	
	public void addNationDecision(NationDecision d) {
		nationDecisions.add(d);
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
	
	public TownDecision getTownDecision(Player p) {
		Town t;
		try {
			t = TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown();
		} catch (NotRegisteredException e) {
			return null;
		}
		for (TownDecision e : townDecisions) {
			if (e.getTown() == t) {
				return e;
			}
		}
		return null;
	}
	
	public NationDecision getNationDecision(Player p) {
		Nation n;
		try {
			n = TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown().getNation();
		} catch (NotRegisteredException e) {
			return null;
		}
		for (NationDecision e : nationDecisions) {
			if (e.getNation() == n) {
				return e;
			}
		}
		return null;
	}

}
