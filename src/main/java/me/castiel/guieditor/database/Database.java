package me.castiel.guieditor.database;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Database {

    private final File file;
    private final FileConfiguration config;

    public Database(File dataFolder, String filename) {
        if (!dataFolder.exists()) {
            if (dataFolder.mkdir())
                Bukkit.getLogger().info("[CasUtils] Created directory '" + dataFolder.getPath() + "'");
        }
        this.file = new File(dataFolder, filename);
        if (!file.exists()){
            try {
                if (file.createNewFile()) {
                    Bukkit.getLogger().info("[CasUtils] Created yaml file '" + filename + "'.");
                }
            }
            catch (IOException e){
                Bukkit.getLogger().warning("==========================================================");
                Bukkit.getLogger().warning("Something went wrong while attempting to create database!");
                Bukkit.getLogger().warning("==========================================================");
                Bukkit.getLogger().warning("> '" + filename + "' in directory '" + dataFolder.getPath() + "'");
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void save(){
        try{
            config.save(file);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}