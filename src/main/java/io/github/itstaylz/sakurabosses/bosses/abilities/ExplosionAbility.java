package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.files.YamlFile;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;

public class ExplosionAbility extends ATargetAbility<ExplosionAbility> {

    private final double damage;

    ExplosionAbility() {
        this(TargetType.CLOSEST, 0f);
    }

    ExplosionAbility(TargetType targetType, double damage) {
        super(targetType);
        this.damage = damage;
    }

    @Override
    public ExplosionAbility create(YamlFile yaml, String path) {
        TargetType targetType = loadTargetType(yaml, path);
        double damage = yaml.getConfig().getDouble(path + ".damage");
        return new ExplosionAbility(targetType, damage);
    }

    @Override
    public void activate(EntityBoss entityBoss, LivingEntity target) {
        target.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, target.getLocation().add(0, 0.5, 0), 1);
        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
        target.damage(this.damage, entityBoss.getMobEntity());
    }
}
