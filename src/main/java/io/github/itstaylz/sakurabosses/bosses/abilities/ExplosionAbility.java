package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.files.YamlFile;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.entity.LivingEntity;

public class ExplosionAbility extends TargetAbility<ExplosionAbility> {

    private float power;

    ExplosionAbility(TargetType targetType, float power) {
        super(targetType);
        this.power = power;
    }

    @Override
    public ExplosionAbility create(YamlFile yaml, String path) {
        TargetType targetType = loadTargetType(yaml, path);
        float power = (float) yaml.getConfig().getDouble(path + ".power");
        return new ExplosionAbility(targetType, power);
    }

    @Override
    public void activate(EntityBoss entityBoss, LivingEntity target) {
        target.getWorld().createExplosion(target.getLocation().add(0, 1, 0), this.power, false, false, entityBoss.getMobEntity());
    }
}
