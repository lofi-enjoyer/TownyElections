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

import com.aurgiyalgo.TownyElections.commands.ElectionsCommandHandler;
import com.aurgiyalgo.TownyElections.commands.PartyCommandHandler;
import com.aurgiyalgo.TownyElections.commands.TElectCommandHandler;
import com.aurgiyalgo.TownyElections.elections.ElectionManager;
import com.aurgiyalgo.TownyElections.listeners.TElectTabCompleter;
import com.aurgiyalgo.TownyElections.metrics.TEMetrics;
import com.aurgiyalgo.TownyElections.parties.PartyManager;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;

public class TownyElections extends JavaPlugin {

	private static TownyElections instance;
	private FileConfiguration languageFile;
	private ElectionManager electionManager;
	private PartyManager partyManager;
	
	private boolean debugEnabled;

	@Override
	public void onEnable() {
		instance = this;
		
		new TEMetrics(instance);
		
		setupConfig();
		setupLanguageFile();
		
		electionManager = new ElectionManager(this, getDataFolder());
		partyManager = new PartyManager(this.getDataFolder());
		
		electionManager.loadElections();
		partyManager.loadData();

		getCommand("townyelections").setExecutor(new TElectCommandHandler(instance));
		getCommand("townyelections").setTabCompleter(new TElectTabCompleter());
		getCommand("elections").setExecutor(new ElectionsCommandHandler());
		getCommand("party").setExecutor(new PartyCommandHandler());
	}

	@Override
	public void onDisable() {
		electionManager.saveElections();
		partyManager.saveData();
		
		saveConfig();
	}
	
