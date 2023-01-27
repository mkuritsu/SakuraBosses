package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.file.YamlFile;
import io.github.itstaylz.hexlib.utils.PDCUtils;
import io.github.itstaylz.sakurabosses.bosses.BossDataKeys;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

public class BombAbility extends ATargetAbility<BombAbility> {

    private final double height, damage;
    private final int amount;

    BombAbility() {
        this(TargetType.CLOSEST, 0, 0, 0);
    }

    BombAbility(TargetType targetType, double height, double damage, int amount) {
        super(targetType);
        this.height = Math.max(height, 0);
        this.amount = Math.max(amount, 1);
        this.damage = Math.max(damage, 0);
    }

    @Override
    public BombAbility create(YamlFile yaml, String path) {
        TargetType targetType = loadTargetType(yaml, path);
        double height = yaml.getConfig().getDouble(path + ".height");
        double power = yaml.getConfig().getDouble(path + ".damage");
        int amount = yaml.getConfig().getInt(path + ".amount");
        return new BombAbility(targetType, height, power, amount);
    }

    @Override
    public void activate(EntityBoss entityBoss, LivingEntity target) {
        for (int i = 0; i < this.amount; i++) {
            Entity entity = entityBoss.spawnMinion(target.getLocation().clone().add(0, this.height, 0), EntityType.PRIMED_TNT);
            PDCUtils.setPDCValue(entity, BossDataKeys.MINION_DAMAGE_KEY, PersistentDataType.DOUBLE, this.damage);
        }
    }
}
