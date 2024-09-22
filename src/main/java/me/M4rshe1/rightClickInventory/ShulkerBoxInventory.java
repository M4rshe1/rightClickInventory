package me.M4rshe1.rightClickInventory;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;


public class ShulkerBoxInventory implements Listener {

    private final Player player;
    private final Inventory inventory;
    private final ItemStack shulkerBox;

    public ShulkerBoxInventory(Player player, ItemStack shulkerBox) {
        this.player = player;
        this.shulkerBox = shulkerBox;

        String displayName = shulkerBox.hasItemMeta() && shulkerBox.getItemMeta().hasDisplayName()
                ? PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(shulkerBox.getItemMeta().displayName()))
                : "Shulker Box";
        this.inventory = Bukkit.createInventory(null, 27, displayName);

        loadContents();
        Bukkit.getPluginManager().registerEvents(this, RightClickInventory.getInstance());
    }

    private void loadContents() {
        if (Arrays.asList(ShulkerBoxes.getShulkerBoxes()).contains(shulkerBox.getType()) && shulkerBox.hasItemMeta()) {
            BlockStateMeta meta = (BlockStateMeta) shulkerBox.getItemMeta();
            if (meta != null) {
                ItemStack[] contents = ((org.bukkit.block.ShulkerBox) meta.getBlockState()).getInventory().getContents();
                inventory.setContents(contents);
            }
        }
    }

    public void open() {
        player.openInventory(inventory);
    }

    public void updateShulkerBox() {
        if (shulkerBox.hasItemMeta()) {
            BlockStateMeta meta = (BlockStateMeta) shulkerBox.getItemMeta();
            if (meta != null) {
                org.bukkit.block.ShulkerBox shulker = (org.bukkit.block.ShulkerBox) meta.getBlockState();
                shulker.getInventory().setContents(inventory.getContents());
                shulker.update();
                meta.setBlockState(shulker);
                shulkerBox.setItemMeta(meta);
            }
        }
        player.getInventory().setItemInMainHand(shulkerBox);
    }


    @EventHandler
    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        if (event.getInventory().equals(inventory)) {
            updateShulkerBox();
        }
    }
}
