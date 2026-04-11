/* Decompiler 116ms, total 296ms, lines 260 */
package pl.batek.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.Builder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

public final class AdventureUtil {
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder().character('&').hexCharacter('#').hexColors().extractUrls().useUnusualXRepeatedCharacterHexFormat().build();
    private static final LegacyComponentSerializer SECTION_SERIALIZER = LegacyComponentSerializer.builder().character('§').hexCharacter('#').hexColors().extractUrls().useUnusualXRepeatedCharacterHexFormat().build();
    private static final PlainTextComponentSerializer PLAIN_TEXT_COMPONENT_SERIALIZER = PlainTextComponentSerializer.plainText();
    private static final Style STYLE;
    private static final MiniMessage MINI;

    private AdventureUtil() {
    }

    private static Component stripItalics(Component component) {
        Style noItalic = component.style().decoration(TextDecoration.ITALIC, State.FALSE);
        component = component.style(noItalic);
        List<Component> children = component.children().stream().map(AdventureUtil::stripItalics).toList();
        return component.children(children);
    }

    public static Component translate(String string) {
        Component base = SERIALIZER.deserializeOrNull(string);
        return stripItalics(base);
    }

    public static List<Component> translate(String... strings) {
        return Stream.of(strings).map(AdventureUtil::translate).toList();
    }

    public static List<Component> translate(List<String> strings) {
        return strings.stream().map(AdventureUtil::translate).toList();
    }

    public static TextComponent textComponentOf(String string) {
        Component base = SERIALIZER.deserializeOrNull(string);
        return (TextComponent)stripItalics(base);
    }

    public static List<TextComponent> textComponentsOf(String... strings) {
        return Stream.of(strings).map((s) -> {
            return (TextComponent)stripItalics(SERIALIZER.deserializeOrNull(s));
        }).toList();
    }

    public static List<TextComponent> textComponentsOf(List<String> strings) {
        return strings.stream().map(AdventureUtil::textComponentOf).toList();
    }

    public static String componentToString(Component component) {
        return PLAIN_TEXT_COMPONENT_SERIALIZER.serialize(component);
    }

    public static Component miniMessage(String message, Map<String, String> placeholders) {
        String parsed = message;
        Entry e;
        if (placeholders != null) {
            for(Iterator var3 = placeholders.entrySet().iterator(); var3.hasNext(); parsed = parsed.replace("{" + (String)e.getKey() + "}", (CharSequence)e.getValue())) {
                e = (Entry)var3.next();
            }
        }

        if (parsed.contains("§")) {
            Component comp = SECTION_SERIALIZER.deserialize(parsed);
            return stripItalics(comp);
        } else {
            Component comp;
            if (parsed.contains("&")) {
                comp = translate(parsed);
                return stripItalics(comp);
            } else {
                comp = MINI.deserialize(parsed, buildPlaceholders(placeholders));
                return stripItalics(comp);
            }
        }
    }

    public static List<Component> miniMessage(Map<String, String> placeholders, String... messages) {
        return Stream.of(messages).map((msg) -> {
            return miniMessage(msg, placeholders);
        }).toList();
    }

    public static List<Component> miniMessage(List<String> messages, Map<String, String> placeholders) {
        return messages.stream().map((msg) -> {
            return miniMessage(msg, placeholders);
        }).toList();
    }

    public static TagResolver buildPlaceholders(Map<String, String> placeholders) {
        if (placeholders != null && !placeholders.isEmpty()) {
            Builder builder = TagResolver.builder();
            placeholders.forEach((key, value) -> {
                builder.resolver(Placeholder.parsed(key, value));
            });
            return builder.build();
        } else {
            return TagResolver.empty();
        }
    }

