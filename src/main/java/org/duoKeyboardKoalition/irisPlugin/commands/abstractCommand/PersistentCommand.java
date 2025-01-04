package org.duoKeyboardKoalition.irisPlugin.commands.abstractCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public abstract class PersistentCommand implements CommandExecutor {
    protected final HashMap<UUID, Long> cooldowns = new HashMap<>();
    protected final File cooldownFile;
    protected final FileConfiguration cooldownConfig;
    protected final Plugin plugin;
    protected final String commandName;

    public PersistentCommand(Plugin plugin, String commandName) {
        this.plugin = plugin;
        this.commandName = commandName;
        this.cooldownFile = new File(plugin.getDataFolder(), commandName + "_cooldowns.yml");
        this.cooldownConfig = YamlConfiguration.loadConfiguration(cooldownFile);
        loadCooldowns();
    }

    protected void loadCooldowns() {
        if (cooldownConfig.contains("cooldowns")) {
            for (String uuid : cooldownConfig.getConfigurationSection("cooldowns").getKeys(false)) {
                cooldowns.put(UUID.fromString(uuid),
                        cooldownConfig.getLong("cooldowns." + uuid));
            }
        }
    }

    protected void saveCooldowns() {
        for (UUID uuid : cooldowns.keySet()) {
            cooldownConfig.set("cooldowns." + uuid.toString(), cooldowns.get(uuid));
        }
        try {
            cooldownConfig.save(cooldownFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save " + commandName + " cooldown data!");
        }
    }

    protected boolean checkCooldown(Player player, long cooldownTime) {
        UUID playerId = player.getUniqueId();
        if (cooldowns.containsKey(playerId)) {
            long timeElapsed = System.currentTimeMillis() - cooldowns.get(playerId);
            if (timeElapsed < cooldownTime) {
                long remainingTime = (cooldownTime - timeElapsed) / 1000;
                long minutes = remainingTime / 60;
                long seconds = remainingTime % 60;
                player.sendMessage(ChatColor.RED +
                        String.format("You must wait %d minutes and %d seconds before using %s again!",
                                minutes, seconds, commandName));
                return false;
            }
        }
        return true;
    }

    protected void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        saveCooldowns();
    }

    protected long getCooldownTime() {
        return plugin.getConfig().getLong("cooldowns." + commandName, 30) * 60 * 1000;
    }
}