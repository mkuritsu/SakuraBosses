package io.github.itstaylz.sakurabosses.menus;

import io.github.itstaylz.hexlib.items.ItemBuilder;
import io.github.itstaylz.hexlib.menu.Menu;
import io.github.itstaylz.hexlib.menu.MenuSettings;
import io.github.itstaylz.hexlib.menu.components.Button;
import io.github.itstaylz.hexlib.utils.StringUtils;
import io.github.itstaylz.sakurabosses.spawning.BossWarp;
import io.github.itstaylz.sakurabosses.spawning.WarpManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class WarpMenu extends Menu {

    private static final MenuSettings SETTINGS = MenuSettings.builder()
            .withNumberOfRows(5)
            .withTitle(StringUtils.colorize("&9&lWarps"))
            .build();

    private static final ItemStack BACK_ARROW = new ItemBuilder(Material.ARROW)
            .setDisplayName(StringUtils.colorize("&ePrevious Page"))
            .build();

    private static final ItemStack NEXT_ARROW = new ItemBuilder(Material.ARROW)
            .setDisplayName(StringUtils.colorize("&eNext Page"))
            .build();

    private final List<BossWarp> warps;

    private final int amountOfPages;

    public WarpMenu() {
        super(SETTINGS);
        this.warps = WarpManager.getAllWarps();
        this.amountOfPages = (int) Math.ceil(this.warps.size() / 21.0);
        openPage(0);
    }

    private void openPage(int page) {
        getInventory().clear();
        int index = page * 21;
        for (int i = 10; i < 35 && index < this.warps.size(); i++) {
            if (i == 17 || i == 18 || i == 26 || i == 27)
                continue;
            BossWarp warp = this.warps.get(index);
            setComponent(i, new Button(warp.getMenuItem(), ((menu, player, event) -> {
                if (player.hasPermission("sakurabosses.managewarps")) {
                    menu.close(player);
                    player.teleport(warp.getLocation());
                    player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                }
            })));
            index++;
        }
        if (page > 0)
            setComponent(18, new Button(BACK_ARROW, (menu, player, event) -> {
                openPage(page - 1);
            }));
        if (page + 1 < this.amountOfPages)
            setComponent(26, new Button(NEXT_ARROW, (menu, player, event) -> {
                openPage(page + 1);
            }));
    }
}
