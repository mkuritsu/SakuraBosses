package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.file.YamlFile;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;

public interface IBossAbility<T extends IBossAbility<T>> {

    T create(YamlFile yaml, String path);

    void activate(EntityBoss entityBoss);
}
