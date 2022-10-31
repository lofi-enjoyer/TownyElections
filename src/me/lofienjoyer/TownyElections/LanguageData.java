package me.lofienjoyer.TownyElections;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LanguageData {

    private final Map<String, String> messages;

    public LanguageData() {
        messages = new HashMap<String, String>();
    }

    public void load() {
        TownyElections instance = TownyElections.getInstance();
        File file = new File(instance.getDataFolder(), "language.yml");
        if (!file.exists()) {
            instance.saveResource("language.yml", false);
        }

        FileConfiguration languageFile = YamlConfiguration.loadConfiguration(new InputStreamReader(instance.getResource("language.yml")));
        for (String key : languageFile.getKeys(false)) {
            messages.put(key, (String) languageFile.get(key));
        }

        languageFile = YamlConfiguration.loadConfiguration(file);
        for (String key : languageFile.getKeys(false)) {
            messages.put(key, (String) languageFile.get(key));
        }
    }

    public void save() {
        TownyElections instance = TownyElections.getInstance();
        File file = new File(instance.getDataFolder(), "language.yml");
        if (!file.exists()) {
            instance.saveResource("language.yml", false);
        }
        FileConfiguration languageFile = YamlConfiguration.loadConfiguration(file);
        for (String key : messages.keySet()) {
            if (!languageFile.contains(key)) languageFile.set(key, "");
        }
        try {
            languageFile.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getString(String key) {
        String result = messages.get(key);
        if (result == null) {
            TownyElections.getInstance().getLogger().warning("Error: Key " + key + " not found");
            messages.put(key, null);
            return "NULL";
        }
        return result;
    }

}
