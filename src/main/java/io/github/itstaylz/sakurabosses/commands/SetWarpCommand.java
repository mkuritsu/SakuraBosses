package io.github.itstaylz.sakurabosses.commands;

import io.github.itstaylz.hexlib.storage.file.YamlFile;
import io.github.itstaylz.hexlib.utils.StringUtils;
import io.github.itstaylz.sakurabosses.spawning.BossWarp;
import io.github.itstaylz.sakurabosses.spawning.WarpManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class SetWarpCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("setbosswarp")) {
            if (sender instanceof Player player) {
                if (args.length < 1) {
                    sender.sendMessage(StringUtils.colorize("&cUsage: /" + label + " <id>"));
                } else {
                    String warpId = args[0];
                    YamlFile yaml = new YamlFile(new File(WarpManager.WARPS_FOLDER, warpId + ".yml"));
                    yaml.set("location.world", player.getWorld().getName());
                    yaml.set("location.x", player.getLocation().getX());
                    yaml.set("location.y", player.getLocation().getY());
                    yaml.set("location.z", player.getLocation().getZ());
                    yaml.save();
                    if (!WarpManager.warpExists(warpId)) {
                        BossWarp warp = BossWarp.createFromCommand(warpId, new Location(player.getWorld(),
                                player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
                        WarpManager.addWarp(warpId, warp);
                    } else {
                        BossWarp warp = WarpManager.getWarp(warpId);
                        warp.setLocation(player.getLocation());

                    }
                    player.sendMessage(StringUtils.colorize("&aWarp has been set!"));
                }
            } else {
                sender.sendMessage(StringUtils.colorize("&cThis command can only be executed by players!"));
            }
            return true;
        }
        return false;
    }
}