	private void setupConfig() {
		getConfig().addDefault("language", "en-US");
		getConfig().addDefault("max-duration", 604800000L);
		getConfig().addDefault("enabled-revolutions", true);
		getConfig().addDefault("debug-mode", false);
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		debugEnabled = getConfig().getBoolean("debug-mode");
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
		
		languageFile = YamlConfiguration.loadConfiguration(file);
		languageFile.addDefault("only-player", "&cOnly a player can execute this command!");
		languageFile.addDefault("not-enough-arguments", "&cNot enough arguments!");
		languageFile.addDefault("not-active-election", "&cYour town does not have an active election");
		languageFile.addDefault("active-election", "&cYour town has an active election");
		languageFile.addDefault("no-permission", "&cNot enough permissions!");
		languageFile.addDefault("not-in-a-town", "&cYou are not part of a town");
		languageFile.addDefault("already-voted", "&7You have already voted");
		languageFile.addDefault("election-convoked", "&aAn election just started!");
		languageFile.addDefault("error-input-string", "&cError on input string!");
		languageFile.addDefault("candidate-now", "&aYou are now a candidate!");
		languageFile.addDefault("new-candidate", "&cThe player %player% is now a candidate");
		languageFile.addDefault("election-won", "&f%player% won the election and is now the mayor!");
		languageFile.addDefault("no-winner", "&cThere is no winner!");
		languageFile.addDefault("election-lost", "&cYou lost the election!");
		languageFile.addDefault("invalid-candidate", "&cInvalid candidate!");
		languageFile.addDefault("you-voted", "&aYou voted for &f&k%player%");
		languageFile.addDefault("is-staff", "&cA city staff cannot do this");
		languageFile.addDefault("revolution-created", "&aRevolution created!");
		languageFile.addDefault("revolution-invited", "&aYou were invited to a revolution!");
		languageFile.addDefault("revolution-joined", "&aA player joined the revolution");
		languageFile.addDefault("revolutions-disabled", "&cRevolutions are disabled!");
		languageFile.addDefault("not-in-a-nation", "&cYour town is not part of a nation");
		languageFile.addDefault("not-active-election-nation", "&cYour nation does not have an active election");
		languageFile.addDefault("active-election-nation", "&cYour nation has an active election");
		languageFile.addDefault("not-active-town-decision", "&cYour town does not have an active decision");
		languageFile.addDefault("active-town-decision", "&cYour town has an active decision");
		languageFile.options().copyDefaults(true);
		try {
			languageFile.save(file);
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

	public static String getTranslatedMessage(String key) {
		String message = instance.languageFile.getString(key);
		if (message == null) return null;
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	public static void sendTownMessage(Town n, String message) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (n.hasResident(p.getName())) {
				p.sendMessage(message);
			}
		}
	}

	public static void sendTownSubtitle(Town n, String message) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (n.hasResident(p.getName())) {
				p.sendTitle(" ", message, 20, 60, 20);
			}
		}
	}

	public static void sendNationMessage(Nation n, String message) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (n.hasResident(p.getName())) {
				p.sendMessage(message);
			}
		}
	}

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
		return languageFile;
	}
	
	public boolean isDebugModeEnabled() {
		return debugEnabled;
	}
	
	public static class Permissions {
		public static final Permission TOWN_VOTE = new Permission("townyelections.elections.vote.town");
		public static final Permission TOWN_CONVOKE = new Permission("townyelections.elections.convoke.town");
		public static final Permission TOWN_LIST = new Permission("townyelections.elections.list.town");
		public static final Permission TOWN_STOP = new Permission("townyelections.elections.stop.town");
		public static final Permission TOWN_UNVOTE = new Permission("townyelections.elections.unvote.town");
		public static final Permission NATION_VOTE = new Permission("townyelections.elections.vote.nation");
		public static final Permission NATION_CONVOKE = new Permission("townyelections.elections.convoke.nation");
		public static final Permission NATION_LIST = new Permission("townyelections.elections.list.nation");
		public static final Permission NATION_STOP = new Permission("townyelections.elections.stop.nation");
		public static final Permission NATION_UNVOTE = new Permission("townyelections.elections.unvote.nation");

		public static final Permission TOWNPARTY_CREATE = new Permission("townyelections.party.create.town");
		public static final Permission TOWNPARTY_LEAVE = new Permission("townyelections.party.leave.town");
		public static final Permission TOWNPARTY_ADD = new Permission("townyelections.party.add.town");
		public static final Permission TOWNPARTY_ACCEPT = new Permission("townyelections.party.accept.town");
		public static final Permission TOWNPARTY_INVITE = new Permission("townyelections.party.invite.town");
		public static final Permission TOWNPARTY_SETLEADER = new Permission("townyelections.party.setleader.town");
		public static final Permission TOWNPARTY_PROMOTE = new Permission("townyelections.party.promote.town");
		public static final Permission TOWNPARTY_DEMOTE = new Permission("townyelections.party.demote.town");
		public static final Permission TOWNPARTY_INFO = new Permission("townyelections.party.info.town");
		public static final Permission NATIONPARTY_CREATE = new Permission("townyelections.party.create.nation");
		public static final Permission NATIONPARTY_LEAVE = new Permission("townyelections.party.leave.nation");
		public static final Permission NATIONPARTY_ADD = new Permission("townyelections.party.add.nation");
		public static final Permission NATIONPARTY_ACCEPT = new Permission("townyelections.party.accept.nation");
		public static final Permission NATIONPARTY_INVITE = new Permission("townyelections.party.invite.nation");
		public static final Permission NATIONPARTY_SETLEADER = new Permission("townyelections.party.setleader.nation");
		public static final Permission NATIONPARTY_PROMOTE = new Permission("townyelections.party.promote.nation");
		public static final Permission NATIONPARTY_DEMOTE = new Permission("townyelections.party.demote.nation");
		public static final Permission NATIONPARTY_INFO = new Permission("townyelections.party.info.nation");
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
				"&fVersion: &e%version%\r\n" + 
				"&fDeveloper: &e%author%\r\n" + 
				"\r\n" + 
				"&8---------------------------------";
	}
	
	public static void info(String msg) {
		if (instance.debugEnabled) 
			instance.getLogger().log(Level.INFO, msg);
	}
	
	public static void warning(String msg) {
		if (instance.debugEnabled) 
			instance.getLogger().log(Level.WARNING, msg);
	}
	
	public static void error(String msg) {
		if (instance.debugEnabled) 
			instance.getLogger().log(Level.SEVERE, msg);
	}
	
	public static TownyElections getInstance() {
		return instance;
	}
	
	public PartyManager getPartyManager() {
		return partyManager;
	}
	
	public ElectionManager getElectionManager() {
		return electionManager;
	}

}
