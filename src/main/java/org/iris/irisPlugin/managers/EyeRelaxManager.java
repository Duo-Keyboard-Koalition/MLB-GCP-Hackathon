package org.iris.irisPlugin.managers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class EyeRelaxManager implements Listener {
    private final Plugin plugin;
    private final HashMap<UUID, Long> playerPlaytime = new HashMap<>();
    private final HashMap<UUID, Long> relaxStartTime = new HashMap<>();
    private final long PLAY_TIME_LIMIT = 30 * 60 * 1000; // 30 minutes
    private final long RELAX_TIME = 15 * 60 * 1000; // 15 minutes

    public EyeRelaxManager(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        startCheckingTimer();
    }

    private void startCheckingTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                checkAllPlayers();
            }
        }.runTaskTimer(plugin, 20L, 20L * 60); // Check every minute
    }

    private void checkAllPlayers() {
        long currentTime = System.currentTimeMillis();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.isOp()) continue; // Skip operators

            UUID playerId = player.getUniqueId();
            if (isInRelaxMode(playerId)) {
                // Check if relax time is over
                if (currentTime - relaxStartTime.get(playerId) >= RELAX_TIME) {
                    releasePlayer(player);
                }
            } else if (playerPlaytime.containsKey(playerId)) {
                // Check if play time limit reached
                if (currentTime - playerPlaytime.get(playerId) >= PLAY_TIME_LIMIT) {
                    enforceRelaxMode(player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.isOp()) {
            UUID playerId = player.getUniqueId();
            if (!playerPlaytime.containsKey(playerId)) {
                playerPlaytime.put(playerId, System.currentTimeMillis());
                player.sendMessage(ChatColor.YELLOW + "Welcome! You have 30 minutes of play time before required eye rest.");
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (isInRelaxMode(player.getUniqueId())) {
            event.setCancelled(true);
            sendRelaxMessage(player);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (isInRelaxMode(player.getUniqueId())) {
            event.setCancelled(true);
            sendRelaxMessage(player);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (isInRelaxMode(player.getUniqueId())) {
            event.setCancelled(true);
            sendRelaxMessage(player);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (isInRelaxMode(player.getUniqueId())) {
                event.setCancelled(true);
                sendRelaxMessage(player);
            }
        }
    }

    private boolean isInRelaxMode(UUID playerId) {
        return relaxStartTime.containsKey(playerId);
    }

    private void enforceRelaxMode(Player player) {
        UUID playerId = player.getUniqueId();
        relaxStartTime.put(playerId, System.currentTimeMillis());
        player.setAllowFlight(true); // Prevent falling
        player.setFlying(true);
        player.sendMessage(ChatColor.RED + "You've reached your play time limit!");
        player.sendMessage(ChatColor.YELLOW + "Please take a 15-minute break to rest your eyes.");
    }

    private void releasePlayer(Player player) {
        UUID playerId = player.getUniqueId();
        relaxStartTime.remove(playerId);
        playerPlaytime.put(playerId, System.currentTimeMillis()); // Reset play time
        player.setAllowFlight(false);
        player.setFlying(false);
        player.sendMessage(ChatColor.GREEN + "Thank you for taking a break! You can now play for another 30 minutes.");
    }

    private void sendRelaxMessage(Player player) {
        long timeLeft = (RELAX_TIME - (System.currentTimeMillis() - relaxStartTime.get(player.getUniqueId()))) / 1000;
        player.sendMessage(ChatColor.RED + "You are in eye relax mode! Please wait " +
                (timeLeft / 60) + " minutes and " + (timeLeft % 60) + " seconds.");
    }
}