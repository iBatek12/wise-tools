package pl.batek.command.commands.tools;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.batek.util.AdventureUtil;

@Command(name = "speed", aliases = {"predkosc"})
@Permission("wisemc.command.speed")
public class SpeedCommand {

    // 1. Zmiana prędkości dla samego siebie
    @Execute
    void executeSelf(@Context Player player, @Arg("prędkość (1-10)") int speed) {
        changeSpeed(player, player, speed);
    }

    // 2. Zmiana prędkości innemu graczowi
    @Execute
    void executeOther(@Context CommandSender sender, @Arg("prędkość (1-10)") int speed, @Arg("gracz") Player target) {
        changeSpeed(sender, target, speed);
    }

    private void changeSpeed(CommandSender sender, Player target, int speed) {
        // Zabezpieczenie limitów Bukkita (wymaga wartości od -1.0 do 1.0)
        if (speed < 1 || speed > 10) {
            sender.sendMessage(AdventureUtil.translate("&cPrędkość musi wynosić od 1 do 10!"));
            return;
        }

        // Przeliczamy (1 = 0.1f, 10 = 1.0f)
        float realSpeed = speed / 10.0f;
        String type;

        if (target.isFlying()) {
            target.setFlySpeed(realSpeed);
            type = "latania";
        } else {
            // Bukkit domyślnie ma mniejszą prędkość chodzenia (0.2), więc trochę to ujednolicamy
            target.setWalkSpeed(realSpeed);
            type = "chodzenia";
        }

        if (sender.equals(target)) {
            sender.sendMessage(AdventureUtil.translate("&aPomyślnie zmieniono Twoją prędkość " + type + " na &e" + speed + "&a."));
        } else {
            sender.sendMessage(AdventureUtil.translate("&aZmieniono prędkość " + type + " gracza &7" + target.getName() + " &ana &e" + speed + "&a."));
            target.sendMessage(AdventureUtil.translate("&aTwoja prędkość " + type + " została zmieniona na &e" + speed + " &aprzez &7" + sender.getName() + "&a."));
        }
    }
}