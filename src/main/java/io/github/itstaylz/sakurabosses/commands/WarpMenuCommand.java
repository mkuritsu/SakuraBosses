package io.github.itstaylz.sakurabosses.commands;

import io.github.itstaylz.hexlib.utils.StringUtils;
import io.github.itstaylz.sakurabosses.menus.WarpMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpMenuCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("bosswarps")) {
            if (sender instanceof Player player) {
                new WarpMenu().open(player);
            } else {
                sender.sendMessage(StringUtils.colorize("&cThis command can only be executed by players!"));
            }
            return true;
        }
        return false;
    }
}
