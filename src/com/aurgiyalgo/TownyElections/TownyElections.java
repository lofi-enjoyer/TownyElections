package com.aurgiyalgo.TownyElections;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
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

import fr.minuskube.inv.InventoryManager;

public class TownyElections extends JavaPlugin {

	private static TownyElections instance;
	private FileConfiguration languageFile;
	private ElectionManager electionManager;
	private PartyManager partyManager;
	private InventoryManager inventoryManager;
	
	private boolean debugEnabled;
	private int minDuration;

	@Override
	public void onEnable() {
		instance = this;
		
		new TEMetrics(instance);
		
		setupConfig();
		setupLanguageFile();
		
		inventoryManager = new InventoryManager(this);
		inventoryManager.init();
		
		electionManager = new ElectionManager();
		partyManager = new PartyManager();
		
		electionManager.loadData();
		partyManager.loadData();

		getCommand("townyelections").setExecutor(new TElectCommandHandler(instance));
		getCommand("townyelections").setTabCompleter(new TElectTabCompleter());
		getCommand("elections").setExecutor(new ElectionsCommandHandler());
		getCommand("party").setExecutor(new PartyCommandHandler());
	}

	@Override
	public void onDisable() {
		electionManager.saveData();
		partyManager.saveData();
		
		saveConfig();
	}
	
	private void setupConfig() {
		getConfig().addDefault("language", "en-US");
		getConfig().addDefault("max-duration", 10080);
		getConfig().addDefault("min-duration", 60);
		getConfig().addDefault("debug-mode", false);
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		Configuration.DEBUG_ENABLED = getConfig().getBoolean("debug-mode");
		Configuration.MAX_DURATION = getConfig().getInt("max-duration");
		Configuration.MIN_DURATION = getConfig().getInt("min-duration");
	}
	
	public static class Configuration {
		
		public static int MAX_DURATION = 10080;
		public static int MIN_DURATION = 60;
		public static boolean DEBUG_ENABLED = false;
		
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
		languageFile.addDefault("election-won", "&f%party% won the election and are leaders now!");
		languageFile.addDefault("no-winner", "&cThere is no winner!");
		languageFile.addDefault("election-lost", "&cYou lost the election!");
		languageFile.addDefault("invalid-candidate", "&cInvalid candidate!");
		languageFile.addDefault("you-voted", "&aYou voted for &f&l%party%");
		languageFile.addDefault("unvoted", "&7Your vote was succesfully removed");
		languageFile.addDefault("is-staff", "&cA city staff cannot do this");
		languageFile.addDefault("not-in-a-nation", "&cYour town is not part of a nation");
		languageFile.addDefault("not-active-election-nation", "&cYour nation does not have an active election");
		languageFile.addDefault("active-election-nation", "&cYour nation has an active election");
		languageFile.addDefault("min-duration", "&cThe duration has to be at least %min%");
		languageFile.addDefault("max-duration", "&cThe duration has to be at most %max%");
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
		if (message == null) return "";
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

	/**
	 * If player does not have permission sends a "no-permission" message and returns false
	 * @param
	 * @param
	 * @return Player have permission
	 */
	public static boolean hasPerms(Player p, String perm) {
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
		public static final String TOWN_VOTE = "townyelections.elections.vote.town";
		public static final String TOWN_CONVOKE = "townyelections.elections.convoke.town";
		public static final String TOWN_LIST = "townyelections.elections.list.town";
		public static final String TOWN_STOP = "townyelections.elections.stop.town";
		public static final String TOWN_UNVOTE = "townyelections.elections.unvote.town";
		public static final String NATION_VOTE = "townyelections.elections.vote.nation";
		public static final String NATION_CONVOKE = "townyelections.elections.convoke.nation";
		public static final String NATION_LIST = "townyelections.elections.list.nation";
		public static final String NATION_STOP = "townyelections.elections.stop.nation";
		public static final String NATION_UNVOTE = "townyelections.elections.unvote.nation";
                            
		public static final String TOWNPARTY_CREATE = "townyelections.party.create.town";
		public static final String TOWNPARTY_LEAVE = "townyelections.party.leave.town";
		public static final String TOWNPARTY_ADD = "townyelections.party.add.town";
		public static final String TOWNPARTY_ACCEPT = "townyelections.party.accept.town";
		public static final String TOWNPARTY_INVITES = "townyelections.party.invites.town";
		public static final String TOWNPARTY_SETLEADER = "townyelections.party.setleader.town";
		public static final String TOWNPARTY_PROMOTE = "townyelections.party.promote.town";
		public static final String TOWNPARTY_DEMOTE = "townyelections.party.demote.town";
		public static final String TOWNPARTY_INFO = "townyelections.party.info.town";
		public static final String NATIONPARTY_CREATE = "townyelections.party.create.nation";
		public static final String NATIONPARTY_LEAVE = "townyelections.party.leave.nation";
		public static final String NATIONPARTY_ADD = "townyelections.party.add.nation";
		public static final String NATIONPARTY_ACCEPT = "townyelections.party.accept.nation";
		public static final String NATIONPARTY_INVITES = "townyelections.party.invites.nation";
		public static final String NATIONPARTY_SETLEADER = "townyelections.party.setleader.nation";
		public static final String NATIONPARTY_PROMOTE = "townyelections.party.promote.nation";
		public static final String NATIONPARTY_DEMOTE = "townyelections.party.demote.nation";
		public static final String NATIONPARTY_INFO = "townyelections.party.info.nation";
	}
	
