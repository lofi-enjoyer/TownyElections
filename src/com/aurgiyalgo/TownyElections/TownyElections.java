package com.aurgiyalgo.TownyElections;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import com.aurgiyalgo.TownyElections.commands.PartyCommandHandler;
import com.aurgiyalgo.TownyElections.commands.TElectCommandHandler;
import com.aurgiyalgo.TownyElections.elections.ElectionManager;
import com.aurgiyalgo.TownyElections.elections.NationDecision;
import com.aurgiyalgo.TownyElections.elections.NationElection;
import com.aurgiyalgo.TownyElections.elections.TownDecision;
import com.aurgiyalgo.TownyElections.elections.TownElection;
import com.aurgiyalgo.TownyElections.listeners.TElectTabCompleter;
import com.aurgiyalgo.TownyElections.metrics.TEMetrics;
import com.aurgiyalgo.TownyElections.parties.PartyManager;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;

public class TownyElections extends JavaPlugin {

	private static TownyElections instance;
	private FileConfiguration _languageFile;
	private ElectionManager _electionManager;
	private TEMetrics _metrics;
	private PartyManager _partyManager;
	
	private boolean _debugEnabled;

	@Override
	public void onEnable() {
		instance = this;
		
		_metrics = new TEMetrics(instance);
		
		setupConfig();
		setupLanguageFile();
		
		_electionManager = new ElectionManager(this, getDataFolder());
		_partyManager = new PartyManager(this.getDataFolder());
		
		_electionManager.loadElections();
		_partyManager.loadData();

		getCommand("townyelections").setExecutor(new TElectCommandHandler(instance));
		getCommand("townyelections").setTabCompleter(new TElectTabCompleter());
		getCommand("party").setExecutor(new PartyCommandHandler());
	}

	@Override
	public void onDisable() {
		_electionManager.saveElections();
		_partyManager.saveData();
		
		saveConfig();
	}
	
	private void setupConfig() {
		getConfig().addDefault("language", "en-US");
		getConfig().addDefault("max-duration", 604800000L);
		getConfig().addDefault("enabled-revolutions", true);
		getConfig().addDefault("debug-mode", false);
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		_debugEnabled = getConfig().getBoolean("debug-mode");
	}
	
	private void setupLanguageFile() {
		File file = new File(getDataFolder(), getConfig().getString("language") + ".yml");
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		_languageFile = YamlConfiguration.loadConfiguration(file);
		_languageFile.addDefault("only-player", "&cOnly a player can execute this command!");
		_languageFile.addDefault("not-enough-arguments", "&cNot enough arguments!");
		_languageFile.addDefault("not-active-election", "&cYour town does not have an active election");
		_languageFile.addDefault("active-election", "&cYour town has an active election");
		_languageFile.addDefault("no-permission", "&cNot enough permissions!");
		_languageFile.addDefault("not-in-a-town", "&cYou are not part of a town");
		_languageFile.addDefault("already-voted", "&7You have already voted");
		_languageFile.addDefault("election-convoked", "&aAn election just started!");
		_languageFile.addDefault("error-input-string", "&cError on input string!");
		_languageFile.addDefault("candidate-now", "&aYou are now a candidate!");
		_languageFile.addDefault("new-candidate", "&cThe player %player% is now a candidate");
		_languageFile.addDefault("election-won", "&f%player% won the election and is now the mayor!");
		_languageFile.addDefault("no-winner", "&cThere is no winner!");
		_languageFile.addDefault("election-lost", "&cYou lost the election!");
		_languageFile.addDefault("invalid-candidate", "&cInvalid candidate!");
		_languageFile.addDefault("you-voted", "&aYou voted for &f&k%player%");
		_languageFile.addDefault("is-staff", "&cA city staff cannot do this");
		_languageFile.addDefault("revolution-created", "&aRevolution created!");
		_languageFile.addDefault("revolution-invited", "&aYou were invited to a revolution!");
		_languageFile.addDefault("revolution-joined", "&aA player joined the revolution");
		_languageFile.addDefault("revolutions-disabled", "&cRevolutions are disabled!");
		_languageFile.addDefault("not-in-a-nation", "&cYour town is not part of a nation");
		_languageFile.addDefault("not-active-election-nation", "&cYour nation does not have an active election");
		_languageFile.addDefault("active-election-nation", "&cYour nation has an active election");
		_languageFile.addDefault("not-active-town-decision", "&cYour town does not have an active decision");
		_languageFile.addDefault("active-town-decision", "&cYour town has an active decision");
		_languageFile.options().copyDefaults(true);
		try {
			_languageFile.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static class MutableInteger {
		public int value;

		public MutableInteger(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}
	
	@Deprecated
	public static TownElection getTownElection(Player p) {
		return instance._electionManager.getTownElection(p);
	}

	@Deprecated
	public static void addTownElection(TownElection e) {
		instance._electionManager.addTownElection(e);
	}

	@Deprecated
	public static void removeTownElection(TownElection e) {
		instance._electionManager.removeTownElection(e);
	}

	@Deprecated
	public static void addNationElection(NationElection e) {
		instance._electionManager.addNationElection(e);
	}

	@Deprecated
	public static void removeNationElection(NationElection e) {
		instance._electionManager.removeNationElection(e);
	}

	@Deprecated
	public static NationElection getNationElection(Player p) {
		return instance._electionManager.getNationElection(p);
	}

	@Deprecated
	public static TownDecision getTownDecision(Player p) {
		return instance._electionManager.getTownDecision(p);
	}

	@Deprecated
	public static void removeTownDecision(TownDecision d) {
		instance._electionManager.removeTownDecision(d);
	}

	@Deprecated
	public static void addTownDecision(TownDecision d) {
		instance._electionManager.addTownDecision(d);
	}

	@Deprecated
	public static NationDecision getNationDecision(Player p) {
		return instance._electionManager.getNationDecision(p);
	}

	@Deprecated
	public static void removeNationDecision(NationDecision d) {
		instance._electionManager.removeNationDecision(d);
	}

	@Deprecated
	public static void addNationDecision(NationDecision d) {
		instance._electionManager.addNationDecision(d);
	}

	@Deprecated
	public static String getTranslatedMessage(String key) {
		String message = instance._languageFile.getString(key);
		if (message == null) return null;
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	@Deprecated
	public static void sendTownMessage(Town n, String message) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (n.hasResident(p.getName())) {
				p.sendMessage(message);
			}
		}
	}

	@Deprecated
	public static void sendTownSubtitle(Town n, String message) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (n.hasResident(p.getName())) {
				p.sendTitle(" ", message, 20, 60, 20);
			}
		}
	}

