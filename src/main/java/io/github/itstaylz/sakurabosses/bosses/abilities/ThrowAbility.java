package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.files.YamlFile;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class ThrowAbility extends ATargetAbility<ThrowAbility> {

    private enum ThrowType {
        DEFAULT,
        FORWARD,
        BACKWARDS
    }

    private final ThrowType type;
    private final Vector velocity;
    private final double radius;

    ThrowAbility() {
        this(TargetType.CLOSEST, ThrowType.DEFAULT, new Vector(0, 0, 0), 0);
    }

    ThrowAbility(TargetType targetType, ThrowType type, Vector velocity, double radius) {
        super(targetType);
        this.type = type;
        this.velocity = velocity;
        this.radius = radius;
    }

    @Override
    public ThrowAbility create(YamlFile yaml, String path) {
        TargetType targetType = loadTargetType(yaml, path);
        ThrowType direction = ThrowType.valueOf(yaml.getConfig().getString(path + ".throw_type").toUpperCase());
        double x = yaml.getConfig().getDouble(path + ".x");
        double y = yaml.getConfig().getDouble(path + ".y");
        double z = yaml.getConfig().getDouble(path + ".z");
        double radius = yaml.getConfig().getDouble(path + ".radius");
        return new ThrowAbility(targetType, direction, new Vector(x, y, z), radius);
    }

    @Override
    public void activate(EntityBoss entityBoss, LivingEntity target) {
        if (getTargetType() == TargetType.ALL_PLAYERS && radius > 0 && target.getLocation().distance(entityBoss.getMobEntity().getLocation()) > radius)
            return; // ignore for players not in radius
        Vector direction = new Vector(1, 1, 1);
        if (this.type == ThrowType.FORWARD)
            direction = entityBoss.getMobEntity().getLocation().subtract(target.getLocation()).toVector().normalize();
        else if (this.type == ThrowType.BACKWARDS)
            direction = target.getLocation().subtract(entityBoss.getMobEntity().getLocation()).toVector().normalize();
        target.setVelocity(direction.multiply(this.velocity));
    }
}
