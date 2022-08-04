package io.github.itstaylz.sakurabosses.commands;

import io.github.itstaylz.hexlib.utils.StringUtils;
import io.github.itstaylz.sakurabosses.bosses.BossManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BossReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("bossesreload")) {
            BossManager.loadBosses();
            sender.sendMessage(StringUtils.colorize("&aReloaded!"));
            return true;
        }
        return false;
    }
}
