package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.files.YamlFile;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class ThrowAbility extends TargetAbility<ThrowAbility> {

    private enum ThrowType {
        DEFAULT,
        KNOCKBACK,
        BOSS_DIRECTION
    }

    private final ThrowType type;
    private final Vector velocity;

    ThrowAbility() {
        this(TargetType.CLOSEST, ThrowType.DEFAULT, new Vector(0, 0, 0));
    }

    ThrowAbility(TargetType targetType, ThrowType type, Vector velocity) {
        super(targetType);
        this.type = type;
        this.velocity = velocity;
    }

    @Override
    public ThrowAbility create(YamlFile yaml, String path) {
        TargetType targetType = loadTargetType(yaml, path);
        ThrowType direction = ThrowType.valueOf(yaml.getConfig().getString(path + ".throw_type").toUpperCase());
        double x = yaml.getConfig().getDouble(path + ".x");
        double y = yaml.getConfig().getDouble(path + ".y");
        double z = yaml.getConfig().getDouble(path + ".z");
        return new ThrowAbility(targetType, direction, new Vector(x, y, z));
    }

    @Override
    public void activate(EntityBoss entityBoss, LivingEntity target) {
        Vector direction = new Vector(1, 1, 1);
        if (this.type == ThrowType.BOSS_DIRECTION)
            direction = entityBoss.getMobEntity().getLocation().subtract(target.getLocation()).toVector().normalize();
        else if (this.type == ThrowType.KNOCKBACK)
            direction = target.getLocation().subtract(entityBoss.getMobEntity().getLocation()).toVector().normalize();
        target.setVelocity(direction.multiply(this.velocity));
    }
}
