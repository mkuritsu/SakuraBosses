package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.file.YamlFile;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;

public class ParticleAbility extends ATargetAbility<ParticleAbility> {

    private enum ParticleShape {
        DEFAULT,
        CIRCLE
    }

    private final Particle particle;
    private final double radius, precision;

    private final int amount;

    private final ParticleShape shape;

    ParticleAbility() {
        this(TargetType.CLOSEST, Particle.FLAME, ParticleShape.CIRCLE, 0, 0, 0);
    }

    ParticleAbility(TargetType targetType, Particle particle, ParticleShape shape, int amount, double radius, double precision) {
        super(targetType);
        this.particle = particle;
        this.shape = shape;
        this.amount = amount;
        this.radius = radius;
        this.precision = precision <= 0 ? 0.1 : precision;
    }


    @Override
    public ParticleAbility create(YamlFile yaml, String path) {
        TargetType targetType = loadTargetType(yaml, path);
        Particle particle = Particle.valueOf(yaml.getConfig().getString(path + ".particle"));
        ParticleShape shape = ParticleShape.valueOf(yaml.getConfig().getString(path + ".shape"));
        int amount = yaml.getConfig().getInt(path + ".amount");
        double radius = yaml.getConfig().getDouble(path + ".radius");
        double precision = yaml.getConfig().getDouble(path + ".precision");
        return new ParticleAbility(targetType, particle, shape, amount, radius, precision);
    }

    @Override
    public void activate(EntityBoss entityBoss, LivingEntity target) {
        switch (this.shape) {
            case CIRCLE -> {
                for (double angle = 0; angle < 2 * Math.PI; angle += this.precision) {
                    Location center = target.getLocation().clone();
                    double x = Math.cos(angle) * this.radius;
                    double z = Math.sin(angle) * this.radius;
                    target.getWorld().spawnParticle(this.particle, center.add(x, 0, z), this.amount, 0, 0, 0, 0);
                }
            }
            case DEFAULT -> {
                target.getWorld().spawnParticle(this.particle, target.getLocation(), this.amount);
            }
        }
    }
}
