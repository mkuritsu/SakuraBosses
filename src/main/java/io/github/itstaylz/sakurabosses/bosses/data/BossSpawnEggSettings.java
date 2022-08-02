package io.github.itstaylz.sakurabosses.bosses.data;

import org.bukkit.Material;

import java.util.List;

public record BossSpawnEggSettings(Material material, String displayName, List<String> lore, boolean glowing) {
}
