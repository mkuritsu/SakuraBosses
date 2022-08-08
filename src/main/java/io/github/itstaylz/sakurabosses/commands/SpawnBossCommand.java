package io.github.itstaylz.sakurabosses.commands;

import io.github.itstaylz.hexlib.utils.StringUtils;
import io.github.itstaylz.sakurabosses.bosses.BossManager;
import io.github.itstaylz.sakurabosses.bosses.data.BossData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpawnBossCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("spawnboss")) {
            if (args.length < 1) {
                sender.sendMessage(StringUtils.colorize("&cUse /" + label + " <boss id>"));
                return true;
            }
            String bossId = args[0];
            BossData data = BossManager.getBossData(bossId);
            if (data == null) {
                sender.sendMessage(StringUtils.colorize("&cThe boss '" + bossId + "' does not exists!"));
                return true;
            }
            if (sender instanceof Player player)
                BossManager.spawnBoss(bossId, player.getLocation());
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("spawnboss")) {
            if (args.length == 1) {
                return new ArrayList<>(BossManager.getAllIds());
            }
        }
        return new ArrayList<>();
    }
}
