package me.castiel.guieditor.listeners;

import me.castiel.guieditor.GUIEditor;
import me.castiel.guieditor.database.DatabaseManager;
import me.castiel.guieditor.inventory.GUIEditorInventory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayOutputStream;

public class InventoryCloseEventListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof GUIEditorInventory) {
            Player player = (Player) event.getPlayer();
            GUIEditor guiEditor = GUIEditor.getInstance();
            DatabaseManager databaseManager = guiEditor.getDatabaseManager();
            FileConfiguration db = databaseManager.getConfig(player.getUniqueId().toString()).getConfig();
            db.set("data", itemStackArrayToBase64(event.getInventory().getContents()));
            player.sendMessage(guiEditor.getMessages().guiSavedMessage);
        }
    }

    private String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(items.length);
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