	public static class Text {
		public static final String ELECTIONS_HELP_MESSAGE = "&8&m-------- &6&lTowny Elections&r &8&m--------\n" + 
				"\n" + 
				"  &7- &f/elections &evote [town/nation] &8- &eVote for a candidate\n" + 
				"  &7- &f/elections &econvoke [town/nation] &8- &eConvoke an election in your town\n" +
				"  &7- &f/elections &elist [town/nation] &8- &eCandidates of the election\n" + 
				"  &7- &f/elections &estop [town/nation] &8- &eStop the current election\n" +
				"  &7- &f/elections &eunvote [town/nation] &8- &eRetire your vote so you can vote for another party\n" +
				"\n" +
				"&8---------------------------------";
		public static final String PARTY_HELP_MESSAGE = "&8&m-------- &6&lTowny Elections&r &8&m--------\n" + 
				"\n" + 
				"  &7- &f/party &ecreate [town/nation] &8- &eCreate a political party\n" +
				"  &7- &f/party &eleave [town/nation] &8- &eLeave your current party\n" +
				"  &7- &f/party &eadd [town/nation] &8- &eInvite a player to the party\n" +
				"  &7- &f/party &eaccept [town/nation] &8- &eAccept an invite to join a party\n" +
				"  &7- &f/party &einvites [town/nation] &8- &eList of your invites\n" +
				"  &7- &f/party &esetleader [town/nation] &8- &eSet the leader of your party\n" +
				"  &7- &f/party &epromote [town/nation] &8- &ePromote a member to a party assistant\n" +
				"  &7- &f/party &edemote [town/nation] &8- &eDemote a party assistant to member\n" +
				"  &7- &f/party &einfo [town/nation] &8- &eInformation about your party\n" +
				"\n" + 
				"&8---------------------------------";
		public static final String HELP_MESSAGE = "&8&m-------- &6&lTowny Elections&r &8&m--------\n" + 
				"\n" + 
				"  &7- &f/elections [town/nation] &evote &8- &eVote for a candidate\n" + 
				"  &7- &f/elections [town/nation] &econvoke &8- &eConvoke an election in your town\n" +
				"  &7- &f/elections [town/nation] &elist &8- &eCandidates of the election\n" + 
				"  &7- &f/elections [town/nation] &estop &8- &eStop the current election\n" +
				"  &7- &f/elections [town/nation] &eunvote &8- &eRetire your vote so you can vote for another party\n" +
				"\n" +
				"  &7- &f/party &ecreate [town/nation] &8- &eCreate a political party\n" +
				"  &7- &f/party &eleave [town/nation] &8- &eLeave your current party\n" +
				"  &7- &f/party &eadd [town/nation] &8- &eInvite a player to the party\n" +
				"  &7- &f/party &eaccept [town/nation] &8- &eAccept an invite to join a party\n" +
				"  &7- &f/party &einvites [town/nation] &8- &eList of your invites\n" +
				"  &7- &f/party &esetleader [town/nation] &8- &eSet the leader of your party\n" +
				"  &7- &f/party &epromote [town/nation] &8- &ePromote a member to a party assistant\n" +
				"  &7- &f/party &edemote [town/nation] &8- &eDemote a party assistant to member\n" +
				"  &7- &f/party &einfo [town/nation] &8- &eInformation about your party\n" +
				"\n" + 
				"&8---------------------------------";
		public static final String INFO_MESSAGE = "&8-------- &6&lTowny Elections&r &8--------\n" + 
				"\n" + 
				"&fVersion: &e%version%\n" + 
				"&fDeveloper: &e%author%\n" + 
				"\n" + 
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
	
	public InventoryManager getInventoryManager() {
		return inventoryManager;
	}

}
