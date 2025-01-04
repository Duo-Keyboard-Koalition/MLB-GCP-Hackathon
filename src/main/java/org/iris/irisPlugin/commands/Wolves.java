package org.iris.irisPlugin.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class Wolves implements CommandExecutor {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final File cooldownFile;
    private final FileConfiguration cooldownConfig;
    private final Plugin plugin;
    private final Random random = new Random();

    // List of possible wolf names
    private final String[] wolfNames = {
            "Luna", "Shadow", "Ghost", "Storm", "Fang",
            "Wolf", "Alpine", "Arctic", "Aspen", "Atlas",
            "Bandit", "Bear", "Blitz", "Boreal", "Chase",
            "Cloud", "Comet", "Creek", "Dash", "Dawn",
            "Echo", "Forest", "Frost", "Glacier", "Haven",
            "Hunter", "Ice", "Juneau", "Knight", "Legend",
            "Maple", "Mountain", "North", "Oak", "Phoenix",
            "River", "Rocky", "Sage", "Scout", "Sierra",
            "Silver", "Snow", "Star", "Thunder", "Timber",
            "Tundra", "Valley", "Winter", "Wisdom", "Yukon"
    };

    public Wolves(Plugin plugin) {
        this.plugin = plugin;
        this.cooldownFile = new File(plugin.getDataFolder(), "wolves_cooldowns.yml");
        this.cooldownConfig = YamlConfiguration.loadConfiguration(cooldownFile);
        loadCooldowns();
    }

    private String getRandomWolfName() {
        String baseName = wolfNames[random.nextInt(wolfNames.length)];
        return baseName + "-" + (random.nextInt(999) + 1);
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
            plugin.getLogger().warning("Could not save wolves cooldown data!");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("wolves")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cThis command can only be run by players.");
                return true;
            }

            Player player = (Player) sender;
            UUID playerId = player.getUniqueId();

            // Check if player is in cooldown
            if (cooldowns.containsKey(playerId)) {
                long timeElapsed = System.currentTimeMillis() - cooldowns.get(playerId);

                // get the config from the config file
                long COOLDOWN_TIME = (long) plugin.getConfig().getInt("cooldowns.wolves", 30) * 60 * 1000;
                if (timeElapsed < COOLDOWN_TIME) {
                    // Calculate remaining time
                    long remainingTime = (COOLDOWN_TIME - timeElapsed) / 1000;
                    long minutes = remainingTime / 60;
                    long seconds = remainingTime % 60;

                    player.sendMessage(String.format("§cYou must wait %d minutes and %d seconds before summoning wolves again!",
                            minutes, seconds));
                    return true;
                }
            }

            // Summon wolves
            Location location = player.getLocation();
            player.sendMessage("§6Summoning your pack of loyal wolves:");

            for (int i = 0; i < 5; i++) {
                Wolf wolf = player.getWorld().spawn(location, Wolf.class);
                String wolfName = getRandomWolfName();

                // Set the wolf's name and make it visible
                wolf.setCustomName("§e" + wolfName);
                wolf.setCustomNameVisible(true);

                wolf.setOwner(player);
                wolf.setTamed(true);
                wolf.setSitting(true);

                // Announce each wolf's name
                player.sendMessage("§7- " + wolfName + " §ehas joined your pack!");
            }

            // Set cooldown and save to file
            cooldowns.put(playerId, System.currentTimeMillis());
            saveCooldowns();

            int cooldownMinutes = plugin.getConfig().getInt("cooldowns.wolves", 30);
            player.sendMessage("§aYour wolf pack has been summoned!");
            player.sendMessage(String.format("§eYou can summon another pack in %d minutes.", cooldownMinutes));

            return true;
        }
        return false;
    }
}