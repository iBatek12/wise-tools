package pl.batek;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteCommandsBukkit;
import dev.rollczi.litecommands.bukkit.LiteBukkitMessages;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import pl.batek.command.commands.*;
import pl.batek.command.argument.KeyTypeArgument;
import pl.batek.command.argument.OfflinePlayerArgument;
import pl.batek.command.commands.message.*;
import pl.batek.command.commands.teleport.*;
import pl.batek.command.commands.tools.*;
import pl.batek.controller.*;
import pl.batek.database.DatabaseManager;
import pl.batek.economy.EconomyManager;
import pl.batek.economy.VaultHook;
import pl.batek.manager.*;
import pl.batek.command.handler.*;
import pl.batek.config.Configuration;
import pl.batek.placeholder.WisePlaceholder;

import java.io.File;
import java.util.logging.Level;

public final class WiseMain extends JavaPlugin {
    private static WiseMain main;
    private Configuration configuration;
    private DatabaseManager databaseManager;
    private MessageManager messageManager;
    private TpaManager tpaManager;
    private ChatManager chatManager;
    private FreeItemsManager freeItemsManager;
    private SettingsManager settingsManager;
    private HomeManager homeManager;
    private AutoMessageManager autoMessageManager;
    private AfkZoneManager afkManager;
    private PlaytimeManager playtimeManager;
    private TopManager topManager;
    private KeyAllManager keyAllManager;
    private EconomyManager economyManager;
    private VanishManager vanishManager;
    private VaultHook vaultHook;
    private SpawnManager spawnManager;
    private CodeManager codeManager;
    private EventManager eventManager;
    private SprawdzManager sprawdzManager;
    private EndManager endManager;
    private DiscordRewardManager discordRewardManager;
    private DiscordBotManager discordBotManager;
    private LiteCommands<CommandSender> liteCommands;

    @Override
    public void onEnable() {
        main = this;
        this.registerConfigs();
        this.initDatabase();
        this.initManagers();
        this.registerVault();
        this.registerPlaceholders();
        this.registerListeners();
        this.registerCommands();
    }

    @Override
    public void onDisable() {
        if (this.liteCommands != null) {
            this.liteCommands.unregister();
        }
        if (this.databaseManager != null) {
            this.databaseManager.disconnect();
        }
        if (this.endManager != null) {
            this.endManager.clearAll();
        }
        if (this.discordBotManager != null) {
            this.discordBotManager.stop();
        }
    }

    private void registerConfigs() {
        this.configuration = this.loadConfig(Configuration.class, "configuration.yml");
    }

    private void initDatabase() {
        this.databaseManager = new DatabaseManager(
                this.configuration.host,
                this.configuration.port,
                this.configuration.database,
                this.configuration.username,
                this.configuration.password
        );
        try {
            this.databaseManager.connect();
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Blad polaczenia z baza MySQL!", e);
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void initManagers() {
        this.economyManager = new EconomyManager(this, this.databaseManager);
        this.settingsManager = new SettingsManager();
        this.keyAllManager = new KeyAllManager(this, this.configuration, this.settingsManager);
        this.messageManager = new MessageManager();
        this.chatManager = new ChatManager();
        this.homeManager = new HomeManager(this);
        this.freeItemsManager = new FreeItemsManager(this, this.databaseManager);
        this.tpaManager = new TpaManager();
        this.endManager = new EndManager(this);
        this.vanishManager = new VanishManager(this);
        this.spawnManager = new SpawnManager(this, this.configuration);
        this.vaultHook = new VaultHook(this, this.economyManager);
        this.codeManager = new CodeManager(this.databaseManager);
        this.afkManager = new AfkZoneManager(this, this.configuration, this.economyManager);
        this.playtimeManager = new PlaytimeManager(this, this.databaseManager);
        this.topManager = new TopManager(this, this.databaseManager);
        this.eventManager = new EventManager(this, this.configuration);
        this.sprawdzManager = new SprawdzManager(this);
        this.autoMessageManager = new AutoMessageManager(this, this.configuration);
        this.discordRewardManager = new DiscordRewardManager(this, this.configuration, this.databaseManager);
        this.discordBotManager = new DiscordBotManager(this.configuration, this.discordRewardManager);
        this.getServer().getScheduler().runTaskAsynchronously(this, () -> {
            this.discordBotManager.start();
        });
    }

    private void registerVault() {
        if (this.getServer().getPluginManager().getPlugin("Vault") != null) {
            this.getServer().getServicesManager().register(Economy.class, this.vaultHook, this, ServicePriority.Highest);
            this.getLogger().info("Zarejestrowano ekonomię w Vault!");
        } else {
            this.getLogger().warning("Vault nie zostal znaleziony! Ekonomia nie bedzie dzialac.");
        }
    }

    private void registerPlaceholders() {
        if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new WisePlaceholder(this.vanishManager, this.topManager).register();
            this.getLogger().info("Zarejestrowano PlaceholderAPI (vanish, top_money, top_time)!");
        } else {
            this.getLogger().warning("PlaceholderAPI nie znaleziono! Placeholdery nie beda dzialac.");
        }
    }

    private void registerListeners() {
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new RespawnController(this.configuration), this);
        pm.registerEvents(new EndController(this.endManager), this);
        pm.registerEvents(new ArmorStandController(this.configuration), this);
        pm.registerEvents(this.playtimeManager, this);
        pm.registerEvents(new BorderController(this.configuration), this);
        pm.registerEvents(new ChatController(this, this.chatManager, this.settingsManager, this.configuration), this);
        pm.registerEvents(new MenuController(), this);
        pm.registerEvents(new FreeItemsJoinController(this.freeItemsManager), this);
        pm.registerEvents(this.vanishManager, this);
        pm.registerEvents(new EconomyController(this.economyManager), this);
        pm.registerEvents(this.spawnManager, this);
        pm.registerEvents(new MechanismController(), this);
        pm.registerEvents(new CodeController(this.codeManager), this);
        pm.registerEvents(this.afkManager, this);
        pm.registerEvents(new SpawnRepairController(), this);
        pm.registerEvents(this.eventManager, this);
        pm.registerEvents(new SprawdzController(this.sprawdzManager), this);
    }

