package io.github.itstaylz.sakurabosses.bosses.effects;

import java.util.List;

public record ImmunityEffect(List<String> ignoredWeapons, List<String> ignoredProjectiles) implements IBossEffect {
}
