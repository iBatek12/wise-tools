package pl.batek.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import org.bukkit.boss.BarStyle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration extends OkaeriConfig {
    @Comment("Ustawienia bazy danych MySQL")
    public String host = "localhost";
    public int port = 3306;
    public String database = "wisemc";
    public String username = "root";
    public String password = "password";

    @Comment("Kordynaty sprawdzarki (pokoju dla cheaterów)")
    public String sprawdzarkaWorld = "world";
    public double sprawdzarkaX = 100.0;
    public double sprawdzarkaY = 100.0;
    public double sprawdzarkaZ = 100.0;
    public float sprawdzarkaYaw = 0.0f;
    public float sprawdzarkaPitch = 0.0f;

    @Comment("Kordynaty spawna")
    public String spawnWorld = "world";
    public double spawnX = 0.0;
    public double spawnY = 100.0;
    public double spawnZ = 0.0;
    public float spawnYaw = 0.0f;
    public float spawnPitch = 0.0f;

    @Comment("Kordynaty strefy AFK")
    public String afkWorld = "world";
    public double afkX = 0.0;
    public double afkY = 100.0;
    public double afkZ = 0.0;
    public float afkYaw = 0.0f;
    public float afkPitch = 0.0f;

    @Comment("Limit armor standów na jeden chunk")
    public int armorStandPerChunkLimit = 4;

    @Comment("Wiadomość gdy gracz osiągnie limit armor standów w chunku")
    public String msgArmorStandLimit = "<red>Osiągnięto limit <dark_red>armor standów <red>w tym miejscu!";

    @Comment("Czas (w sekundach) co ile ma się pojawiać automatyczna wiadomość na czacie")
    public int autoMessageInterval = 120; // Domyślnie co 2 minuty

    @Comment("Lista automatycznych wiadomości")
    public List<String> autoMessages = Arrays.asList(
            "<b><gradient:#EC6FA3:#F297BD:#EC6FA3>ᴡɪѕᴇ</gradient><white>ᴍᴄ.ᴘʟ</b><dark_gray>♯ <white>Znalazłeś cheatera? Zgłoś go na <#F297BD><click:open_url:'https://discord.gg/PjQGxjPr3r'>Discordzie</click></#F297BD>",
            "<b><gradient:#EC6FA3:#F297BD:#EC6FA3>ᴡɪѕᴇ</gradient><white>ᴍᴄ.ᴘʟ</b> <dark_gray>♯ <white>Dołącz do naszej społeczności <#F297BD><click:open_url:'https://discord.gg/PjQGxjPr3r'>dc.wisemc.pl</click></#F297BD>",
            "<b><gradient:#EC6FA3:#F297BD:#EC6FA3>ᴡɪѕᴇ</gradient><white>ᴍᴄ.ᴘʟ</b> <dark_gray>♯ <white>Nie używaj <#F297BD>cheatów <white>ani <#F297BD>modów wspomagających!",
            "<b><gradient:#EC6FA3:#F297BD:#EC6FA3>ᴡɪѕᴇ</gradient><white>ᴍᴄ.ᴘʟ</b> <dark_gray>♯ <white>Zgłaszaj <#F297BD>błędy <white>i <#F297BD>sugestie <white>na naszym <click:open_url:'https://discord.gg/PjQGxjPr3r'>Discordzie</click>",
            "<b><gradient:#EC6FA3:#F297BD:#EC6FA3>ᴡɪѕᴇ</gradient><white>ᴍᴄ.ᴘʟ</b> <dark_gray>♯ <white>Nie podawaj nikomu swojego <#F297BD>hasła <white>ani <#F297BD>danych dostępu!",
            "<b><gradient:#EC6FA3:#F297BD:#EC6FA3>ᴡɪѕᴇ</gradient><white>ᴍᴄ.ᴘʟ</b> <dark_gray>♯ <white>Czat jest dla <#F297BD>wszystkich <white>zachowaj <#F297BD>kulturę!",
            "<b><gradient:#EC6FA3:#F297BD:#EC6FA3>ᴡɪѕᴇ</gradient><white>ᴍᴄ.ᴘʟ</b> <dark_gray>♯ <white>Zaproś <#F297BD>znajomych <white>na nasz serwer!",
            "<b><gradient:#EC6FA3:#F297BD:#EC6FA3>ᴡɪѕᴇ</gradient><white>ᴍᴄ.ᴘʟ</b> <dark_gray>♯ <red>Pamiętaj, że jest to <#fc3728>ANARCHIA<red>. Nie ufaj nikomu każdy może cię\n  <u>okraść<reset><red>!",
            "<b><gradient:#EC6FA3:#F297BD:#EC6FA3>ᴡɪѕᴇ</gradient><white>ᴍᴄ.ᴘʟ</b> <dark_gray>♯ <white>Masz <#F297BD>pytania? <white>Użyj <#F297BD><hover:show_text:'<red>Kliknij aby użyć'><insert:/helpop>/helpop</insert>",
            "<b><gradient:#EC6FA3:#F297BD:#EC6FA3>ᴡɪѕᴇ</gradient><white>ᴍᴄ.ᴘʟ</b> <dark_gray>♯ <white>Każdy gracz to potencjalny <#F297BD>sojusznik <white>lub <#F297BD>wróg!",
            "<b><gradient:#EC6FA3:#F297BD:#EC6FA3>ᴡɪѕᴇ</gradient><white>ᴍᴄ.ᴘʟ</b><dark_gray>♯ <white>Wsparcie serwera pomaga nam w <#F297BD>utrzymaniu <white>i <#F297BD>rozwijaniu!"
    );

    @Comment("Ustawienia bota Discord")
    public String discordBotToken = "TWÓJ_TOKEN_BOTA";
    public String discordRewardChannelId = "ID_KANALU_ANARCHIAFFA";
    public String discordRewardCommand = "lp user {player} parent add vip";

    @Comment("Konfiguracja jednorazowych kodow (np. /kod start)")
    public List<CodeReward> codes = List.of(new CodeReward());

    public static class CodeReward extends OkaeriConfig {
        public String name = "start";
        public String requiredTime = "1m";
        public List<String> broadcast = List.of(
                "",
                "<#F297BD>☺ <dark_gray>⁑ <white>Gracz <#F297BD><player> <white>odebrał darmowe <#F297BD><b>klucze</b>",
                "<#F297BD>☺ <dark_gray>⁑ <white>Uzywając kodu <#F297BD><b>/ᴋᴏᴅ ꜱᴛᴀʀᴛ</b>"
        );
        public List<String> commands = List.of(
                "case give <player> anarchiczna 10",
                "case give <player> epicka 10",
                "case give <player> skarbow 10",
                "case give <player> rzadka 10"
        );
    }

    @Comment("Ustawienia automatycznego KeyAll")
    public int keyallStartHour = 15;
    public int keyallEndHour = 22;
    public List<String> keyallCommands = List.of(
            "case giveall anarchiczna 1",
            "case giveall epicka 1",
            "case giveall skarbow 1",
            "case giveall rzadka 1"
    );

    @Comment("=== USTAWIENIA KEYALL (BossBar i Klucze) ===")
    public Map<String, String> keyAllKeys = new HashMap<String, String>() {{
        put("SKARBÓW", "Klucze Skarbów|case giveall skarbow {AMOUNT}");
        put("RZADKA", "Klucze Rzadkie|case giveall rzadka {AMOUNT}");
        put("ANARCHICZNA", "Klucze Anarchiczne|case giveall anarchiczna {AMOUNT}");
        put("EPICKA", "Klucze Epickie|case giveall epicka {AMOUNT}");
        put("AFK", "Klucze Afk|case giveall afk {AMOUNT}");
    }};

    @Comment("Wygląd powiadomienia BossBar podczas eventu KeyAll")
    public String keyAllBossBarTitle = "&#FFEE8C☀ &8⁑ &fZa &#FFEE8C{TIME} &fcały serwer otrzyma &#DBCC79&l{KEY} x{AMOUNT}!";
    public org.bukkit.boss.BarColor keyAllBossBarColor = org.bukkit.boss.BarColor.YELLOW;
    public org.bukkit.boss.BarStyle keyAllBossBarStyle = BarStyle.SEGMENTED_12;

    @Comment("Konfiguracja RTP i granicy mapy")
    public String rtpWorld = "world";
    public int rtpMin = 300;
    public int rtpMax = 700;
    public int borderSize = 1000;

    @Comment("AFK Zone - Ustawienia")
    public boolean afkZoneEnabled = true;
    public String afkZoneRegion = "afkzone";

    @Comment("AFK Zone - Nagroda Premium (Klucz)")
    public int afkPremiumRewardSeconds = 1200; // 20 minut
    public String afkPremiumRewardCommand = "case give <player> afk 1";
    public String afkPremiumRewardTitle = "<#FF9300>⚓ <dark_gray>⁎ <white>Losowanie klucza afk <#FF9300>szansa <#FF9300><chance>% <white>za <#FF9300><time> <dark_gray>⁑ <#FF9300><b><percentage>%";
    public String afkPremiumRewardSuccessMessage = "<green>Otrzymałeś super <dark_green>nagrodę!";
    public String afkPremiumRewardFailMessage = "<red>Nie udało się trafić <dark_red>super nagrody!";

    @Comment("AFK Zone - Nagroda Standardowa (Pieniądze)")
    public int afkStandardRewardSeconds = 60;
    public double afkStandardRewardMoney = 5.0;
    public String afkStandardRewardTitle = "<#1cfc03><b>$</b> <dark_gray>⁎ <white>Darmowe <#1cfc03>$<money> <white>otrzymasz za <#1cfc03><time> <dark_gray>⁑ <#1cfc03><b><percentage>%";

    @Comment("AFK Zone - BossBary")
    public String afkPremiumBossBarColor = "YELLOW";
    public String afkPremiumBossBarStyle = "SEGMENTED_20"; // Zmienione z NOTCHED_20
    public String afkStandardBossBarColor = "GREEN";
    public String afkStandardBossBarStyle = "SEGMENTED_12"; // Zmienione z NOTCHED_12

    @Comment("AFK Zone - Wiadomości")
    public String msgAfkEnabled = "<green>Dołączyłeś na strefę <dark_green>AFK!";
    public String msgAfkDisabled = "<red>Wyszedłeś ze strefy <dark_red>AFK!";

    @Comment("Wiadomosci chatu")
    public String chatDisabledMessage = "&cChat jest aktualnie wyłączony!";
    public String chatSlowmodeMessage = "&cNastępną wiadomość możesz wysłać za &6{time}s&c!";

    @Comment("Formaty czatu dla rang z LuckPerms")
    public Map<String, String> groupFormats = new HashMap<>() {{
        put("default", "<gray><b>ɢʀᴀᴄᴢ</b> <white>{name}<dark_gray> »<reset> <gray>{message}");
        put("vip", "<#FFFF9E><b>ᴠɪᴘ</b> <white>{name}<white><dark_gray> »<reset> <#FFFF9E>{message}");
        put("svip", "<#FFC58B><b>ꜱᴠɪᴘ</b> <white>{name}<dark_gray> »<reset> <#FFC58B>{message}");
        put("mvip", "<#9AFF9A><b>ᴍᴠɪᴘ</b> <white>{name}<dark_gray> »<reset> <#9AFF9A>{message}");
        put("wise", "<b><gradient:#FD6BBA:#F7C6FF:#FD6BBA>ᴡɪѕᴇ</gradient></b> <white>{name}<dark_gray> »<reset> <#F7C6FF>{message}");
        put("amor", "<b><gradient:#FC7DC1:#F2C6DE:#FC7DC1>ᴀᴍᴏʀ</gradient></b> <white>{name}<dark_gray> »<reset> <#F2C6DE>{message}");
        put("bunny", "<gradient:#FFDEF0:#E1D5FF>ʙ</gradient><gradient:#E1D5FF:#BBEFFF>ᴜ</gradient><gradient:#BBEFFF:#D0ECD3>ɴ</gradient><gradient:#FAFFCD:#E7CC9B>ɴ</gradient><gradient:#E7CC9B:#A49888>ʏ</gradient> <white>{name}<dark_gray> »<reset> <#E1D5FF>{message}");
        put("media", "<b><gradient:#DC7FFC:#F3D0FF:#DC7FFC>ᴍᴇᴅɪᴀ</gradient></b> <white>{name}<dark_gray> »<reset> <#D7B6FD>{message}");
        put("tworca", "<b><gradient:#8A2FF6:#D7B6FD:#8A2FF6>ᴛᴡóʀᴄᴀ</gradient></b> <white>{name}<dark_gray> »<reset> <#D7B6FD>{message}");
        put("helper", "<b><gradient:#97CFF5:#B1E0FF:#97CFF5>ʜᴇʟᴘᴇʀ</gradient></b> {name}<dark_gray> »<reset> <#97CFF5>{message}");
        put("moderator", "<b><gradient:#00FF25:#6EFF3F>ᴍᴏᴅᴇʀᴀᴛᴏʀ</gradient></b> {name}<dark_gray> »<reset> <#00FF25>{message}");
        put("admin", "<b><gradient:#F12626:#FF3F3F>ᴀᴅᴍɪɴ</gradient></b> {name}<dark_gray> »<reset> <#F12626>{message}");
        put("op", "<b><gradient:#FA2E4F:#FFA5B4:#FA2E4F>ᴏᴘ ᴀᴅᴍ</gradient></b> {name}<dark_gray> »<reset> <#FFA5B4>{message}");
        put("headadmin", "<b><gradient:#FF0000:#FF2B2B>ʜᴇᴀᴅᴀᴅᴍɪɴ</gradient></b> {name}<dark_gray> »<reset> <#FF0000>{message}");
        put("root", "<b><gradient:#FF0000:#FF2B2B>ᴄᴇᴏ</gradient></b> {name}<dark_gray> »<reset> <#FF0000>{message}");
        put("dev", "<b><gradient:#FF2B2B:#E45252>ᴅᴇᴠ</gradient></b> {name}<dark_gray> »<reset> <#FF2B2B>{message}");
    }};
}