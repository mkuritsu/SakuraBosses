package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.file.YamlFile;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.effects.ImmunityEffect;

import java.util.List;

public class ImmunityAbility implements IBossAbility<ImmunityAbility> {

    private final List<String> ignoredWeapons;
    private final List<String> ignoredProjectiles;
    private final int duration;

    ImmunityAbility() {
        this(null, null, 0);
    }

    ImmunityAbility(List<String> ignoredWeapons, List<String> ignoredProjectiles, int duration) {
        this.ignoredWeapons = ignoredWeapons;
        this.ignoredProjectiles = ignoredProjectiles;
        this.duration = duration;
    }

    @Override
    public ImmunityAbility create(YamlFile yaml, String path) {
        List<String> weapons = yaml.getConfig().getStringList(path + ".weapons");
        List<String> projectiles = yaml.getConfig().getStringList(path + ".projectiles");
        int duration = yaml.getConfig().getInt(path + ".duration");
        return new ImmunityAbility(weapons, projectiles, duration);
    }

    @Override
    public void activate(EntityBoss entityBoss) {
        entityBoss.activateEffect(new ImmunityEffect(ignoredWeapons, ignoredProjectiles), this.duration);
    }

}
