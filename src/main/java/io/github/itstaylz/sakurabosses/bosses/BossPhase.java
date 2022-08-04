package io.github.itstaylz.sakurabosses.bosses;

import io.github.itstaylz.sakurabosses.bosses.abilities.IBossAbility;
import org.bukkit.Bukkit;

import java.util.List;

public record BossPhase(double minHealth, List<IBossAbility<?>> abilities) implements Comparable<BossPhase> {

    public void start(EntityBoss boss) {
        for (IBossAbility<?> ability : abilities) {
            ability.activate(boss);
        }
    }

    @Override
    public int compareTo(BossPhase o) {
        return Double.compare(o.minHealth, minHealth);
    }
}