    public static void setSkullTexture(SkullMeta meta, String textureValue) {
        try {
            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
            PlayerTextures textures = profile.getTextures();
            String id;
            String version;
            if (!textureValue.startsWith("http")) {
                try {
                    id = new String(Base64.getDecoder().decode(textureValue), StandardCharsets.UTF_8);
                    Pattern pattern = Pattern.compile("\"url\":\"(https?://textures\\.minecraft\\.net/texture/[^\"]+)\"");
                    Matcher matcher = pattern.matcher(id);
                    String textureUrl;
                    URL url;
                    if (matcher.find()) {
                        textureUrl = matcher.group(1);
                        url = new URL(textureUrl);
                        textures.setSkin(url);
                    } else {
                        textureUrl = "https://textures.minecraft.net/texture/" + textureValue;
                        url = new URL(textureUrl);
                        textures.setSkin(url);
                    }
                } catch (IllegalArgumentException var20) {
                    version = "https://textures.minecraft.net/texture/" + textureValue;
                    URL url = new URL(version);
                    textures.setSkin(url);
                }
            } else {
                URL apiUrl;
                if (!textureValue.startsWith("https://minesk.in/") && !textureValue.startsWith("https://mineskin.org/")) {
                    if (textureValue.startsWith("https://textures.minecraft.net/texture/")) {
                        apiUrl = new URL(textureValue);
                        textures.setSkin(apiUrl);
                    }
                } else {
                    id = "";
                    if (textureValue.startsWith("https://minesk.in/")) {
                        id = textureValue.substring("https://minesk.in/".length());
                    } else if (textureValue.startsWith("https://mineskin.org/")) {
                        id = textureValue.substring("https://mineskin.org/".length());
                    }

                    if (id.contains("/")) {
                        id = id.substring(0, id.indexOf("/"));
                    }

                    if (id.contains("?")) {
                        id = id.substring(0, id.indexOf("?"));
                    }

                    try {
                        apiUrl = new URL("https://api.mineskin.org/get/id/" + id);
                        HttpURLConnection connection = (HttpURLConnection)apiUrl.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(5000);
                        connection.setReadTimeout(5000);
                        int status = connection.getResponseCode();
                        if (status != 200) {
                            System.out.println("Failed to fetch MineSkin texture. Status code: " + status);
                        } else {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            StringBuilder response = new StringBuilder();

                            while(true) {
                                String line;
                                if ((line = reader.readLine()) == null) {
                                    reader.close();
                                    String jsonResponse = response.toString();
                                    int urlIndex = jsonResponse.indexOf("\"url\":\"");
                                    if (urlIndex != -1) {
                                        int startIndex = urlIndex + 7;
                                        int endIndex = jsonResponse.indexOf("\"", startIndex);
                                        if (endIndex != -1) {
                                            String textureUrl = jsonResponse.substring(startIndex, endIndex);
                                            URL url = new URL(textureUrl);
                                            textures.setSkin(url);
                                        }
                                    } else {
                                        System.out.println("Could not find texture URL in MineSkin response");
                                    }
                                    break;
                                }

                                response.append(line);
                            }
                        }
                    } catch (Exception var21) {
                        System.out.println("Error fetching MineSkin texture: " + var21.getMessage());
                        var21.printStackTrace();
                    }
                }
            }

            profile.setTextures(textures);

            try {
                meta.setOwnerProfile(profile);
            } catch (Exception var19) {
                try {
                    version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
                    Class<?> craftMetaSkullClass = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftMetaSkull");
                    Field profileField = craftMetaSkullClass.getDeclaredField("profile");
                    profileField.setAccessible(true);
                    Class<?> craftPlayerProfileClass = Class.forName("org.bukkit.craftbukkit." + version + ".profile.CraftPlayerProfile");
                    Method getProfileMethod = craftPlayerProfileClass.getDeclaredMethod("getGameProfile");
                    getProfileMethod.setAccessible(true);
                    Object gameProfile = getProfileMethod.invoke(profile);
                    profileField.set(meta, gameProfile);
                } catch (Exception var18) {
                    System.out.println("Failed to set skull texture using reflection: " + var18.getMessage());
                    var19.printStackTrace();
                }
            }
        } catch (Exception var22) {
            var22.printStackTrace();
        }

    }

    static {
        STYLE = Style.style().decoration(TextDecoration.ITALIC, State.FALSE).build();
        MINI = MiniMessage.miniMessage();
    }
}