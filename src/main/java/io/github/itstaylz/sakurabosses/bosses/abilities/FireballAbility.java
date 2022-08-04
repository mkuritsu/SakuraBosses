package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.files.YamlFile;
import io.github.itstaylz.sakurabosses.SakuraBossesPlugin;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class FireballAbility extends TargetAbility<FireballAbility> {

    private static final double SPEED = 1.5;

    private final int amount, delay;

    FireballAbility(TargetType targetType, int amount, int delay) {
        super(targetType);
        this.amount = amount;
        this.delay = delay;
    }

    @Override
    public FireballAbility create(YamlFile yaml, String path) {
        TargetType type = loadTargetType(yaml, path);
        int amount = yaml.getConfig().getInt(path + ".amount");
        int delay = yaml.getConfig().getInt(path + ".delay");
        return new FireballAbility(type, amount, delay);
    }

    @Override
    public void activate(EntityBoss entityBoss, LivingEntity target) {
        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {
                if (counter == amount || entityBoss.getMobEntity().isDead() || target.isDead()) {
                    cancel();
                    return;
                }
                Vector direction = target.getLocation().subtract(entityBoss.getMobEntity().getLocation()).toVector().normalize();
                entityBoss.getMobEntity().launchProjectile(Fireball.class, direction.multiply(SPEED));
                counter++;
            }
        }.runTaskTimer(JavaPlugin.getPlugin(SakuraBossesPlugin.class), 0L, this.delay);
    }
}
