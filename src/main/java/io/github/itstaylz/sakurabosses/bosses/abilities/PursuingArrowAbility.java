package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.files.YamlFile;
import io.github.itstaylz.sakurabosses.SakuraBossesPlugin;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class PursuingArrowAbility extends TargetAbility<PursuingArrowAbility> {

    private double damage, speed;

    PursuingArrowAbility(TargetType targetType, double damage, double speed) {
        super(targetType);
        this.damage = damage;
        this.speed = speed;
    }

    @Override
    public PursuingArrowAbility create(YamlFile yaml, String path) {
        TargetType targetType = loadTargetType(yaml, path);
        double damage = yaml.getConfig().getDouble(path + ".damage");
        double speed = yaml.getConfig().getDouble(path + ".speed");
        return new PursuingArrowAbility(targetType, damage, speed);
    }

    @Override
    public void activate(EntityBoss entityBoss, LivingEntity target) {
        Arrow arrow = target.getWorld().spawnArrow(entityBoss.getMobEntity().getLocation().add(0, 2, 0), new Vector(0, 0, 0), 0, 0);
        arrow.setShooter(entityBoss.getMobEntity());
        arrow.setDamage(damage);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (arrow.isOnGround() || arrow.isDead()) {
                    cancel();
                    return;
                }
                Vector direction = target.getLocation().subtract(arrow.getLocation()).toVector();
                arrow.setVelocity(direction.multiply(speed));
            }
        }.runTaskTimer(JavaPlugin.getPlugin(SakuraBossesPlugin.class), 0L, 1L);
    }
}
