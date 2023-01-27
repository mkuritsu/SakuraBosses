package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.file.YamlFile;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.effects.LifeStealEffect;

public class LifeStealAbility implements IBossAbility<LifeStealAbility> {

    private final double multiplier;
    private final int duration;

    LifeStealAbility() {
        this(0, 0);
    }

    LifeStealAbility(double multiplier, int duration) {
        this.multiplier = multiplier;
        this.duration = duration;
    }

    @Override
    public LifeStealAbility create(YamlFile yaml, String path) {
        double multiplier = yaml.getConfig().getDouble(path + ".multiplier");
        int duration = yaml.getConfig().getInt(path + ".duration");
        return new LifeStealAbility(multiplier, duration);
    }

    @Override
    public void activate(EntityBoss entityBoss) {
        entityBoss.activateEffect(new LifeStealEffect(multiplier), this.duration);
    }
}
