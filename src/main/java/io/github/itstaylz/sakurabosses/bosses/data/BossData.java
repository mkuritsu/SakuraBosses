package io.github.itstaylz.sakurabosses.bosses.data;

import io.github.itstaylz.hexlib.items.ItemBuilder;
import io.github.itstaylz.hexlib.storage.files.YamlFile;
import io.github.itstaylz.hexlib.utils.ItemUtils;
import io.github.itstaylz.hexlib.utils.StringUtils;
import io.github.itstaylz.sakurabosses.SakuraBossesPlugin;
import io.github.itstaylz.sakurabosses.bosses.BossDataKeys;
import io.github.itstaylz.sakurabosses.bosses.BossPhase;
import io.github.itstaylz.sakurabosses.bosses.abilities.Abilities;
import io.github.itstaylz.sakurabosses.bosses.abilities.IBossAbility;
import io.github.itstaylz.sakurabosses.utils.YamlUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.List;
import java.util.PriorityQueue;

public record BossData(String id, BossSettings settings, ItemStack spawnEgg, BossEquipmentItem[] equipment, PriorityQueue<BossPhase> phases) {

    public static BossData loadFromFile(File file) {
        YamlFile yaml = new YamlFile(file);
        try {
            String id = file.getName().replace(".yml", "");

            // Load general boss settings
            String displayName = StringUtils.fullColorize(yaml.getOrDefault("settings.display_name", "&4&lBOSS"));
            EntityType entityType = EntityType.valueOf(yaml.getOrDefault("settings.entity_type", "ZOMBIE"));
            double maxHealth = yaml.contains("settings.max_health") ? yaml.getConfig().getDouble("settings.max_health") : 10000;
            TargetType targetType = TargetType.valueOf(yaml.getOrDefault("settings.target_type", "CLOSEST"));
            boolean knockBack = yaml.getConfig().getBoolean("settings.knockback");
            double radius = yaml.getConfig().getDouble("settings.radius");
            BossSettings settings = new BossSettings(displayName, entityType, maxHealth, targetType, knockBack, radius);

            // Load spawn egg
            ItemStack spawnEgg = YamlUtils.loadItemStack(yaml, "spawn_egg");
            boolean glowing = yaml.getConfig().getBoolean("spawn_egg.glowing");
            if (glowing)
                new ItemBuilder(spawnEgg).addEnchant(Enchantment.DURABILITY, 1).addItemFlags(ItemFlag.HIDE_ENCHANTS).build();
            ItemUtils.setPDCValue(spawnEgg, BossDataKeys.BOSS_SPAWN_EGG_KEY, PersistentDataType.STRING, id);

            // Load equipment
            BossEquipmentItem weapon = YamlUtils.loadBossEquipment(yaml, "equipment.weapon");
            BossEquipmentItem helmet = YamlUtils.loadBossEquipment(yaml, "equipment.helmet");
            BossEquipmentItem chestplate = YamlUtils.loadBossEquipment(yaml, "equipment.chestplate");
            BossEquipmentItem leggings = YamlUtils.loadBossEquipment(yaml, "equipment.leggings");
            BossEquipmentItem boots = YamlUtils.loadBossEquipment(yaml, "equipment.boots");
            BossEquipmentItem[] equipment = new BossEquipmentItem[] { weapon, helmet, chestplate, leggings, boots };

            // Load phases
            PriorityQueue<BossPhase> phases = new PriorityQueue<>();
            ConfigurationSection phasesSection = yaml.getSection("phases");
            for (String path : phasesSection.getKeys(false)) {
                double minHealth = phasesSection.getDouble(path + ".min_health");
                String abilitiesPath = "phases." + path + ".abilities";
                List<IBossAbility<?>> abilities = Abilities.loadBossAbilities(yaml, abilitiesPath);
                phases.add(new BossPhase(minHealth, abilities));
            }

            return new BossData(id, settings, spawnEgg, equipment, phases);
        } catch (Exception e) {
            SakuraBossesPlugin.getPluginLogger().severe("Failed to load boss: " + file.getName());
            e.printStackTrace();
        }
        return null;
    }
}
