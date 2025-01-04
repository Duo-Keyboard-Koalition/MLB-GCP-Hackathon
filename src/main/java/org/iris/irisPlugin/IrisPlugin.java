package org.iris.irisPlugin;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class IrisPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);

        // Pass 'this' (the plugin instance) to HealCommand constructor
        getCommand("heal").setExecutor(new HealCommand(this));
        getCommand("wolves").setExecutor(new WolvesCommand());
        getCommand("iris-plugin").setExecutor(new ReloadCommand(this));

        // Send a message to all online players
        Bukkit.broadcastMessage("The IrisPlugin is now up and running!");

        // Output to the server terminal
        getLogger().info("IrisPlugin has been enabled successfully.");
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