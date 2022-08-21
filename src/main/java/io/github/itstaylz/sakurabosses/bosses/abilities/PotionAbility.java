package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.files.YamlFile;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

public class PotionAbility extends ATargetAbility<PotionAbility> {

    private final PotionType type;
    private final int duration, amplifier;

    PotionAbility() {
        this(TargetType.CLOSEST, PotionType.INVISIBILITY, 0, 0);
    }

    PotionAbility(TargetType targetType, PotionType type, int duration, int amplifier) {
        super(targetType);
        this.type = type;
        this.duration = duration;
        this.amplifier = amplifier;
    }

    @Override
    public PotionAbility create(YamlFile yaml, String path) {
        TargetType targetType = loadTargetType(yaml, path);
        PotionType type = PotionType.valueOf(yaml.getConfig().getString(path + ".potion_type"));
        int duration = yaml.getConfig().getInt(path + ".duration");
        int amplifier = yaml.getConfig().getInt(path + ".amplifier");
        return new PotionAbility(targetType, type, duration, amplifier);
    }

    @Override
    public void activate(EntityBoss entityBoss, LivingEntity target) {
        if (this.type != null && this.type.getEffectType() != null)
            target.addPotionEffect(new PotionEffect(this.type.getEffectType(), this.duration, this.amplifier));

    }
}
