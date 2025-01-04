package org.duoKeyboardKoalition.irisPlugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class FoodCommand implements CommandExecutor {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final File cooldownFile;
    private final FileConfiguration cooldownConfig;
    private final Plugin plugin;

    public FoodCommand(Plugin plugin) {
        this.plugin = plugin;
        this.cooldownFile = new File(plugin.getDataFolder(), "food_cooldowns.yml");
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
            plugin.getLogger().warning("Could not save food cooldown data!");
        }
    }

    @Override
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
            // 15 minutes in milliseconds
            // long COOLDOWN_TIME = (long) plugin.getConfig().getInt("cooldowns.wolves", 30) * 60 * 1000;
            //
            long COOLDOWN_TIME = (long) plugin.getConfig().getInt("cooldowns.food", 15) * 60 * 1000;
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
        // read the food items from the config
        int beef_amount = plugin.getConfig().getInt("food.beef_amount", 8);
        int chicken_amount = plugin.getConfig().getInt("food.chicken_amount", 8);
        int pork_amount = plugin.getConfig().getInt("food.pork_amount", 8);
        // Give food items
        ItemStack beef = new ItemStack(Material.BEEF, beef_amount);
        ItemStack chicken = new ItemStack(Material.CHICKEN, chicken_amount);
        ItemStack pork = new ItemStack(Material.PORKCHOP, pork_amount);


        player.getWorld().dropItemNaturally(player.getLocation(), beef);
        player.getWorld().dropItemNaturally(player.getLocation(), chicken);
        player.getWorld().dropItemNaturally(player.getLocation(), pork);

        // Set cooldown and save to file
        cooldowns.put(playerId, System.currentTimeMillis());
        saveCooldowns();

        player.sendMessage(ChatColor.GREEN + "You have received raw meat!");
        player.sendMessage(ChatColor.YELLOW + "You can use this command again in 15 minutes.");

        return true;
    }
}