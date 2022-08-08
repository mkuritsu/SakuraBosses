package io.github.itstaylz.sakurabosses.bosses.effects;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public record DurabilityEffect(int damageAmount) implements IBossEffect {

    public void applyArmorDamage(Player player) {
        applyDamage(player.getInventory().getHelmet());
        applyDamage(player.getInventory().getChestplate());
        applyDamage(player.getInventory().getLeggings());
        applyDamage(player.getInventory().getBoots());
    }

    private void applyDamage(ItemStack itemStack) {
        if (itemStack != null && itemStack.getItemMeta() != null && itemStack.getItemMeta() instanceof Damageable damageable) {
            damageable.setDamage(damageable.getDamage() + damageAmount);
            itemStack.setItemMeta(damageable);
        }
    }
}
