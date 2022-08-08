package io.github.itstaylz.sakurabosses.utils;

import io.github.itstaylz.hexlib.utils.StringUtils;
import org.bukkit.entity.LivingEntity;

public final class HealthBarUtils {

    private static final String HEALTH_DIVIDER = StringUtils.colorize("&7|");

    public static void updateHealthBar(LivingEntity entity) {
        String name = entity.getCustomName();
        if (name != null && entity.isCustomNameVisible()) {
            StringBuilder sb = new StringBuilder();
            for (String s : name.split(" ")) {
                if (s.contains(HEALTH_DIVIDER))
                    break;
                sb.append(s).append(" ");
            }
            String displayName = sb.toString();
            String formattedHealth = String.format("%.1f", entity.getHealth());
            entity.setCustomName(StringUtils.fullColorize(displayName + HEALTH_DIVIDER + " &c" + formattedHealth + " &4♥"));
        }
    }
}
