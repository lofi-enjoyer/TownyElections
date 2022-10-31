package me.lofienjoyer.TownyElections.elections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.lofienjoyer.TownyElections.TownyElections;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import me.lofienjoyer.TownyElections.data.DataHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;

public class ElectionManager {

	private final List<TownElection> townElections;
	private final List<NationElection> nationElections;
	private final DataHandler dataHandler;
	private final Gson gson;

	public ElectionManager() {
		townElections = new ArrayList<TownElection>();
		nationElections = new ArrayList<NationElection>();
		
		dataHandler = new DataHandler(TownyElections.getInstance().getDataFolder(), "elections.json");
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

		}.runTaskTimer(TownyElections.getInstance(), 0, 100);
	}

	public void loadData() {
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
	}

	public void saveData() {
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
			t = TownyUniverse.getInstance().getResident(p.getName()).getTown();
		} catch (NotRegisteredException e1) {
			return null;
		}
		for (TownElection e : townElections) {
			if (e.getTown().getName().equals(t.getName())) {
				return e;
			}
		}
		return null;
	}
	
	public NationElection getNationElection(Player p) {
		Nation n;
		try {
			n = TownyUniverse.getInstance().getResident(p.getName()).getTown().getNation();
		} catch (NotRegisteredException e) {
			return null;
		}
		for (NationElection e : nationElections) {
			if (e.getNation().getName().equals(n.getName())) {
				return e;
			}
		}
		return null;
	}

}
