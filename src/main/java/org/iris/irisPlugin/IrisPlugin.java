package org.iris.irisPlugin;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.iris.irisPlugin.commands.FoodCommand;
import org.iris.irisPlugin.commands.HealCommand;
import org.iris.irisPlugin.commands.WolvesCommand;
import org.iris.irisPlugin.managers.EyeRelaxManager;
import java.util.Objects;

public final class IrisPlugin extends JavaPlugin implements Listener {
    private EyeRelaxManager eyeRelaxManager;
    @Override
    public void onEnable() {

        eyeRelaxManager = new EyeRelaxManager(this);

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        loadConfiguration(); // Load plugin configuration
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);

        // Pass 'this' (the plugin instance) to HealCommand constructor
        Objects.requireNonNull(getCommand("heal")).setExecutor(new HealCommand(this));
        Objects.requireNonNull(getCommand("wolves")).setExecutor(new WolvesCommand(this));
        Objects.requireNonNull(getCommand("food")).setExecutor(new FoodCommand(this));

        // Send a message to all online players
        Bukkit.broadcastMessage("The IrisPlugin is now up and running!");

        // Output to the server terminal
        getLogger().info("IrisPlugin has been enabled successfully.");
    }
    private void loadConfiguration() {
        // Log cooldown values
        getLogger().info("=== Loading Plugin Configuration ===");
        getLogger().info("Cooldowns (minutes):");
        getLogger().info("- Food: " + getConfig().getInt("cooldowns.food", 15));
        getLogger().info("- Heal: " + getConfig().getInt("cooldowns.heal", 30));
        getLogger().info("- Wolves: " + getConfig().getInt("cooldowns.wolves", 30));

        // Log food amounts
        getLogger().info("Food Amounts:");
        getLogger().info("- Beef: " + getConfig().getInt("food.beef_amount", 4));
        getLogger().info("- Chicken: " + getConfig().getInt("food.chicken_amount", 4));
        getLogger().info("- Pork: " + getConfig().getInt("food.pork_amount", 4));

        // Verify all required config sections exist
        if (!getConfig().contains("cooldowns")) {
            getLogger().warning("Cooldowns section missing in config.yml");
        }
        if (!getConfig().contains("food")) {
            getLogger().warning("Food section missing in config.yml");
        }
        if (!getConfig().contains("messages")) {
            getLogger().warning("Messages section missing in config.yml");
        }
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Send a welcome message to the player
        event.getPlayer().sendMessage("IRIS-PLUGIN: Welcome to the server, " + event.getPlayer().getName() + "!");
    }
}