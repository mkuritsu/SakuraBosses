package io.github.itstaylz.sakurabosses.menus;

import io.github.itstaylz.hexlib.items.ItemBuilder;
import io.github.itstaylz.hexlib.menus.Menu;
import io.github.itstaylz.hexlib.menus.components.MenuButton;
import io.github.itstaylz.hexlib.utils.StringUtils;
import io.github.itstaylz.sakurabosses.bosses.data.BossData;
import io.github.itstaylz.sakurabosses.bosses.BossManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BossMenu extends Menu {

    private static final ItemStack BACK_ARROW = new ItemBuilder(Material.ARROW)
            .setDisplayName(StringUtils.colorize("&ePrevious Page"))
            .build();

    private static final ItemStack NEXT_ARROW = new ItemBuilder(Material.ARROW)
            .setDisplayName(StringUtils.colorize("&eNext Page"))
            .build();

    private final List<BossData> bosses;

    private final int amountOfPages;

    public BossMenu() {
        super(5*9, StringUtils.colorize("&5Bosses"), false, true, null);
        this.bosses = BossManager.getAllBossData();
        this.amountOfPages = (int) Math.ceil(this.bosses.size() / 21.0);
        openPage(0);
    }

    private void openPage(int page) {
        getInventory().clear();
        int index = page * 21;
        for (int i = 10; i < 35 && index < this.bosses.size(); i++) {
            if (i == 17 || i == 18 || i == 26 || i == 27)
                continue;
            BossData bossData = this.bosses.get(index);
            addComponent(i, new MenuButton(bossData.spawnEgg(), ((event, player, menu) -> {
                player.getInventory().addItem(bossData.spawnEgg());
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1f);
            })));
            index++;
        }
        if (page > 0)
            addComponent(18, new MenuButton(BACK_ARROW, (event, player, menu) -> {
                openPage(page - 1);
            }));
        if (page + 1 < this.amountOfPages)
            addComponent(26, new MenuButton(NEXT_ARROW, (event, player, menu) -> {
                openPage(page + 1);
            }));
    }
}