    private void registerCommands() {
        OfflinePlayerArgument offlinePlayerArgument = new OfflinePlayerArgument();
        KeyTypeArgument keyTypeArgument = new KeyTypeArgument(this.configuration);

        this.liteCommands = LiteCommandsBukkit.builder()
                .settings(settings -> settings.fallbackPrefix("WISE-TOOLS").nativePermissions(false))
                .argumentParser(OfflinePlayer.class, offlinePlayerArgument)
                .argumentSuggester(OfflinePlayer.class, offlinePlayerArgument)
                .argumentParser(pl.batek.command.argument.KeyTypeWrapper.class, keyTypeArgument)
                .argumentSuggester(pl.batek.command.argument.KeyTypeWrapper.class, keyTypeArgument)

                .commands(
                        new DiscordCommand(),
                        new SpawnCommand(this.spawnManager),
                        new RepairCommand(),
                        new CodeCommand(this.configuration, this.codeManager, this),
                        new InvseeCommand(),
                        new TrashCommand(),
                        new HomeCommand(this.homeManager),
                        new WorkBenchCommand(),
                        new MessageCommand(this.messageManager),
                        new IgnoreCommand(this.messageManager),
                        new ReplyCommand(this.messageManager),
                        new ChatCommand(this.chatManager),
                        new GamemodeCommand(),
                        new ShopCommand(this.economyManager, this.playtimeManager),
                        new FreeItemsCommand(this.freeItemsManager),
                        new SettingsCommand(this.settingsManager),
                        new AfkCommand(this.configuration),
                        new StpCommand(),
                        new TpaCommand(this.tpaManager),
                        new TpacceptCommand(this.tpaManager),
                        new SpeedCommand(),
                        new BroadcastCommand(),
                        new AnvilCommand(),
                        new PomocCommand(),
                        new HelpopCommand(),
                        new PayCommand(this.vaultHook),
                        new EcoCommand(this.vaultHook),
                        new FlyCommand(),
                        new VanishCommand(this.vanishManager),
                        new BalanceCommand(this.vaultHook),
                        new GammaCommand(),
                        new PrzyznajeSieCommand(this.configuration, this, this.sprawdzManager),
                        new SprawdzCommand(this.configuration, this, this.sprawdzManager),
                        new AutoEventCommand(this.eventManager, this.configuration),
                        new NagrodaDiscordCommand(this.discordRewardManager)
                )
                .message(LiteBukkitMessages.PLAYER_ONLY, "&cTylko gracz może użyć tej komendy!")
                .message(LiteBukkitMessages.PLAYER_NOT_FOUND, input -> "&cGracz &4" + input + " &cnie został odnaleziony!")
                .missingPermission(new MissingPermissionsHandler())
                .invalidUsage(new InvalidUsageHandler())
                .build();
    }

    private <T extends OkaeriConfig> T loadConfig(Class<T> clazz, String fileName) {
        try {
            return ConfigManager.create(clazz, it -> {
                it.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit());
                it.withBindFile(new File(this.getDataFolder(), fileName));
                it.saveDefaults();
                it.load(true);
            });
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Error loading config: " + fileName, e);
            this.getServer().getPluginManager().disablePlugin(this);
            return null;
        }
    }

    public static WiseMain getInstance() {
        return main;
    }
}