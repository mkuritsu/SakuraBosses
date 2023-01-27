package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.file.YamlFile;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.utils.HealthBarUtils;
import io.github.itstaylz.sakurabosses.utils.MobEntityUtils;
import org.bukkit.Location;
import org.bukkit.entity.Mob;

public class MirrorAbility implements IBossAbility<MirrorAbility> {

    private static final int SPAWN_RADIUS = 3;

    private final double maxHealth;
    private final int amount;

    MirrorAbility() {
        this(0, 0);
    }

    MirrorAbility(double maxHealth, int amount) {
        this.maxHealth = maxHealth;
        this.amount = amount;
    }

    @Override
    public MirrorAbility create(YamlFile yaml, String path) {
        double maxHealth = yaml.getConfig().getDouble(path + ".max_health");
        int amount = yaml.getConfig().getInt(path + ".amount");
        return new MirrorAbility(maxHealth, amount);
    }

    @Override
    public void activate(EntityBoss entityBoss) {
        for (int i = 0; i < this.amount; i++) {
            double x = Math.cos(i) * SPAWN_RADIUS;
            double z = Math.sin(i) * SPAWN_RADIUS;
            Location circleLocation = entityBoss.getMobEntity().getLocation().clone().add(x, 0, z);
            Location spawnLocation = circleLocation.getWorld().getHighestBlockAt(circleLocation).getLocation().add(0, 1, 0);
            Mob entity = (Mob) entityBoss.spawnMinion(spawnLocation, entityBoss.getMobEntity().getType());
            MobEntityUtils.setEquipment(entity, entityBoss.getBossData().equipment());
            MobEntityUtils.setMaxHealth(entity, maxHealth);
            entity.setCustomNameVisible(true);
            entity.setCustomName(entityBoss.getBossData().settings().displayName());
            HealthBarUtils.updateHealthBar(entity);
        }
    }
}
