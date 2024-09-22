package me.M4rshe1.rightClickInventory;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public final class RightClickInventory extends JavaPlugin implements Listener {

    private final HashMap<UUID, Boolean> toggleStates = new HashMap<>();
    private final HashMap<UUID, Boolean> shiftStates = new HashMap<>();
    private final Material[] shulkers = ShulkerBoxes.getShulkerBoxes();
    private static RightClickInventory instance;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }


    public static RightClickInventory getInstance() {
        return instance;
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (!toggleStates.getOrDefault(playerUUID, true)) {
            return;
        }

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Material itemInHand = player.getInventory().getItemInMainHand().getType();
        boolean requireShiftForPlayer = shiftStates.getOrDefault(playerUUID, false);

        if (!requireShiftForPlayer || player.isSneaking()) {
            if (itemInHand == Material.ENDER_CHEST) {
                if (player.hasPermission("rightclickinventory.use.enderchest")) {
                    Inventory enderChestInventory = player.getEnderChest();
                    player.openInventory(enderChestInventory);
                } else {
                    player.sendMessage("You don't have permission to open Ender Chests this way.");
                }
            } else if (Arrays.asList(shulkers).contains(itemInHand)) {
                if (player.hasPermission("rightclickinventory.use.shulkerbox")) {
                    ShulkerBoxInventory shulkerBox = new ShulkerBoxInventory(player, player.getInventory().getItemInMainHand());
                    shulkerBox.open();
                } else {
                    player.sendMessage("You don't have permission to open Shulker Boxes this way.");
                }
            }
        }
    }




    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        UUID playerUUID = player.getUniqueId();

        if (args.length == 0) {
            player.sendMessage("Usage: /rightClickInventory <toggle|toggleshift>");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("toggle")) {
            if (toggleStates.containsKey(playerUUID)) {
                boolean currentState = toggleStates.get(playerUUID);
                toggleStates.put(playerUUID, !currentState);  // Toggle the state
                player.sendMessage("Right-click inventory functionality " + (!currentState ? "enabled" : "disabled") + ".");
            } else {
                toggleStates.put(playerUUID, true);
                player.sendMessage("Right-click inventory functionality enabled.");
            }
            return true;
        } else if (subCommand.equals("toggleshift")) {
            if (shiftStates.containsKey(playerUUID)) {
                boolean currentShiftState = shiftStates.get(playerUUID);
                shiftStates.put(playerUUID, !currentShiftState);  // Toggle the shift requirement
                player.sendMessage("Shift requirement " + (!currentShiftState ? "enabled" : "disabled") + ".");
            } else {
                shiftStates.put(playerUUID, false);
                player.sendMessage("Shift requirement disabled.");
            }
            return true;
        } else {
            player.sendMessage("Invalid subcommand. Use /rightClickInventory <toggle|toggleshift>.");
            return true;
        }
    }
}
