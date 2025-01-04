package org.iris.irisPlugin;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

public class WolvesCommand implements CommandExecutor {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final long COOLDOWN_TIME = 30 * 60 * 1000; // 30 minutes in milliseconds

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("wolves")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command can only be run by players.");
                return true;
            }

            Player player = (Player) sender;
            UUID playerId = player.getUniqueId();

            // Check if player is in cooldown
            if (cooldowns.containsKey(playerId)) {
                long timeElapsed = System.currentTimeMillis() - cooldowns.get(playerId);

                if (timeElapsed < COOLDOWN_TIME) {
                    // Calculate remaining time
                    long remainingTime = (COOLDOWN_TIME - timeElapsed) / 1000; // Convert to seconds
                    long minutes = remainingTime / 60;
                    long seconds = remainingTime % 60;

                    player.sendMessage(String.format("§cYou must wait %d minutes and %d seconds before summoning wolves again!",
                            minutes, seconds));
                    return true;
                }
            }

            // Summon wolves
            Location location = player.getLocation();
            for (int i = 0; i < 5; i++) {
                Wolf wolf = player.getWorld().spawn(location, Wolf.class);
                wolf.setOwner(player);
                wolf.setTamed(true);
            }

            // Set cooldown
            cooldowns.put(playerId, System.currentTimeMillis());
            player.sendMessage("§aYou have summoned 5 loyal wolves!");
            player.sendMessage("§eYou can use this command again in 30 minutes.");

            return true;
        }
        return false;
    }
}