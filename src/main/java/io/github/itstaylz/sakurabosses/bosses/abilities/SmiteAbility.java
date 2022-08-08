package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.files.YamlFile;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;

public class SmiteAbility extends TargetAbility<SmiteAbility> {

    private final double damage;

    SmiteAbility() {
        this(TargetType.CLOSEST, 0);
    }

    SmiteAbility(TargetType targetType, double damage) {
        super(targetType);
        this.damage = damage;
    }

    @Override
    public SmiteAbility create(YamlFile yaml, String path) {
        TargetType targetType = loadTargetType(yaml, path);
        double damage = yaml.getConfig().getDouble(path + ".damage");
        return new SmiteAbility(targetType, damage);
    }

    @Override
    public void activate(EntityBoss entityBoss, LivingEntity target) {
        target.getWorld().strikeLightningEffect(target.getLocation());
        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1f, 1f);
        target.damage(this.damage, entityBoss.getMobEntity());
    }
}
