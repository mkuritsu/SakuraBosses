package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.file.YamlFile;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class ArrowRainAbility extends ATargetAbility<ArrowRainAbility> {

    private final double radius, height, precision;

    ArrowRainAbility() {
        this(TargetType.CLOSEST, 0, 0, 0);
    }

    ArrowRainAbility(TargetType targetType, double radius, double height, double precision) {
        super(targetType);
        this.radius = radius;
        this.height = height;
        this.precision = precision <= 0 ? 0.1 : precision;
    }

    @Override
    public ArrowRainAbility create(YamlFile yaml, String path) {
        TargetType targetType = loadTargetType(yaml, path);
        double radius = yaml.getConfig().getDouble(path + ".radius");
        double height = yaml.getConfig().getDouble(path + ".height");
        double precision = yaml.getConfig().getDouble(path + ".precision");
        return new ArrowRainAbility(targetType, radius, height, precision);
    }

    @Override
    public void activate(EntityBoss entityBoss, LivingEntity target) {
        Location firstCorner = target.getLocation().clone().add(this.radius, 0, this.radius);
        Location secondCorner = target.getLocation().clone().subtract(this.radius, 0, this.radius);
        double radiusSquared = radius * radius;
        double centerX = target.getLocation().getX();
        double centerZ = target.getLocation().getZ();
        double smallestX = Math.min(firstCorner.getX(), secondCorner.getX());
        double smallestZ = Math.min(firstCorner.getZ(), secondCorner.getZ());
        double biggestX = Math.max(firstCorner.getX(), secondCorner.getX());
        double biggestZ = Math.max(firstCorner.getZ(), secondCorner.getZ());

        for (double x = smallestX; x < biggestX; x += this.precision) {
            for (double z = smallestZ; z < biggestZ; z += this.precision) {
                double xSquared = (centerX - x) * (centerX - x);
                double zSquared = (centerZ - z) * (centerZ - z);
                if (xSquared + zSquared <= radiusSquared) {
                    Location loc = new Location(target.getWorld(), x, target.getLocation().getY() + this.height, z);
                    Arrow arrow = (Arrow) entityBoss.spawnMinion(loc, EntityType.ARROW);
                    arrow.setShooter(entityBoss.getMobEntity());
                }
            }
        }
    }
}
