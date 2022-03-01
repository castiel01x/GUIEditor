package me.castiel.guieditor.inventory;

import me.castiel.guieditor.GUIEditor;
import me.castiel.guieditor.database.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;

public class GUIEditorInventory implements InventoryHolder {

    private final Inventory inv;

    public GUIEditorInventory(Player player) {
        GUIEditor guiEditor = GUIEditor.getInstance();
        DatabaseManager databaseManager = guiEditor.getDatabaseManager();
        FileConfiguration db = databaseManager.getConfig(player.getUniqueId().toString()).getConfig();
        FileConfiguration config = guiEditor.getConfig();
        inv = Bukkit.createInventory(this, config.getInt("defaultInventorySize", 54), Objects.requireNonNull(db.getString("title", config.getString("defaultInventoryTitle"))).replace('&', ChatColor.COLOR_CHAR));
        ItemStack[] itemStacks = null;
        try {
            itemStacks = itemStackArrayFromBase64(db.getString("data", null));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (itemStacks != null)
            inv.setContents(itemStacks);
    }

    @NotNull
    @Override
    public Inventory getInventory(){
        return inv;
    }

    private ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
        if (data != null) {
            try {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
                BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
                ItemStack[] items = new ItemStack[dataInput.readInt()];
                for (int i = 0; i < items.length; i++) {
                    items[i] = (ItemStack) dataInput.readObject();
                }
                dataInput.close();
                return items;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}