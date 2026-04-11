package pl.batek.command.commands;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;
import pl.batek.manager.DiscordRewardManager;
import pl.batek.menu.menus.NagrodaDiscordGui;

@Command(name = "nagrodadiscord")
public class NagrodaDiscordCommand {
    private final DiscordRewardManager rewardManager;

    public NagrodaDiscordCommand(DiscordRewardManager rewardManager) {
        this.rewardManager = rewardManager;
    }

    @Execute
    void execute(@Context Player player) {
        new NagrodaDiscordGui(rewardManager).show(player);
    }
}