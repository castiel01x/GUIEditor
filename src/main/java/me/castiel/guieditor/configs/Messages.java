package me.castiel.guieditor.configs;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

public class Messages {

    public String guiSavedMessage;
    public String guiNewTitleSaved;
    public String guiRenameInvalidArguments;
    public String guiRenameExceededLimit;
    public String commandsCooldownMessage;

    public Messages(FileConfiguration messagesCFG) {
        guiSavedMessage = Objects.requireNonNull(messagesCFG.getString("guiSavedMessage")).replace('&', ChatColor.COLOR_CHAR);
        guiNewTitleSaved = Objects.requireNonNull(messagesCFG.getString("guiNewTitleSaved")).replace('&', ChatColor.COLOR_CHAR);
        guiRenameInvalidArguments = Objects.requireNonNull(messagesCFG.getString("guiRenameInvalidArguments")).replace('&', ChatColor.COLOR_CHAR);
        guiRenameExceededLimit = Objects.requireNonNull(messagesCFG.getString("guiRenameExceededLimit")).replace('&', ChatColor.COLOR_CHAR);
        commandsCooldownMessage = Objects.requireNonNull(messagesCFG.getString("commandsCooldownMessage")).replace('&', ChatColor.COLOR_CHAR);
    }
}
