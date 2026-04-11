package pl.batek.command.commands.tools;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.batek.util.AdventureUtil;

@Command(name = "gamemode", aliases = {"gm"})
public class GamemodeCommand {

    @Execute
    void executeSelf(@Context Player player, @Arg("tryb") String modeString) {
        GameMode gameMode = getGameModeFromString(modeString);

        if (gameMode == null) {
            player.sendMessage(AdventureUtil.translate("&cNieznany tryb gry! Użyj: 0, 1, 2, 3 lub survival, creative itp."));
            return;
        }

        // Sprawdzanie permisji dla konkretnego trybu
        if (!hasGamemodePermission(player, gameMode)) {
            player.sendMessage(AdventureUtil.translate("&cNie masz uprawnień do trybu &7" + formatGameMode(gameMode) + "&c!"));
            return;
        }

        player.setGameMode(gameMode);
        player.sendMessage(AdventureUtil.translate("&aPomyślnie zmieniono własny tryb gry na &e" + formatGameMode(gameMode) + "&a."));
    }

    @Execute
    void executeOther(@Context CommandSender sender, @Arg("tryb") String modeString, @Arg("gracz") Player target) {
        // Zabezpieczenie przed zmienianiem trybu innym graczom
        if (!sender.hasPermission("wisemc.command.gamemode.other")) {
            sender.sendMessage(AdventureUtil.translate("&cNie masz uprawnień do zmiany trybu gry innych graczy!"));
            return;
        }

        GameMode gameMode = getGameModeFromString(modeString);

        if (gameMode == null) {
            sender.sendMessage(AdventureUtil.translate("&cNieznany tryb gry! Użyj: 0, 1, 2, 3 lub survival, creative itp."));
            return;
        }

        // Sprawdzanie permisji nadającego dla konkretnego trybu
        if (!hasGamemodePermission(sender, gameMode)) {
            sender.sendMessage(AdventureUtil.translate("&cNie masz uprawnień do nadawania trybu &7" + formatGameMode(gameMode) + "&c!"));
            return;
        }

        target.setGameMode(gameMode);
        sender.sendMessage(AdventureUtil.translate("&aZmieniono tryb gry gracza &7" + target.getName() + " &ana &e" + formatGameMode(gameMode) + "&a."));
        target.sendMessage(AdventureUtil.translate("&aTwój tryb gry został zmieniony na &e" + formatGameMode(gameMode) + " &aprzez &7" + sender.getName() + "&a."));
    }

    private boolean hasGamemodePermission(CommandSender sender, GameMode gameMode) {
        switch (gameMode) {
            case SURVIVAL: return sender.hasPermission("wisemc.command.gamemode.survival");
            case CREATIVE: return sender.hasPermission("wisemc.command.gamemode.creative");
            case ADVENTURE: return sender.hasPermission("wisemc.command.gamemode.adventure");
            case SPECTATOR: return sender.hasPermission("wisemc.command.gamemode.spectator");
            default: return false;
        }
    }

    private GameMode getGameModeFromString(String input) {
        switch (input.toLowerCase()) {
            case "0":
            case "s":
            case "survival":
                return GameMode.SURVIVAL;
            case "1":
            case "c":
            case "creative":
                return GameMode.CREATIVE;
            case "2":
            case "a":
            case "adventure":
                return GameMode.ADVENTURE;
            case "3":
            case "sp":
            case "spectator":
                return GameMode.SPECTATOR;
            default:
                return null;
        }
    }

    private String formatGameMode(GameMode gameMode) {
        switch (gameMode) {
            case SURVIVAL: return "Survival";
            case CREATIVE: return "Creative";
            case ADVENTURE: return "Adventure";
            case SPECTATOR: return "Spectator";
            default: return "Nieznany";
        }
    }
}