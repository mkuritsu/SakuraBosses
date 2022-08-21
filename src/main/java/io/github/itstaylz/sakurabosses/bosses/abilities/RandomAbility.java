package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.sakurabosses.bosses.EntityBoss;

import java.util.List;

public record RandomAbility(IBossAbility<?> ability, List<IBossAbility<?>> childAbilities, double activationChance) {

    public void activate(EntityBoss boss) {
        this.ability.activate(boss);
        for (IBossAbility<?> child : this.childAbilities)
            child.activate(boss);
    }
}
