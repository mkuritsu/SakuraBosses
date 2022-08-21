package io.github.itstaylz.sakurabosses.menus;

import io.github.itstaylz.hexlib.items.ItemBuilder;
import io.github.itstaylz.hexlib.menus.Menu;
import io.github.itstaylz.hexlib.menus.components.MenuButton;
import io.github.itstaylz.hexlib.utils.StringUtils;
import io.github.itstaylz.sakurabosses.spawning.BossWarp;
import io.github.itstaylz.sakurabosses.spawning.WarpManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class WarpMenu extends Menu {

    private static final ItemStack BACK_ARROW = new ItemBuilder(Material.ARROW)
            .setDisplayName(StringUtils.colorize("&ePrevious Page"))
            .build();

    private static final ItemStack NEXT_ARROW = new ItemBuilder(Material.ARROW)
            .setDisplayName(StringUtils.colorize("&eNext Page"))
            .build();

    private final List<BossWarp> warps;

    private final int amountOfPages;

    public WarpMenu() {
        super(5*9, StringUtils.colorize("&9&lWarps"), false, true, null);
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
            addComponent(i, new MenuButton(warp.getMenuItem(), ((event, player, menu) -> {
                if (player.hasPermission("sakurabosses.managewarps")) {
                    menu.close(player);
                    player.teleport(warp.getLocation());
                    player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                }
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
