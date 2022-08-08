package io.github.itstaylz.sakurabosses.utils;

import io.github.itstaylz.sakurabosses.SakuraBossesPlugin;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public final class TargetUtils {

    public static boolean canBeTarget(Player player) {
        return player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR &&
                !SakuraBossesPlugin.getEssentials().getUser(player).isVanished()
                && !player.isDead();
    }
}
