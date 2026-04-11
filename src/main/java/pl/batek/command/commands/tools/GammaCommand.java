package pl.batek.command.commands.tools;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;
import pl.batek.util.SoundsUtil;

@Command(name = "gamma", aliases = {"jasnosc", "fullbright"})
public class GammaCommand {

    @Execute
    void execute(@Context Player player) {
        if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            PlayerMessage.message(player, MessageType.SUBTITLE, "<red>Wyłączono jasność.");
            SoundsUtil.glass(player);
        } else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false, false));
            PlayerMessage.message(player, MessageType.SUBTITLE, "<green>Włączono jasność.");
            SoundsUtil.accept(player);
        }
    }
}