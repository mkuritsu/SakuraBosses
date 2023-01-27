package io.github.itstaylz.sakurabosses.listeners;

import io.github.itstaylz.hexlib.utils.PDCUtils;
import io.github.itstaylz.sakurabosses.bosses.BossDataKeys;
import io.github.itstaylz.sakurabosses.bosses.BossManager;
import io.github.itstaylz.sakurabosses.bosses.BossTargeting;
import net.ess3.api.events.VanishStatusChangeEvent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class PlayerListener implements Listener {

    @EventHandler
    private void onSpawn(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Block block = event.getClickedBlock();
        if (item != null && block != null && item.getType().name().contains("SPAWN_EGG") && event.getAction() == Action.RIGHT_CLICK_BLOCK &&
                PDCUtils.hasPDCValue(item, BossDataKeys.BOSS_SPAWN_EGG_KEY, PersistentDataType.STRING)) {
            event.setCancelled(true);
            String bossId = PDCUtils.getPDCValue(item, BossDataKeys.BOSS_SPAWN_EGG_KEY, PersistentDataType.STRING);
            Location spawnLocation = block.getLocation().add(event.getBlockFace().getDirection());
            BossManager.spawnBoss(bossId, spawnLocation);
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                item.setAmount(item.getAmount() - 1);
        }
    }

    @EventHandler
    private void onGameModeChange(PlayerGameModeChangeEvent event) {
        if (event.getNewGameMode() == GameMode.CREATIVE || event.getNewGameMode() == GameMode.SPECTATOR) {
            BossTargeting.removeTarget(event.getPlayer());
        }
    }

    @EventHandler
    private void onVanish(VanishStatusChangeEvent event) {
        BossTargeting.removeTarget(event.getAffected().getBase());
    }
}
