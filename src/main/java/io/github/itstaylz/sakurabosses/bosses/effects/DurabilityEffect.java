package io.github.itstaylz.sakurabosses.bosses.effects;

import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.events.BossDamagePlayerEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class DurabilityEffect implements IBossEffect<BossDamagePlayerEvent> {

    private int damageAmount;

    public DurabilityEffect(int damageAmount) {
        this.damageAmount = damageAmount;
    }

    @Override
    public Class<BossDamagePlayerEvent> getEventClass() {
        return BossDamagePlayerEvent.class;
    }

    @Override
    public void activate(EntityBoss boss, BossDamagePlayerEvent event) {
        Player player = event.getPlayer();
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor != null && armor.getItemMeta() instanceof Damageable damageable) {
                damageable.setDamage(damageable.getDamage() + this.damageAmount);
                armor.setItemMeta(damageable);
            }
        }
    }
}
