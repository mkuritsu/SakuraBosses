package io.github.itstaylz.sakurabosses.listeners;

import io.github.itstaylz.hexlib.utils.PDCUtils;
import io.github.itstaylz.sakurabosses.bosses.BossDataKeys;
import io.github.itstaylz.sakurabosses.bosses.BossManager;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.persistence.PersistentDataType;

public class AbilityListener implements Listener {

    // Disables block explosions and applies explosion damage from abilities such as Bomb and Explosion

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (BossManager.isMinion(entity)) {
            event.setCancelled(true);
            entity.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, entity.getLocation(), 1);
            entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
        }
    }

    @EventHandler
    private void onDamageByEntity(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        Entity damager = event.getDamager();
        if (victim instanceof Player) {
            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && BossManager.isMinion(damager)) {
                Double minionDamage = PDCUtils.getPDCValue(damager, BossDataKeys.MINION_DAMAGE_KEY, PersistentDataType.DOUBLE);
                double damage = 0;
                if (minionDamage != null)
                    damage = minionDamage;
                event.setDamage(damage);
            }
        }
    }
}
