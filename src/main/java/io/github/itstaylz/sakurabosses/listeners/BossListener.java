package io.github.itstaylz.sakurabosses.listeners;

import io.github.itstaylz.hexlib.utils.EntityUtils;
import io.github.itstaylz.sakurabosses.bosses.BossDataKeys;
import io.github.itstaylz.sakurabosses.bosses.BossManager;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.events.BossDamagePlayerEvent;
import io.github.itstaylz.sakurabosses.events.PlayerDamageBossEvent;
import io.github.itstaylz.sakurabosses.utils.HealthBarUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

public class BossListener implements Listener {

    private final JavaPlugin plugin;

    public BossListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }


    // Load bosses into memory on server startup
    //-------------------------------------------------------------------------------------------------------
    @EventHandler
    private void onEntitiesLoad(EntitiesLoadEvent event) {
        for (Entity entity : event.getEntities()) {
            if (entity instanceof Mob mob) {
                String bossId = EntityUtils.getPDCValue(entity, BossDataKeys.ENTITY_BOSS_KEY, PersistentDataType.STRING);
                if (bossId != null) {
                    BossManager.loadEntityBoss(mob, bossId);
                }
            }
        }
    }

    // Update boss health bar and disable knockback if necessary
    //-------------------------------------------------------------------------------------------------------
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (BossManager.isBoss(entity) || BossManager.isMinion(entity)) {
            EntityBoss entityBoss = BossManager.getEntityBoss(entity.getUniqueId());
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (entityBoss != null) {
                        int multiplier = entityBoss.getBossData().settings().knockBack() ? 1 : 0;
                        entity.setVelocity(entity.getVelocity().multiply(multiplier));
                        entityBoss.updateHealth();
                    } else if (entity instanceof LivingEntity livingEntity)
                        HealthBarUtils.updateHealthBar(livingEntity);
                }
            }.runTaskLater(this.plugin, 1L);
        }
    }

    // Disable projectile  hitting boss
    //-------------------------------------------------------------------------------------------------------

    @EventHandler
    private void onHit(ProjectileHitEvent event) {
        Entity victim = event.getHitEntity();
        ProjectileSource shooter = event.getEntity().getShooter();
        if (victim != null && !(victim instanceof Player) && shooter instanceof Mob mob && BossManager.isBoss(mob)) {
            event.setCancelled(true);
        }
    }


    // Disables minions and boss drops and call boss death
    //-------------------------------------------------------------------------------------------------------
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        EntityBoss boss = BossManager.getEntityBoss(event.getEntity().getUniqueId());
        if (BossManager.isMinion(entity) || boss != null) {
            event.getDrops().clear();
            event.setDroppedExp(0);
        }
        if (boss != null)
            boss.onDeath();
    }

    // Call custom events and disable minion damaging boss
    //-------------------------------------------------------------------------------------------------------
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onDamage(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        Entity damager = event.getDamager();
        EntityBoss victimBoss = BossManager.getEntityBoss(victim.getUniqueId());
        EntityBoss damagerBoss = BossManager.getEntityBoss(damager.getUniqueId());
        if (victimBoss != null) {
            if (BossManager.isMinion(damager))
                event.setCancelled(true);
            else if (damager instanceof Player player)
                Bukkit.getPluginManager().callEvent(new PlayerDamageBossEvent(event, player, victimBoss));
        } else if (damagerBoss != null && event.getEntity() instanceof Player player)
            Bukkit.getPluginManager().callEvent(new BossDamagePlayerEvent(event, player, damagerBoss));
    }


    // Custom events to trigger effects
    //-------------------------------------------------------------------------------------------------------
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBossDamage(BossDamagePlayerEvent event) {
        event.getEntityBoss().triggerEffects(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlayerDamage(PlayerDamageBossEvent event) {
        event.getEntityBoss().triggerEffects(event);
    }

    // Disable entity functionality for boss and minions
    //-------------------------------------------------------------------------------------------------------
    @EventHandler
    private void onItemPickup(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (BossManager.isBoss(entity) || BossManager.isMinion(entity))
            event.setCancelled(true);
    }

    @EventHandler
    private void onMobGrief(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        if (BossManager.isMinion(entity) || BossManager.isBoss(entity))
            event.setCancelled(true);
    }
}
