package org.duoKeyboardKoalition.irisPlugin.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Pig;
import org.bukkit.entity.MushroomCow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class LivestockCommand implements CommandExecutor {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final File cooldownFile;
    private final FileConfiguration cooldownConfig;
    private final Plugin plugin;

    public LivestockCommand(Plugin plugin) {
        this.plugin = plugin;
        this.cooldownFile = new File(plugin.getDataFolder(), "livestock_cooldowns.yml");
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
            plugin.getLogger().warning("Could not save livestock cooldown data!");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("livestock")) {
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
                long COOLDOWN_TIME = (long) plugin.getConfig().getInt("cooldowns.livestock", 30) * 60 * 1000;
                if (timeElapsed < COOLDOWN_TIME) {
                    // Calculate remaining time
                    long remainingTime = (COOLDOWN_TIME - timeElapsed) / 1000;
                    long minutes = remainingTime / 60;
                    long seconds = remainingTime % 60;

                    player.sendMessage(String.format("§cYou must wait %d minutes and %d seconds before summoning livestock again!",
                            minutes, seconds));
                    return true;
                }
            }

            // Summon livestock
            Location location = player.getLocation();
            player.sendMessage("§6Summoning your livestock:");

            for (int i = 0; i < 2; i++) {
                Sheep sheep = player.getWorld().spawn(location, Sheep.class);
                sheep.setColor(org.bukkit.DyeColor.WHITE);
                player.sendMessage("§7- A white sheep has been summoned!");

                Pig pig = player.getWorld().spawn(location, Pig.class);
                player.sendMessage("§7- A pig has been summoned!");

                MushroomCow mooshroom = player.getWorld().spawn(location, MushroomCow.class);
                player.sendMessage("§7- A mooshroom has been summoned!");
            }

            // Give 16 eggs to the player
            ItemStack eggs = new ItemStack(Material.EGG, 16);
            //player.getInventory().addItem(eggs); drop the eggs as an item
            player.getWorld().dropItemNaturally(player.getLocation(), eggs);

            player.sendMessage("§7- You have received 16 eggs!");

            // Drop 6 leads at the player's location
            ItemStack leads = new ItemStack(Material.LEAD, 6);
            player.getWorld().dropItemNaturally(location, leads);
            player.sendMessage("§7- You have received 6 leads");

            // Set cooldown and save to file
            cooldowns.put(playerId, System.currentTimeMillis());
            saveCooldowns();

            int cooldownMinutes = plugin.getConfig().getInt("cooldowns.livestock", 30);
            player.sendMessage("§aYour livestock has been summoned!");
            player.sendMessage(String.format("§eYou can summon more livestock in %d minutes.", cooldownMinutes));

            return true;
        }
        return false;
    }
}