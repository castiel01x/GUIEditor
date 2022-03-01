package me.castiel.guieditor;

import me.castiel.guieditor.commands.CommandsManager;
import me.castiel.guieditor.configs.Messages;
import me.castiel.guieditor.database.DatabaseManager;
import me.castiel.guieditor.listeners.InventoryCloseEventListener;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class GUIEditor extends JavaPlugin {

    private static GUIEditor guiEditor;

    private Messages messages;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        guiEditor = this;
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        console.sendMessage("[GUIEditor] GUIEditor Loading...");
        saveDefaultConfig();
        saveResource("messages.yml", false);
        saveResource("commands.yml", false);
        databaseManager = new DatabaseManager(this, getDataFolder(), "inventories");
        new BukkitRunnable() {
            @Override
            public void run() {
                messages = new Messages(YamlConfiguration.loadConfiguration(new File(getDataFolder(), "messages.yml")));
                new CommandsManager(YamlConfiguration.loadConfiguration(new File(getDataFolder(), "commands.yml")));
                PluginManager pm = getServer().getPluginManager();
                pm.registerEvents(new InventoryCloseEventListener(), guiEditor);
                console.sendMessage("[GUIEditor] GUIEditor Loaded :)");
            }
        }.runTaskLater(this, 100);
    }

    @Override
    public void onDisable() {
        databaseManager.saveDatabases();
    }

    public static GUIEditor getInstance() {
        return guiEditor;
    }

    public Messages getMessages() {
        return messages;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
