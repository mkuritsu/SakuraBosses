package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.files.YamlFile;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.effects.DurabilityEffect;

public class DurabilityAbility implements IBossAbility<DurabilityAbility> {

    private final int damage, duration;

    public DurabilityAbility(int damage, int duration) {
        this.damage = damage;
        this.duration = duration;
    }

    @Override
    public DurabilityAbility create(YamlFile yaml, String path) {
        int damageAmount = yaml.getConfig().getInt(path + ".damage");
        int duration = yaml.getConfig().getInt(path + ".duration");
        return new DurabilityAbility(damageAmount, duration);
    }

    @Override
    public void activate(EntityBoss entityBoss) {
        entityBoss.activateEffect(new DurabilityEffect(this.damage), this.duration);
    }
}