	@Deprecated
	public static void sendNationMessage(Nation n, String message) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (n.hasResident(p.getName())) {
				p.sendMessage(message);
			}
		}
	}

	@Deprecated
	public static void sendNationSubtitle(Nation n, String message) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (n.hasResident(p.getName())) {
				p.sendTitle(" ", message, 20, 60, 20);
			}
		}
	}
	
	public static boolean checkPerms(Player p, Permission perm) {
		if (!p.hasPermission(perm)) {
			p.sendMessage(TownyElections.getTranslatedMessage("no-permission"));
			return false;
		}
		return true;
	}
	
	public FileConfiguration getLanguageFile() {
		return _languageFile;
	}
	
	public boolean isDebugModeEnabled() {
		return _debugEnabled;
	}
	
	public static class Permissions {
		public static final Permission TOWN_VOTE = new Permission("townyelections.vote.town");
		public static final Permission NATION_VOTE = new Permission("townyelections.vote.nation");
	}
	
	public static class Text {
		public static final String HELP_MESSAGE = "&8&m-------- &6&lTowny Elections&r &8&m--------\r\n" + 
				"\r\n" + 
				"  &7- &f/elections [town/nation/towndecision/nationdecision] info &8- &ePlugin info\r\n" + 
				"  &7- &f/elections [town/nation/towndecision/nationdecision] &8- &ePlugin info\r\n" + 
				"  &7- &f/elections [town/nation/towndecision/nationdecision] vote &8- &eVote for a candidate\r\n" + 
				"  &7- &f/elections [town/nation/towndecision/nationdecision] list &8- &eCandidates of the election\r\n" + 
				"  &7- &f/elections [town/nation/towndecision/nationdecision] convoke &8- &eConvoke an election in your town\r\n" + 
				"  &7- &f/elections [town/nation/towndecision/nationdecision] run &8- &eBe a candidate\r\n" + 
				"\r\n" + 
				"&8---------------------------------";
		public static final String INFO_MESSAGE = "&8-------- &6&lTowny Elections&r &8--------\r\n" + 
				"\r\n" + 
				"&fDescription: &e%version%\r\n" + 
				"&fVersion: &e%version%\r\n" + 
				"&fDeveloper: &e%author%\r\n" + 
				"\r\n" + 
				"&8---------------------------------";
	}
	
	public static void debug(Level level, String msg) {
		if (!instance._debugEnabled) return;
		instance.getLogger().log(level, msg);
	}
	
	public static TownyElections getInstance() {
		return instance;
	}
	
	public PartyManager getPartyManager() {
		return _partyManager;
	}

}
