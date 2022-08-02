package io.github.itstaylz.sakurabosses.listeners;

import com.earth2me.essentials.Essentials;
import io.github.itstaylz.hexlib.utils.EntityUtils;
import io.github.itstaylz.hexlib.utils.RandomUtils;
import io.github.itstaylz.sakurabosses.bosses.BossManager;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class BossListener implements Listener {

    private final JavaPlugin plugin;
    private final Essentials essentials;

    public BossListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
    }

    @EventHandler
    private void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (BossManager.isBoss(entity)) {
            EntityBoss entityBoss = BossManager.getEntityBoss(entity.getUniqueId());
            new BukkitRunnable() {
                @Override
                public void run() {
                    int multiplier = entityBoss.getBossData().settings().knockBack() ? 1 : 0;
                    entity.setVelocity(entity.getVelocity().multiply(multiplier));
                    entityBoss.updateHealthBar();
                }
            }.runTaskLater(this.plugin, 1L);
        }
    }

    @EventHandler
    private void onChunkLoad(EntitiesLoadEvent event) {
        for (Entity entity : event.getEntities()) {
            if (entity instanceof Mob mob) {
                String bossId = EntityUtils.getPDCValue(entity, BossManager.ENTITY_BOSS_KEY, PersistentDataType.STRING);
                if (bossId != null) {
                    BossManager.loadEntityBoss(mob, bossId);
                }
            }
        }
    }

    @EventHandler
    private void onDeath(EntityDeathEvent event) {
        EntityBoss boss = BossManager.getEntityBoss(event.getEntity().getUniqueId());
        if (boss != null) {
            event.getDrops().clear();
            event.setDroppedExp(0);
            boss.onDeath();
        }
    }

    @EventHandler
    private void onTarget(EntityTargetLivingEntityEvent event) {
        Bukkit.broadcastMessage("TARGET");
        Entity entity = event.getEntity();
        EntityBoss boss = BossManager.getEntityBoss(entity.getUniqueId());
        if (boss != null) {
            TargetType type = boss.getBossData().settings().targetType();
            List<Player> players = boss.getPlayersInRadius();
            Player target = null;
            if (type == TargetType.RANDOM) {
                int index = RandomUtils.RANDOM.nextInt(0, players.size());
                target = players.get(index);
            } else {
                for (Player player : players) {
                    if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR ||
                            this.essentials.getUser(player).isVanished() || player.isDead())
                        continue;
                    if (target == null)
                        target = player;
                    if ((type == TargetType.CLOSEST &&
                            player.getLocation().distance(entity.getLocation()) < target.getLocation().distance(entity.getLocation())) ||
                            (type == TargetType.HIGHEST_HEALTH && player.getHealth() > target.getHealth()) ||
                            (type == TargetType.LOWEST_HEALTH && player.getHealth() < target.getHealth())) {
                        target = player;
                    }
                }
            }
            event.setTarget(target);
        }
    }
}
