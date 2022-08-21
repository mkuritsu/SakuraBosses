package io.github.itstaylz.sakurabosses.bosses.data;

import org.bukkit.entity.EntityType;

public record BossSettings(String displayName, EntityType entityType, double maxHealth, TargetType targetType,
                           boolean knockBack, double radius, int abilityTimer, String spawnMessage) {

}
