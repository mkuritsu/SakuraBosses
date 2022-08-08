package io.github.itstaylz.sakurabosses.utils;

import io.github.itstaylz.sakurabosses.bosses.data.BossEquipmentItem;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public final class MobEntityUtils {

    public static void setEquipment(LivingEntity entity, BossEquipmentItem[] equipment) {
        ItemStack[] items = new ItemStack[equipment.length];
        for (int i = 0; i < equipment.length; i++) {
            items[i] = equipment[i].itemStack();
        }
        setEquipment(entity, items);
    }

    public static void setEquipment(LivingEntity entity, ItemStack[] equipment) {
        if (entity.getEquipment() != null) {
            entity.getEquipment().setItemInMainHand(equipment[0]);
            entity.getEquipment().setHelmet(equipment[1]);
            entity.getEquipment().setChestplate(equipment[2]);
            entity.getEquipment().setLeggings(equipment[3]);
            entity.getEquipment().setBoots(equipment[4]);
        }
    }

    public static void setMaxHealth(LivingEntity entity, double maxHealth) {
        AttributeInstance healthAttribute = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (healthAttribute != null) {
            healthAttribute.setBaseValue(maxHealth);
            entity.setHealth(maxHealth);
        }
    }
}
