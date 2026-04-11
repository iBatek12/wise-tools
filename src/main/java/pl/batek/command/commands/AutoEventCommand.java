package pl.batek.command.commands;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import pl.batek.config.Configuration;
import pl.batek.manager.EventManager;
import pl.batek.util.AdventureUtil;

@Command(name = "autoevent")
@Permission("wisemc.autoevent")
public class AutoEventCommand {

    private final EventManager eventManager;
    private final Configuration config;

    public AutoEventCommand(EventManager eventManager, Configuration config) {
        this.eventManager = eventManager;
        this.config = config;
    }

    // /autoevent stop [event]
    @Execute(name = "stop")
    void stop(@Context CommandSender sender, @Arg("event") String event) {
        eventManager.stopEvent(event);
        sender.sendMessage(AdventureUtil.translate("&aZatrzymano event: &7" + event));
    }

    // /autoevent start smok [czas_minuty]
    @Execute(name = "start smok")
    void startSmok(@Context CommandSender sender, @Arg("minuty") int minutes) {
        int seconds = minutes * 60;
        eventManager.startEvent("smok", seconds, null, 0);
        sender.sendMessage(AdventureUtil.translate("&aWystartowano odliczanie do eventu &7SMOK &a(&7" + minutes + " min&a)"));
    }

    // Wcześniej było: @Arg("typ") String keyType
    @Execute(name = "start keyall")
    void startKeyAll(@Context CommandSender sender,
                     @Arg("minuty") int minutes,
                     @Arg("typ") pl.batek.command.argument.KeyTypeWrapper keyTypeWrapper,
                     @Arg("ilosc") int amount) {

        // Wyciągamy Stringa z naszego wrappera
        String upperType = keyTypeWrapper.getKey().toUpperCase();

        // Zabezpieczenie: Sprawdzamy czy klucz istnieje w pliku konfiguracyjnym
        if (!config.keyAllKeys.containsKey(upperType)) {
            sender.sendMessage(pl.batek.util.AdventureUtil.translate("&cNieznany typ klucza! Dostępne typy to: &7" + String.join(", ", config.keyAllKeys.keySet())));
            return;
        }

        int seconds = minutes * 60;
        eventManager.startEvent("keyall", seconds, upperType, amount);
        sender.sendMessage(pl.batek.util.AdventureUtil.translate("&aWystartowano odliczanie do &7KEYALL &a(&7" + upperType + " x" + amount + "&a) za &7" + minutes + " min"));
    }
}