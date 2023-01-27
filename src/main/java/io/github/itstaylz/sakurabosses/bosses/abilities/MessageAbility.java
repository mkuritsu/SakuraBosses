package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.file.YamlFile;
import io.github.itstaylz.hexlib.utils.StringUtils;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.entity.LivingEntity;

public class MessageAbility extends ATargetAbility<MessageAbility> {

    private final String message;

    MessageAbility() {
        this(TargetType.CLOSEST, null);
    }

    MessageAbility(TargetType targetType, String message) {
        super(targetType);
        this.message = message;
    }

    @Override
    public MessageAbility create(YamlFile yaml, String path) {
        TargetType targetType = loadTargetType(yaml, path);
        String message = StringUtils.colorize(yaml.get(path + ".text", String.class));
        return new MessageAbility(targetType, message);
    }

    @Override
    public void activate(EntityBoss entityBoss, LivingEntity target) {
        String targetMessage = message.replace("{BOSS}", entityBoss.getBossData().settings().displayName())
                .replace("{TARGET}", target.getName());
        target.sendMessage(targetMessage);
    }
}
