package me.castiel.guieditor.util;

import me.castiel.guieditor.GUIEditor;
import me.castiel.guieditor.configs.Messages;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class CooldownManager {

    private final HashMap<UUID, Long> cooldownsList;
    private final Messages messages;
    private final int cooldown;

    public CooldownManager() {
        cooldownsList = new HashMap<>();
        GUIEditor guiEditor = GUIEditor.getInstance();
        messages = guiEditor.getMessages();
        cooldown = guiEditor.getConfig().getInt("commands-Cooldown");
    }

    public void addCooldown(UUID uuid) {
        cooldownsList.put(uuid, System.currentTimeMillis() + (cooldown * 1000L));
    }

    public Long getCooldown(UUID uuid) {
        if (cooldownsList.containsKey(uuid))
            return cooldownsList.get(uuid) - System.currentTimeMillis();
        return 0L;
    }

    public String getCooldownFormatted(UUID uuid) {
        long seconds = getCooldown(uuid) / 1000L;
        return messages.commandsCooldownMessage.replace("%timeleft%", String.valueOf(seconds));
    }

    public boolean cooldownNotOver(Player player) {
        UUID uuid = player.getUniqueId();
        boolean stillHasCooldown = getCooldown(uuid) > 0;
        if (stillHasCooldown)
            player.sendMessage(getCooldownFormatted(uuid));
        else
            addCooldown(uuid);
        return stillHasCooldown;
    }
}