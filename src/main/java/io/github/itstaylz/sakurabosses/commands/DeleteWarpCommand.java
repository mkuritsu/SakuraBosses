package io.github.itstaylz.sakurabosses.commands;

import io.github.itstaylz.hexlib.storage.file.YamlFile;
import io.github.itstaylz.hexlib.utils.StringUtils;
import io.github.itstaylz.sakurabosses.spawning.WarpManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class DeleteWarpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("deletebosswarp")) {
            if (sender instanceof Player player) {
                if (args.length < 1) {
                    sender.sendMessage(StringUtils.colorize("&cUsage: /" + label + " <id>"));
                } else {
                    String warpId = args[0];
                    if (WarpManager.warpExists(warpId)) {
                        WarpManager.removeWarp(warpId);
                        YamlFile yaml = new YamlFile(new File(WarpManager.WARPS_FOLDER, warpId + ".yml"));
                        yaml.deleteFile();
                        player.sendMessage(StringUtils.colorize("&aWarp has been deleted!"));
                    } else {
                        player.sendMessage(StringUtils.colorize("&cThat warp does not exists!"));
                    }
                }
            } else {
                sender.sendMessage(StringUtils.colorize("&cThis command can only be executed by players!"));
            }
            return true;
        }
        return false;
    }
}
