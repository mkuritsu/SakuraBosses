package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.file.YamlFile;
import io.github.itstaylz.hexlib.utils.PDCUtils;
import io.github.itstaylz.sakurabosses.SakuraBossesPlugin;
import io.github.itstaylz.sakurabosses.bosses.BossDataKeys;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class FireballAbility extends ATargetAbility<FireballAbility> {
    private final int amount, delay;
    private final double damage;

    FireballAbility() {
        this(TargetType.CLOSEST, 0, 0, 0);
    }

    FireballAbility(TargetType targetType, int amount, int delay, double damage) {
        super(targetType);
        this.amount = amount;
        this.delay = delay;
        this.damage = damage;
    }

    @Override
    public FireballAbility create(YamlFile yaml, String path) {
        TargetType type = loadTargetType(yaml, path);
        int amount = yaml.getConfig().getInt(path + ".amount");
        int delay = yaml.getConfig().getInt(path + ".delay");
        double damage = yaml.getConfig().getDouble(path + ".damage");
        return new FireballAbility(type, amount, delay, damage);
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
                Fireball fireball = (Fireball) entityBoss.spawnMinion(entityBoss.getMobEntity().getEyeLocation(), EntityType.FIREBALL);
                Vector direction = target.getLocation().subtract(fireball.getLocation()).toVector().normalize();
                fireball.setDirection(direction);
                fireball.setShooter(entityBoss.getMobEntity());
                PDCUtils.setPDCValue(fireball, BossDataKeys.MINION_DAMAGE_KEY, PersistentDataType.DOUBLE, damage);
                counter++;
            }
        }.runTaskTimer(JavaPlugin.getPlugin(SakuraBossesPlugin.class), 0L, this.delay);
    }
}
