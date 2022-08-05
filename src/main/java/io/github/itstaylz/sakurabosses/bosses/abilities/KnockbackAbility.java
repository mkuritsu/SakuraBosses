package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.files.YamlFile;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class KnockbackAbility extends TargetAbility<KnockbackAbility> {

    private double horizontal, vertical, radius;

    public KnockbackAbility(TargetType targetType, double horizontal, double vertical, double radius) {
        super(targetType);
        this.horizontal = horizontal;
        this.vertical = vertical;
        this.radius = radius;
    }

    @Override
    public KnockbackAbility create(YamlFile yaml, String path) {
        TargetType targetType = loadTargetType(yaml, path);
        double horizontal = yaml.getConfig().getDouble(path + ".horizontal");
        double vertical = yaml.getConfig().getDouble(path + ".vertical");
        double radius = yaml.getConfig().getDouble(path + ".radius");
        return new KnockbackAbility(targetType, horizontal, vertical, radius);
    }

    @Override
    public void activate(EntityBoss entityBoss, LivingEntity target) {
        if (target.getLocation().distance(entityBoss.getMobEntity().getLocation()) < this.radius) {
            Vector direction = target.getLocation().subtract(entityBoss.getMobEntity().getLocation()).toVector().normalize();
            Vector knockback = direction.setY(1).multiply(new Vector(this.horizontal, this.vertical, this.horizontal));
            target.setVelocity(knockback);
        }
    }
}
