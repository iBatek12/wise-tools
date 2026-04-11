package pl.batek.controller;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.batek.WiseMain;
import pl.batek.config.Configuration;
import pl.batek.manager.ChatManager;
import pl.batek.manager.SettingsManager;
import pl.batek.util.AdventureUtil;

import java.util.regex.Pattern;

public class ChatController implements Listener {
    private static final Pattern FORBIDDEN_PATTERN = Pattern.compile("[&<>\\\\]");
    private static final String DEFAULT_FORMAT = "<gray><b>ɢʀᴀᴄᴢ</b> <white>{name}<dark_gray> »<reset> <gray>{message}";
    private final ChatManager chatManager;
    private final SettingsManager settingsManager;
    private final Configuration configuration;
    private final LuckPerms luckPerms;
    private final WiseMain plugin;

    public ChatController(WiseMain plugin, ChatManager chatManager, SettingsManager settingsManager, Configuration configuration) {
        this.plugin = plugin;
        this.chatManager = chatManager;
        this.settingsManager = settingsManager;
        this.configuration = configuration;
        this.luckPerms = LuckPermsProvider.get();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        String message = event.getMessage();
        if (!sender.hasPermission("core.admin") && FORBIDDEN_PATTERN.matcher(message).find()) {
            event.setCancelled(true);
            processSecurityKick(sender);
            return;
        }

        if (!chatManager.isChatEnabled() && !sender.hasPermission("wisemc.chat.bypass")) {
            event.setCancelled(true);
            sender.sendMessage(AdventureUtil.translate(configuration.chatDisabledMessage));
            return;
        }

        if (!sender.hasPermission("wisemc.chat.slowmode.bypass")) {
            long remaining = chatManager.getRemainingCooldown(sender.getUniqueId());
            if (remaining > 0) {
                event.setCancelled(true);
                sender.sendMessage(AdventureUtil.translate(
                        configuration.chatSlowmodeMessage.replace("{time}", String.valueOf(remaining))
                ));
                return;
            }
            chatManager.updateCooldown(sender.getUniqueId());
        }

        event.setCancelled(true);
        String safeMessage = sender.hasPermission("wisemc.chat.minimessage")
                ? message
                : MiniMessage.miniMessage().escapeTags(message);
        String group = getPrimaryGroup(sender);
        String format = configuration.groupFormats.getOrDefault(group, configuration.groupFormats.getOrDefault("default", DEFAULT_FORMAT));
        String finalMessageString = format
                .replace("{name}", sender.getName())
                .replace("{message}", safeMessage);

        Component finalComponent = AdventureUtil.miniMessage(finalMessageString, null);

        for (Player recipient : event.getRecipients()) {
            if (recipient.equals(sender) || settingsManager.isChatVisible(recipient.getUniqueId())) {
                recipient.sendMessage(finalComponent);
            }
        }
        Bukkit.getConsoleSender().sendMessage(finalComponent);
    }

    private void processSecurityKick(Player player) {
        String name = player.getName();
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "kick " + name + " &cPróba użycia formatowania na czacie! &7(&e&, <, >, \\&7)");
        });
    }

    private String getPrimaryGroup(Player player) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        return (user != null) ? user.getPrimaryGroup() : "default";
    }
}