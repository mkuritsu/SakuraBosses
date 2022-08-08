package io.github.itstaylz.sakurabosses.utils;

import io.github.itstaylz.hexlib.items.ItemBuilder;
import io.github.itstaylz.hexlib.items.SkullBuilder;
import io.github.itstaylz.hexlib.storage.files.YamlFile;
import io.github.itstaylz.hexlib.utils.StringUtils;
import io.github.itstaylz.sakurabosses.bosses.data.BossEquipmentItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.List;

public final class YamlUtils {

    public static ItemStack loadItemStack(YamlFile yaml, String path) {
        if (!yaml.contains(path))
            return null;
        Material material = Material.valueOf(yaml.getOrDefault(path + ".material", "STONE"));
        ItemBuilder builder;
        if (material == Material.PLAYER_HEAD) {
            SkullBuilder skullBuilder = new SkullBuilder();
            String url = yaml.get(path + ".skin_url", String.class);
            if (url != null) {
                skullBuilder.setSkinFromURL(url);
            }
            builder = new ItemBuilder(skullBuilder.build());
        } else {
            builder = new ItemBuilder(material);
        }
        int amount = yaml.getConfig().getInt(path + ".amount");
        amount = Math.max(Math.min(amount, 64), 1);
        builder.setAmount(amount);
        if (yaml.contains(path + ".lore")) {
            List<String> lore = yaml.getConfig().getStringList(path + ".lore");
            lore.replaceAll(StringUtils::fullColorize);
            builder.setLore(lore);
        }
        if (yaml.contains(path + ".display_name"))
            builder.setDisplayName(StringUtils.fullColorize(yaml.get(path + ".display_name", String.class)));
        if (yaml.contains(path + ".enchants")) {
            List<String> enchants = yaml.getConfig().getStringList(path + ".enchants");
            for (String enchantString : enchants) {
                String[] enchantStringSplitted = enchantString.split(":");
                String enchantName = enchantStringSplitted[0];
                int level = Integer.parseInt(enchantStringSplitted[1]);
                try {
                    Field enchantField = Enchantment.class.getDeclaredField(enchantName);
                    Enchantment enchant = (Enchantment) enchantField.get(null);
                    builder.addEnchant(enchant, level);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    Bukkit.getLogger().severe("INVALID ENCHANT NAME!: " + enchantName);
                }
            }
        }
        return builder.build();
    }

    public static ItemStack loadItemStackOrDefault(YamlFile yaml, String path, ItemStack defaultValue) {
        if (yaml.contains(path))
            return loadItemStack(yaml, path);
        return defaultValue;
    }

    public static BossEquipmentItem loadBossEquipment(YamlFile yaml, String path) {
        ItemStack item = loadItemStackOrDefault(yaml, path, null);
        double dropChance = yaml.getConfig().getDouble(path + ".drop_chance");
        return new BossEquipmentItem(item, dropChance);
    }
}
