package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.file.YamlFile;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.utils.YamlUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class TreasureAbility implements IBossAbility<TreasureAbility> {

    private final ItemStack[] items;

    TreasureAbility() {
        this(new ItemStack[0]);
    }

    TreasureAbility(ItemStack[] items) {
        this.items = items;
    }

    @Override
    public TreasureAbility create(YamlFile yaml, String path) {
        ConfigurationSection section = yaml.getSection(path + ".items");
        if (section != null) {
            Set<String> keys = section.getKeys(false);
            ItemStack[] items = new ItemStack[keys.size()];
            int index = 0;
            for (String key : keys) {
                items[index] = YamlUtils.loadItemStack(yaml, path + ".items." + key);
                index++;
            }
            return new TreasureAbility(items);
        }
        return new TreasureAbility(new ItemStack[0]);
    }

    @Override
    public void activate(EntityBoss entityBoss) {
        for (ItemStack item : this.items) {
            entityBoss.getMobEntity().getWorld().dropItemNaturally(entityBoss.getMobEntity().getLocation(), item);
        }
    }
}
