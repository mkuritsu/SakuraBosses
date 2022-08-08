package io.github.itstaylz.sakurabosses.bosses;

import io.github.itstaylz.sakurabosses.SakuraBossesPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public final class BossTargeting {

    private final SakuraBossesPlugin plugin;

    public BossTargeting(SakuraBossesPlugin plugin) {
        this.plugin = plugin;
    }

    public void startTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (EntityBoss boss : BossManager.getAllActiveBossEntities())
                    boss.updateTarget();
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public static void removeTarget(Player player) {
        for (EntityBoss boss : BossManager.getAllActiveBossEntities()) {
            if (boss.getMobEntity() != null && boss.getMobEntity().getTarget() != null && boss.getMobEntity().getTarget().equals(player)) {
                boss.updateTarget();
            }
        }
    }
}
