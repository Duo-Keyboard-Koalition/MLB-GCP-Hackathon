package org.iris.irisPlugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class FoodCommand implements CommandExecutor {  // Make sure to implement CommandExecutor
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final long COOLDOWN_TIME = 15 * 60 * 1000; // 15 minutes in milliseconds

    @Override  // Make sure to include the @Override annotation
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be run by players.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();

        // Check cooldown
        if (cooldowns.containsKey(playerId)) {
            long timeElapsed = System.currentTimeMillis() - cooldowns.get(playerId);
            if (timeElapsed < COOLDOWN_TIME) {
                long remainingTime = (COOLDOWN_TIME - timeElapsed) / 1000;
                long minutes = remainingTime / 60;
                long seconds = remainingTime % 60;
                player.sendMessage(ChatColor.RED +
                        String.format("You must wait %d minutes and %d seconds before requesting food again!",
                                minutes, seconds));
                return true;
            }
        }

        // Give food items
        ItemStack beef = new ItemStack(Material.BEEF, 4);
        ItemStack chicken = new ItemStack(Material.CHICKEN, 4);
        ItemStack pork = new ItemStack(Material.PORKCHOP, 4);

        player.getInventory().addItem(beef, chicken, pork);

        // Set cooldown
        cooldowns.put(playerId, System.currentTimeMillis());

        player.sendMessage(ChatColor.GREEN + "You have received raw meat!");
        player.sendMessage(ChatColor.YELLOW + "You can use this command again in 15 minutes.");

        return true;
    }
}