package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.files.YamlFile;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.effects.ImmunityEffect;

import java.util.List;

public class ImmunityAbility implements IBossAbility<ImmunityAbility> {

    private final List<String> ignoreList;
    private final int duration;

    ImmunityAbility() {
        this(null, 0);
    }

    ImmunityAbility(List<String> materials, int duration) {
        this.ignoreList = materials;
        this.duration = duration;
    }

    @Override
    public ImmunityAbility create(YamlFile yaml, String path) {
        List<String> materials = yaml.getConfig().getStringList(path + ".materials");
        int duration = yaml.getConfig().getInt(path + ".duration");
        return new ImmunityAbility(materials, duration);
    }

    @Override
    public void activate(EntityBoss entityBoss) {
        entityBoss.activateEffect(new ImmunityEffect(ignoreList), this.duration);
    }

}
