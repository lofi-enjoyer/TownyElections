package com.aurgiyalgo.TownyElections;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import com.aurgiyalgo.TownyElections.commands.ElectionsCommandHandler;
import com.aurgiyalgo.TownyElections.commands.PartyCommandHandler;
import com.aurgiyalgo.TownyElections.commands.RevolutionsCommandHandler;
import com.aurgiyalgo.TownyElections.commands.TElectCommandHandler;
import com.aurgiyalgo.TownyElections.elections.ElectionManager;
import com.aurgiyalgo.TownyElections.elections.NationDecision;
import com.aurgiyalgo.TownyElections.elections.NationElection;
import com.aurgiyalgo.TownyElections.elections.TownDecision;
import com.aurgiyalgo.TownyElections.elections.TownElection;
import com.aurgiyalgo.TownyElections.listeners.TElectTabCompleter;
import com.aurgiyalgo.TownyElections.metrics.TEMetrics;
import com.aurgiyalgo.TownyElections.revolutions.Revolution;
import com.aurgiyalgo.TownyElections.revolutions.RevolutionManager;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;

public class TownyElections extends JavaPlugin {

	private FileConfiguration languageFile;
	private static TownyElections instance;
	private ElectionManager electionManager;
	private RevolutionManager revolutionManager;
	private TEMetrics metrics;
	private boolean debugEnabled;
	
	private List<String> helpLines;
	private List<String> infoLines;

	@Override
	public void onEnable() {
		instance = this;
		
		metrics = new TEMetrics(instance);
		
		setupConfig();
		setupLanguageFile();
		setupExtraFiles();
		
		electionManager = new ElectionManager(this);
		revolutionManager = new RevolutionManager(this);
		try {
			electionManager.loadElections(getDataFolder());
		} catch (Exception e) {
			e.printStackTrace();
		}

		getCommand("townyelections").setExecutor(new TElectCommandHandler(instance));
		getCommand("townyelections").setTabCompleter(new TElectTabCompleter());
		getCommand("elections").setExecutor(new ElectionsCommandHandler(this));
		getCommand("revolutions").setExecutor(new RevolutionsCommandHandler(this));
		getCommand("party").setExecutor(new PartyCommandHandler(this));
	}

	@Override
	public void onDisable() {
		try {
			electionManager.saveElections(getDataFolder());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
	
	private void setupExtraFiles() {
		instance.saveResource("help.txt", false);
		
		File helpFile = new File(getDataFolder(), "help.txt");
		try {
			helpLines = Files.readAllLines(helpFile.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		instance.saveResource("info.txt", false);
		
		File infoFile = new File(getDataFolder(), "info.txt");
		try {
			infoLines = Files.readAllLines(infoFile.toPath());
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
	
	public static TownElection getTownElection(Player p) {
		return instance.electionManager.getTownElection(p);
	}
	
	public static void addTownElection(TownElection e) {
		instance.electionManager.addTownElection(e);
	}
	
	public static void addRevolution(Revolution r) {
		instance.revolutionManager.addRevolution(r);
	}
	
	public static void removeTownElection(TownElection e) {
		instance.electionManager.removeTownElection(e);
	}
	
	public static void addNationElection(NationElection e) {
		instance.electionManager.addNationElection(e);
	}
	
	public static void removeNationElection(NationElection e) {
		instance.electionManager.removeNationElection(e);
	}
	
	public static Revolution getRevolution(Player p) {
		return instance.revolutionManager.getRevolution(p);
	}
	
	public static void removeRevolutions(Town t) {
		instance.revolutionManager.removeTownRevolutions(t);
	}
	
	public static NationElection getNationElection(Player p) {
		return instance.electionManager.getNationElection(p);
	}
	
	public static Revolution getInvite(UUID player) {
		return instance.revolutionManager.getInvite(player);
	}
	
	public static void revolutionInvite(UUID player, Revolution r) {
		instance.revolutionManager.revolutionInvite(player, r);
	}
	
	public static TownDecision getTownDecision(Player p) {
		return instance.electionManager.getTownDecision(p);
	}
	
	public static void removeTownDecision(TownDecision d) {
		instance.electionManager.removeTownDecision(d);
	}
	
	public static void addTownDecision(TownDecision d) {
		instance.electionManager.addTownDecision(d);
	}
	
	public static NationDecision getNationDecision(Player p) {
		return instance.electionManager.getNationDecision(p);
	}
	
	public static void removeNationDecision(NationDecision d) {
		instance.electionManager.removeNationDecision(d);
	}
	
	public static void addNationDecision(NationDecision d) {
		instance.electionManager.addNationDecision(d);
	}
	
	public static void disbandRevolution(Revolution r) {
		if (!instance.revolutionManager.getRevolutions().contains(r)) return;
		instance.revolutionManager.getRevolutions().remove(r);
	}
	
	public static boolean areRevolutionsEnabled() {
		return instance.revolutionManager.areRevolutionsEnabled();
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
	
	public static void successRevolution(Revolution r) {
		instance.revolutionManager.finishRevolution(r);
	}
	
	public static boolean checkPerms(Player p, Permission perm) {
		if (!p.hasPermission(perm)) {
			p.sendMessage(TownyElections.getTranslatedMessage("no-permission"));
			return false;
		}
		return true;
	}
	
	public List<String> getHelpMessage() {
		return helpLines;
	}
	
	public List<String> getInfoMessage() {
		return infoLines;
	}
	
	public FileConfiguration getLanguageFile() {
		return languageFile;
	}
	
	public boolean isDebugModeEnabled() {
		return debugEnabled;
	}
	
	public static class Permissions {
		public static final Permission TOWN_VOTE = new Permission("townyelections.vote.town");
		public static final Permission NATION_VOTE = new Permission("townyelections.vote.nation");
	}

}
