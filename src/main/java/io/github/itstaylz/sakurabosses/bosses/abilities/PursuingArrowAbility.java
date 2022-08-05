package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.files.YamlFile;
import io.github.itstaylz.hexlib.utils.RandomUtils;
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

import java.util.ArrayList;
import java.util.List;

public class PursuingArrowAbility extends TargetAbility<PursuingArrowAbility> {

    private double damage, speed;
    private int amount;
    private Particle particle;

    PursuingArrowAbility(TargetType targetType, double damage, double speed, int amount, Particle particle) {
        super(targetType);
        this.damage = damage;
        this.speed = speed;
        this.amount = amount;
        this.particle = particle;
    }

    @Override
    public PursuingArrowAbility create(YamlFile yaml, String path) {
        TargetType targetType = loadTargetType(yaml, path);
        double damage = yaml.getConfig().getDouble(path + ".damage");
        double speed = yaml.getConfig().getDouble(path + ".speed");
        int amount = yaml.getConfig().getInt(path + ".amount");
        String particleString = yaml.getConfig().getString(path + ".particle");
        Particle particle = particleString != null ? Particle.valueOf(particleString) : null;
        return new PursuingArrowAbility(targetType, damage, speed, amount, particle);
    }

    @Override
    public void activate(EntityBoss entityBoss, LivingEntity target) {
        List<Arrow> arrows = new ArrayList<>();
        for (int i = 0; i < this.amount; i++) {
            Location location = entityBoss.getMobEntity().getLocation();
            double xSpread = RandomUtils.RANDOM.nextDouble() * 2;
            double zSpread = RandomUtils.RANDOM.nextDouble() * 2;
            Arrow arrow = target.getWorld().spawnArrow(location.add(xSpread, 2, zSpread), new Vector(0, 0, 0), 0, 3);
            arrow.setShooter(entityBoss.getMobEntity());
            arrow.setDamage(this.damage);
            arrows.add(arrow);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean allDead = true;
                for (Arrow arrow : arrows) {
                    if (arrow.isOnGround() || arrow.isDead()) {
                        continue;
                    }
                    if (particle != null)
                        arrow.getWorld().spawnParticle(particle, arrow.getLocation(), 2, 0, 0, 0, 0.03);
                    Vector direction = target.getEyeLocation().subtract(arrow.getLocation()).toVector();
                    arrow.setVelocity(direction.multiply(speed));
                    allDead = false;
                }
                if (allDead)
                    cancel();
            }
        }.runTaskTimer(JavaPlugin.getPlugin(SakuraBossesPlugin.class), 0L, 1L);
    }
}
