package me.castiel.guieditor.commands;

import me.castiel.guieditor.inventory.GUIEditorInventory;
import me.castiel.guieditor.util.CooldownManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;

public class CommandsManager extends BukkitCommand {

    private final String mainEditorCMD;
    private List<String> guiRenameSubCommands = new ArrayList<>();
    private GUIRenameCommand guiRenameCommand;
    private final CooldownManager cooldownManager;

    public CommandsManager(FileConfiguration commandsCFG) {
        super(Objects.requireNonNull(commandsCFG.getString("guiEditCommand.mainCommand")));
        cooldownManager = new CooldownManager();

        mainEditorCMD = getName();

        List<String> guiEditCommands = commandsCFG.getStringList("guiEditCommand.aliases");
        this.setAliases(guiEditCommands);

        final Field bukkitCommandMap;
        try {
            bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return;
        }
        bukkitCommandMap.setAccessible(true);
        CommandMap commandMap;
        try {
            commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        commandMap.register(getName(), this);

        if (commandsCFG.getBoolean("guiRenameCommand.enabled", false)) {
            String mainRenameCommand = commandsCFG.getString("guiRenameCommand.mainCommand");
            guiRenameSubCommands = commandsCFG.getStringList("guiRenameCommand.sub-commands");
            guiRenameCommand = new GUIRenameCommand(mainRenameCommand, commandsCFG.getStringList("guiRenameCommand.aliases"), cooldownManager);
            assert mainRenameCommand != null;
            commandMap.register(mainRenameCommand, guiRenameCommand);
        }
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You can only run the command in-game :(");
            return true;
        }
        if (cooldownManager.cooldownNotOver(player))
            return true;
        if (args.length > 0) {
            if (guiRenameSubCommands.contains(args[0])) {
                guiRenameCommand.executeAliase(sender, args);
                return true;
            }
        }
        player.openInventory(new GUIEditorInventory(player).getInventory());
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        final List<String> completions = new ArrayList<>();
        if (!guiRenameSubCommands.isEmpty())
            completions.add(guiRenameSubCommands.get(0));
        StringUtil.copyPartialMatches(args[0], Collections.singletonList(mainEditorCMD), completions);
        Collections.sort(completions);
        return completions;
    }
}