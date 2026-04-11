package pl.batek.command.commands;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;
import pl.batek.manager.SettingsManager;
import pl.batek.menu.menus.SettingsGui;

@Command(name = "ustawienia", aliases = {"settings"})
public class SettingsCommand {

    private final SettingsManager settingsManager;

    public SettingsCommand(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Execute
    void execute(@Context Player player) {
        new SettingsGui(settingsManager).show(player);
    }
}