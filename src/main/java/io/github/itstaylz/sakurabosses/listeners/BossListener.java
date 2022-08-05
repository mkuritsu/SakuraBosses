package io.github.itstaylz.sakurabosses.listeners;

import io.github.itstaylz.hexlib.utils.EntityUtils;
import io.github.itstaylz.sakurabosses.bosses.BossManager;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.events.BossDamagePlayerEvent;
import io.github.itstaylz.sakurabosses.events.PlayerDamageBossEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BossListener implements Listener {

    private final JavaPlugin plugin;

    public BossListener(JavaPlugin plugin) {
        this.plugin = plugin;
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
    private void onEntitiesLoad(EntitiesLoadEvent event) {
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
    private void onHit(ProjectileHitEvent event) {
        if (event.getHitEntity() != null && event.getEntityType() != EntityType.ARROW) {
            EntityBoss boss = BossManager.getEntityBoss(event.getHitEntity().getUniqueId());
            if (boss != null) {
                if (event.getEntity().getShooter() != null && event.getEntity().getShooter() == event.getHitEntity())
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        Entity damager = event.getDamager();
        EntityBoss victimBoss = BossManager.getEntityBoss(victim.getUniqueId());
        EntityBoss damagerBoss = BossManager.getEntityBoss(damager.getUniqueId());
        if (victimBoss != null && damager instanceof Arrow arrow) {
            if (arrow.getShooter() != null && arrow.getShooter().equals(victim)) {
                event.setCancelled(true);
            }
        }
        if (damagerBoss != null && event.getEntity() instanceof Player player)
            Bukkit.getPluginManager().callEvent(new BossDamagePlayerEvent(event, player, damagerBoss));
        else if (victimBoss != null && damager instanceof Player player)
            Bukkit.getPluginManager().callEvent(new PlayerDamageBossEvent(event, player, victimBoss));
    }

    @EventHandler
    private void onBossDamage(BossDamagePlayerEvent event) {
        event.getEntityBoss().triggerEffects(event);
    }

    @EventHandler
    private void onPlayerDamage(PlayerDamageBossEvent event) {
        event.getEntityBoss().triggerEffects(event);
    }
}
