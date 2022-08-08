package io.github.itstaylz.sakurabosses.listeners;

import io.github.itstaylz.sakurabosses.bosses.BossManager;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.effects.DurabilityEffect;
import io.github.itstaylz.sakurabosses.bosses.effects.ImmunityEffect;
import io.github.itstaylz.sakurabosses.bosses.effects.LifeStealEffect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

public final class EffectListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onDamage(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        Entity victim = event.getHitEntity();
        if (victim != null) {
            EntityBoss boss = BossManager.getEntityBoss(victim.getUniqueId());
            if (boss != null && boss.hasEffect(ImmunityEffect.class)) {
                ImmunityEffect immunityEffect = boss.getActiveEffect(ImmunityEffect.class);
                for (String ignored : immunityEffect.ignoredProjectiles()) {
                    if (projectile.getType().name().contains(ignored)) {
                        event.setCancelled(true);
                        break;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onDamage(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        Entity damager = event.getDamager();
        EntityBoss victimBoss = BossManager.getEntityBoss(victim.getUniqueId());
        EntityBoss damagerBoss = BossManager.getEntityBoss(damager.getUniqueId());

        if (victimBoss != null && damager instanceof Player player && event.getCause().name().contains("ATTACK") &&
                victimBoss.hasEffect(ImmunityEffect.class)) { // PLAYER ATTACKING BOSS
            ItemStack item = player.getInventory().getItemInMainHand();
            ImmunityEffect immunityEffect = victimBoss.getActiveEffect(ImmunityEffect.class);
            if (item.getType() != Material.BOW && item.getType() != Material.CROSSBOW) {
                for (String ignored : immunityEffect.ignoredWeapons()) {
                    if (item.getType().name().contains(ignored)) {
                        event.setCancelled(true);
                        break;
                    }
                }
            }
        } else if (damagerBoss != null && victim instanceof Player player && event.getCause().name().contains("ATTACK")) { // BOSS ATTACKING PLAYER
            // LIFE STEAL
            LifeStealEffect lifeStealEffect = damagerBoss.getActiveEffect(LifeStealEffect.class);
            if (lifeStealEffect != null) {
                double heal = event.getDamage() * lifeStealEffect.multiplier();
                double healCapped = Math.min(heal, damagerBoss.getBossData().settings().maxHealth());
                damagerBoss.getMobEntity().setHealth(healCapped);
                damagerBoss.updateHealth();
            }

            // DURABILITY
            DurabilityEffect durabilityEffect = damagerBoss.getActiveEffect(DurabilityEffect.class);
            if (durabilityEffect != null)
                durabilityEffect.applyArmorDamage(player);
        }
    }
}
