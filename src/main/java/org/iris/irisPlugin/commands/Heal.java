package org.iris.irisPlugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class Heal implements CommandExecutor {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final File cooldownFile;
    private final FileConfiguration cooldownConfig;
    private final Plugin plugin;

    public Heal(Plugin plugin) {
        this.plugin = plugin;
        this.cooldownFile = new File(plugin.getDataFolder(), "heal_cooldowns.yml");
        this.cooldownConfig = YamlConfiguration.loadConfiguration(cooldownFile);
        loadCooldowns();
    }

    private void loadCooldowns() {
        if (cooldownConfig.contains("cooldowns")) {
            for (String uuid : cooldownConfig.getConfigurationSection("cooldowns").getKeys(false)) {
                cooldowns.put(UUID.fromString(uuid),
                        cooldownConfig.getLong("cooldowns." + uuid));
            }
        }
    }

    private void saveCooldowns() {
        for (UUID uuid : cooldowns.keySet()) {
            cooldownConfig.set("cooldowns." + uuid.toString(), cooldowns.get(uuid));
        }
        try {
            cooldownConfig.save(cooldownFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save heal cooldown data!");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("heal")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                UUID playerUUID = player.getUniqueId();
                long currentTime = System.currentTimeMillis();

                // Check for permission to bypass cooldown
                if (!player.hasPermission("irisplugin.heal.bypass")) {
                    if (cooldowns.containsKey(playerUUID)) {
                        long lastUsed = cooldowns.get(playerUUID);
                        // 30 minutes
                        // long COOLDOWN_TIME = (long) plugin.getConfig().getInt("cooldowns.wolves", 30) * 60 * 1000;
                        //
                        long cooldownTime = (long) plugin.getConfig().getInt("cooldowns.heal", 30) * 60 * 1000;
                        if (currentTime - lastUsed < cooldownTime) {
                            long timeLeft = (cooldownTime - (currentTime - lastUsed)) / 1000;
                            player.sendMessage(ChatColor.RED + "You must wait " +
                                    (timeLeft / 60) + " minutes and " +
                                    (timeLeft % 60) + " seconds before healing again!");
                            return true;
                        }
                    }
                }

                // Heal the player
                player.setHealth(player.getMaxHealth());
                player.sendMessage(ChatColor.GREEN + "You have been healed to full health!");

                // Set cooldown (unless they have bypass permission)
                if (!player.hasPermission("irisplugin.heal.bypass")) {
                    cooldowns.put(playerUUID, currentTime);
                    saveCooldowns();
                }
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "This command can only be run by players.");
                return true;
            }
        }
        return false;
    }
}