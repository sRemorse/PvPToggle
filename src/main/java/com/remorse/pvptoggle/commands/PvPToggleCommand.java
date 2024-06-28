package com.remorse.pvptoggle.commands;

import com.remorse.pvptoggle.PvPToggle;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class PvPToggleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (args.length == 0){
            if (!(sender instanceof Player player)){
                sender.sendMessage("Console cannot use this!");
                return true;
            }

            boolean pvpStatus;
            try {
                pvpStatus = PvPToggle.pvpDatabase.getPlayerPvpStatus(player);
                PvPToggle.log.info(player.getName() + " PVP Status is: " + pvpStatus);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (pvpStatus){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&8| &c&lPVP&8 | &4Enabled"));
                return true;
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&8| &c&lPVP&8 | &7Disabled"));
            return true;
        }

        if (args.length == 1){
            switch (args[0].toLowerCase()){
                case "on" -> {
                    try {
                        togglePvP((Player) sender, true);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                case "off" -> {
                    try {
                        togglePvP((Player) sender, false);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                default -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&8| &c&lPVP&8 | &7Invalid command"));
            }
        }
        return true;
    }

    private void togglePvP(Player player, boolean newPvpState) throws SQLException {
        if(newPvpState){
            PvPToggle.pvpDatabase.updatePvpStatus(player, true);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&8| &c&lPVP&8 | &4Enabled"));
        }

        if(!newPvpState){
            PvPToggle.pvpDatabase.updatePvpStatus(player, false);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&8| &c&lPVP&8 | &7Disabled"));
        }

    }
}
