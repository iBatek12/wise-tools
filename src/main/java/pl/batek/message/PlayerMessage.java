package pl.batek.message;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.batek.util.AdventureUtil;

public class PlayerMessage {
    public static void message(CommandSender sender, MessageType type, String message, Map<String, String> placeholders) {
        sendMessageToSender(sender, type, message, placeholders);
    }

    public static void message(CommandSender sender, MessageType type, String message) {
        message(sender, type, message, (Map)null);
    }

    public static void message(CommandSender sender, List<Component> components) {
        sendMessagesToSender(sender, components);
    }

    private static void sendMessageToSender(CommandSender sender, MessageType type, String rawMessage, Map<String, String> placeholders) {
        Component chat;
        if (sender instanceof Player) {
            Player player = (Player)sender;
            switch(type) {
                case TITLE:
                    chat = AdventureUtil.miniMessage(rawMessage, placeholders);
                    player.showTitle(Title.title(chat, Component.empty()));
                    break;
                case TITLE_SUBTITLE:
                    String[] parts = rawMessage.split("\n", 2);
                    Component title = AdventureUtil.miniMessage(parts[0], placeholders);
                    Component subtitle = parts.length > 1 ? AdventureUtil.miniMessage(parts[1], placeholders) : Component.empty();
                    player.showTitle(Title.title(title, (Component)subtitle));
                    break;
                case SUBTITLE:
                    chat = AdventureUtil.miniMessage(rawMessage, placeholders);
                    player.showTitle(Title.title(Component.empty(), chat));
                    break;
                case ACTIONBAR:
                    chat = AdventureUtil.miniMessage(rawMessage, placeholders);
                    player.sendActionBar(chat);
                    break;
                case CHAT:
                    chat = AdventureUtil.miniMessage(rawMessage, placeholders);
                    player.sendMessage(chat);
            }
        } else {
            chat = AdventureUtil.miniMessage(rawMessage, placeholders);
            sender.sendMessage(chat);
        }

    }

    private static void sendMessagesToSender(CommandSender sender, List<Component> components) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            Objects.requireNonNull(player);
            Objects.requireNonNull(player);
            Objects.requireNonNull(player);
            components.forEach(player::sendMessage);
        } else {
            Objects.requireNonNull(sender);
            Objects.requireNonNull(sender);
            Objects.requireNonNull(sender);
            components.forEach(sender::sendMessage);
        }

    }
}