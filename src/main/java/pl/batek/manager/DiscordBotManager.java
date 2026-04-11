package pl.batek.manager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.batek.config.Configuration;

import java.awt.Color;

public class DiscordBotManager extends ListenerAdapter {
    private JDA jda;
    private final Configuration config;
    private final DiscordRewardManager rewardManager;

    public DiscordBotManager(Configuration config, DiscordRewardManager rewardManager) {
        this.config = config;
        this.rewardManager = rewardManager;
    }

    public void start() {
        if (config.discordBotToken == null || config.discordBotToken.isEmpty() || config.discordBotToken.equals("TWÓJ_TOKEN_BOTA")) {
            return;
        }

        try {
            jda = JDABuilder.createDefault(config.discordBotToken)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .setActivity(Activity.customStatus("Serwer Tworzony z pasji."))
                    .addEventListeners(this)
                    .build();
            jda.awaitReady();
            sendRewardMessage();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (jda != null) jda.shutdown();
    }

    private void sendRewardMessage() {
        TextChannel channel = jda.getTextChannelById(config.discordRewardChannelId);
        if (channel != null) {
            channel.getIterableHistory().takeAsync(10).thenAccept(messages -> {
                boolean hasMessage = messages.stream().anyMatch(msg -> !msg.getEmbeds().isEmpty() || (!msg.getButtons().isEmpty() && msg.getButtons().stream().anyMatch(b -> "claim_reward".equals(b.getId()))));

                if (!hasMessage) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(Color.decode("#2b2d31"));
                    embed.setDescription("# ODBIERZ NAGRODĘ 🎁\n\nKliknij poniższy przycisk, wpisz swój nick i odbierz darmową rangę **VIP** w grze Minecraft!");

                    channel.sendMessageEmbeds(embed.build())
                            .addActionRow(Button.success("claim_reward", "🎁 Odbierz nagrodę"))
                            .queue();
                }
            });
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("claim_reward")) {
            TextInput nickInput = TextInput.create("mc_nick", "Twój nick w Minecraft", TextInputStyle.SHORT)
                    .setPlaceholder("Wpisz swój dokładny nick")
                    .setMinLength(3)
                    .setMaxLength(16)
                    .setRequired(true)
                    .build();

            Modal modal = Modal.create("reward_modal", "Odbiór Nagrody Discord")
                    .addActionRow(nickInput)
                    .build();

            event.replyModal(modal).queue();
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getModalId().equals("reward_modal")) {
            String discordId = event.getUser().getId();
            String nick = event.getValue("mc_nick").getAsString();

            // Zabezpieczenie przed multikontami i oszustwami!
            if (rewardManager.hasClaimedDiscord(discordId)) {
                event.reply("❌ Twoje konto Discord odebrało już nagrodę!").setEphemeral(true).queue();
                return;
            }

            if (rewardManager.hasClaimed(nick)) {
                event.reply("❌ Ten nick Minecraft odebrał już nagrodę!").setEphemeral(true).queue();
                return;
            }

            Player player = Bukkit.getPlayerExact(nick);
            if (player == null) {
                event.reply("❌ Aby odebrać nagrodę, musisz być aktualnie na serwerze Minecraft!").setEphemeral(true).queue();
                return;
            }

            // Nadanie używając id discorda i nicku
            rewardManager.claimReward(discordId, player.getName());

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.decode("#2ecc71"));
            embed.setAuthor("Odbiór Nagrody | Anarchia", null, event.getJDA().getSelfUser().getEffectiveAvatarUrl());
            embed.setTitle("Nagroda została pomyślnie odebrana!");
            embed.setDescription("Ranga vip została nadana na nick `" + player.getName() + "`");

            event.replyEmbeds(embed.build()).setEphemeral(true).queue();
        }
    }
}