package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.files.YamlFile;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;

public class ArrowRainAbility extends TargetAbility<ArrowRainAbility> {

    private static final double RADIUS_PRECISION = 0.5;
    private static final int ANGLE_PRECISION_INCREMENTS = 5;
    private static final int STARTING_ANGLE_PRECISION = 5;

    private final double radius, height;

    ArrowRainAbility(TargetType targetType, double radius, double height) {
        super(targetType);
        this.radius = radius;
        this.height = height;
    }

    @Override
    public ArrowRainAbility create(YamlFile yaml, String path) {
        TargetType targetType = loadTargetType(yaml, path);
        double radius = yaml.getConfig().getDouble(path + ".radius");
        double height = yaml.getConfig().getDouble(path + ".height");
        return new ArrowRainAbility(targetType, radius, height);
    }

    @Override
    public void activate(EntityBoss entityBoss, LivingEntity target) {
        double anglePrecision = STARTING_ANGLE_PRECISION;
        for (double radius = this.radius; radius > 0; radius -= RADIUS_PRECISION)
        {
            for (int angle = 0; angle < 360; angle += anglePrecision)
            {
                double x = Math.cos(Math.toRadians(angle)) * radius;
                double z = Math.sin(Math.toRadians(angle)) * radius;
                Location loc = target.getLocation().add(x, this.height, z);
                Projectile projectile = (Projectile) target.getWorld().spawnEntity(loc, EntityType.ARROW);
                projectile.setShooter(entityBoss.getMobEntity());
            }
            anglePrecision += ANGLE_PRECISION_INCREMENTS;
        }
        target.getWorld().spawnEntity(target.getLocation().add(0, this.height, 0), EntityType.ARROW);
    }
}
