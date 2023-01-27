package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.file.YamlFile;
import io.github.itstaylz.sakurabosses.SakuraBossesPlugin;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class PursuingArrowAbility extends ATargetAbility<PursuingArrowAbility> {

    private final double damage, speed;
    private final Particle particle;

    PursuingArrowAbility() {
        this(TargetType.CLOSEST, 0, 0, Particle.FLAME);
    }

    PursuingArrowAbility(TargetType targetType, double damage, double speed, Particle particle) {
        super(targetType);
        this.damage = damage;
        this.speed = speed;
        this.particle = particle;
    }

    @Override
    public PursuingArrowAbility create(YamlFile yaml, String path) {
        TargetType targetType = loadTargetType(yaml, path);
        double damage = yaml.getConfig().getDouble(path + ".damage");
        double speed = yaml.getConfig().getDouble(path + ".speed");
        String particleString = yaml.getConfig().getString(path + ".particle");
        Particle particle = particleString != null ? Particle.valueOf(particleString) : null;
        return new PursuingArrowAbility(targetType, damage, speed, particle);
    }

    @Override
    public void activate(EntityBoss entityBoss, LivingEntity target) {
        Location location = entityBoss.getMobEntity().getEyeLocation();
        Arrow arrow = target.getWorld().spawnArrow(location, new Vector(0, 0, 0), 0, 3);
        arrow.setShooter(entityBoss.getMobEntity());
        arrow.setDamage(this.damage);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (arrow.isOnGround() || arrow.isDead()) {
                    cancel();
                    return;
                }
                if (particle != null)
                    arrow.getWorld().spawnParticle(particle, arrow.getLocation(), 2, 0, 0, 0, 0.03);
                Vector direction = target.getEyeLocation().subtract(arrow.getLocation()).toVector();
                arrow.setVelocity(direction.multiply(speed));
            }
        }.runTaskTimer(JavaPlugin.getPlugin(SakuraBossesPlugin.class), 0L, 1L);
    }
}
