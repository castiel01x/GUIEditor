package me.castiel.guieditor.commands;

import me.castiel.guieditor.GUIEditor;
import me.castiel.guieditor.configs.Messages;
import me.castiel.guieditor.database.DatabaseManager;
import me.castiel.guieditor.util.CooldownManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.stream;

public class GUIRenameCommand extends BukkitCommand {

    private final CooldownManager cooldownManager;
    private final DatabaseManager databaseManager;
    private final Messages messages;

    protected GUIRenameCommand(String name, List<String> aliases, CooldownManager cooldownManager) {
        super(name);
        this.setAliases(aliases);
        this.cooldownManager = cooldownManager;
        GUIEditor guiEditor = GUIEditor.getInstance();
        databaseManager = guiEditor.getDatabaseManager();
        messages = guiEditor.getMessages();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You can only run the command in-game :(");
            return true;
        }
        if (cooldownManager.cooldownNotOver(player))
            return true;
        execute(sender, args, 0);
        return true;
    }

    public void executeAliase(@NotNull CommandSender sender, String[] args) {
        execute(sender, args, 1);
    }

    private void execute(@NotNull CommandSender sender, String[] args, int skip) {
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        if (args.length > skip) {
            StringBuilder finalTitle = new StringBuilder();
            for (String str : stream(args).skip(skip).toList()) {
                finalTitle.append(str);
            }
            FileConfiguration db = databaseManager.getConfig(uuid.toString()).getConfig();
            String title = finalTitle.toString();
            if (title.length() <= 32) {
                db.set("title", title);
                player.sendMessage(messages.guiNewTitleSaved);
            }
            else {
                player.sendMessage(messages.guiRenameExceededLimit);
            }
        }
        else {
            player.sendMessage(messages.guiRenameInvalidArguments);
        }
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }
}
