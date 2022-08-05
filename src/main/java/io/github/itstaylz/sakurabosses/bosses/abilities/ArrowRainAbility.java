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

    private final double radius, height, density;

    ArrowRainAbility(TargetType targetType, double radius, double height, double density) {
        super(targetType);
        this.radius = radius;
        this.height = height;
        this.density = density;
    }

    @Override
    public ArrowRainAbility create(YamlFile yaml, String path) {
        TargetType targetType = loadTargetType(yaml, path);
        double radius = yaml.getConfig().getDouble(path + ".radius");
        double height = yaml.getConfig().getDouble(path + ".height");
        double density = yaml.getConfig().getDouble(path + ".density");
        return new ArrowRainAbility(targetType, radius, height, density);
    }

    @Override
    public void activate(EntityBoss entityBoss, LivingEntity target) {
        double angleIncrements = 360 / density;
        for (double radius = this.radius; radius > 0; radius -= RADIUS_PRECISION)
        {
            for (int angle = 0; angle < 360; angle += angleIncrements)
            {
                double x = Math.cos(Math.toRadians(angle)) * radius;
                double z = Math.sin(Math.toRadians(angle)) * radius;
                Location loc = target.getLocation().add(x, this.height, z);
                Projectile projectile = (Projectile) target.getWorld().spawnEntity(loc, EntityType.ARROW);
                projectile.setShooter(entityBoss.getMobEntity());
            }
        }
        target.getWorld().spawnEntity(target.getLocation().add(0, this.height, 0), EntityType.ARROW);
    }
}
