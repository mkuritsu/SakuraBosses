package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.files.YamlFile;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class ThrowAbility extends TargetAbility<ThrowAbility> {

    private final Vector velocity;

    ThrowAbility(TargetType targetType, Vector velocity) {
        super(targetType);
        this.velocity = velocity;
    }

    @Override
    public ThrowAbility create(YamlFile yaml, String path) {
        TargetType targetType = loadTargetType(yaml, path);
        double x = yaml.getConfig().getDouble(path + ".velocityX");
        double y = yaml.getConfig().getDouble(path + ".velocityY");
        double z = yaml.getConfig().getDouble(path + ".velocityZ");
        return new ThrowAbility(targetType, new Vector(x, y, z));
    }

    @Override
    public void activate(EntityBoss entityBoss, LivingEntity target) {
        target.setVelocity(this.velocity);
    }
}
