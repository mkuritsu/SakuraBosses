package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.files.YamlFile;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class ArrowRainAbility extends TargetAbility<ArrowRainAbility> {

    ArrowRainAbility() { }

    ArrowRainAbility(TargetType targetType, double radius) {
        super(targetType);
    }

    @Override
    public ArrowRainAbility create(YamlFile yaml, String path) {
        TargetType targetType = TargetType.valueOf(yaml.getOrDefault(path + ".target", "CURRENT_TARGET"));
        double radius = yaml.getConfig().getDouble(path + ".radius");
        return new ArrowRainAbility(targetType, radius);
    }

    @Override
    public void activate(EntityBoss entityBoss, LivingEntity target) {
        target.getWorld().spawnEntity(target.getLocation().add(0, 5, 0), EntityType.ARROW);
    }
}
