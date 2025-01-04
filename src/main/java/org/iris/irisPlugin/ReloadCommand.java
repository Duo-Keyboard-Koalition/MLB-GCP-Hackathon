package org.iris.irisPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    private final IrisPlugin plugin;

    public ReloadCommand(IrisPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("iris-plugin")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("irisplugin.reload")) {
                    plugin.reloadConfig();
                    sender.sendMessage(ChatColor.GREEN + "IrisPlugin has been reloaded!");
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to reload the plugin!");
                    return true;
                }
            }
        }
        return false;
    }
}
