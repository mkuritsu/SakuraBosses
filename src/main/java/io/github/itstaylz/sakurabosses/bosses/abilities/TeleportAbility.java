package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.files.YamlFile;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;

public class TeleportAbility extends ATargetAbility<TeleportAbility> {

    TeleportAbility() {
        this(TargetType.CLOSEST);
    }

    TeleportAbility(TargetType targetType) {
        super(targetType);
    }

    @Override
    public TeleportAbility create(YamlFile yaml, String path) {
        TargetType targetType = loadTargetType(yaml, path);
        return new TeleportAbility(targetType);
    }

    @Override
    public void activate(EntityBoss entityBoss, LivingEntity target) {
        entityBoss.getMobEntity().teleport(target.getLocation());
        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
    }
}
