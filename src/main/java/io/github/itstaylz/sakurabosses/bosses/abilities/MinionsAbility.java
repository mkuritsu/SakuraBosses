package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.files.YamlFile;
import io.github.itstaylz.hexlib.utils.StringUtils;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.utils.HealthBarUtils;
import io.github.itstaylz.sakurabosses.utils.MobEntityUtils;
import io.github.itstaylz.sakurabosses.utils.YamlUtils;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.ItemStack;

public class MinionsAbility implements IBossAbility<MinionsAbility> {

    private static final int SPAWN_RADIUS = 3;

    private final EntityType entityType;
    private final double maxHealth;
    private final ItemStack[] equipment;
    private final int amount;

    private final String displayName;

    MinionsAbility() {
        this(EntityType.ZOMBIE, 0, null, 0, "");
    }

    MinionsAbility(EntityType entityType, double maxHealth, ItemStack[] equipment, int amount, String displayName) {
        this.entityType = entityType;
        this.maxHealth = maxHealth;
        this.equipment = equipment;
        this.amount = amount;
        this.displayName = displayName;
    }

    @Override
    public MinionsAbility create(YamlFile yaml, String path) {
        EntityType entityType = EntityType.valueOf(yaml.getConfig().getString(path + ".entity_type").toUpperCase());
        double maxHealth = yaml.getConfig().getDouble(path + ".max_health");
        int amount = yaml.getConfig().getInt(path + ".amount");
        String displayName = StringUtils.fullColorize(yaml.getConfig().getString(path + ".display_name"));
        ItemStack[] equipment = new ItemStack[5];
        equipment[0] = YamlUtils.loadItemStack(yaml, path + ".equipment.weapon");
        equipment[1] = YamlUtils.loadItemStack(yaml, path + ".equipment.helmet");
        equipment[2] = YamlUtils.loadItemStack(yaml, path + ".equipment.chestplate");
        equipment[3] = YamlUtils.loadItemStack(yaml, path + ".equipment.leggings");
        equipment[4] = YamlUtils.loadItemStack(yaml, path + ".equipment.boots");
        return new MinionsAbility(entityType, maxHealth, equipment, amount, displayName);
    }

    @Override
    public void activate(EntityBoss entityBoss) {
        for (int i = 0; i < this.amount; i++) {
            double x = Math.cos(i) * SPAWN_RADIUS;
            double z = Math.sin(i) * SPAWN_RADIUS;
            Location circleLocation = entityBoss.getMobEntity().getLocation().clone().add(x, 0, z);
            Location spawnLocation = circleLocation.getWorld().getHighestBlockAt(circleLocation).getLocation().add(0, 1, 0);
            Mob entity = (Mob) entityBoss.spawnMinion(spawnLocation, this.entityType);
            entity.setCustomNameVisible(true);
            entity.setCustomName(this.displayName);
            entity.setTarget(entityBoss.getMobEntity().getTarget());
            MobEntityUtils.setMaxHealth(entity, this.maxHealth);
            MobEntityUtils.setEquipment(entity, this.equipment);
            HealthBarUtils.updateHealthBar(entity);
        }
    }
}
