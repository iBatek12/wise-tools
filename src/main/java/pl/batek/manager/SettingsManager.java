package pl.batek.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SettingsManager {

    private final Set<UUID> chatDisabled = new HashSet<>();
    private final Set<UUID> titlesDisabled = new HashSet<>(); // NOWE

    public boolean isChatVisible(UUID uuid) {
        return !chatDisabled.contains(uuid);
    }

    public boolean toggleChat(UUID uuid) {
        if (chatDisabled.contains(uuid)) {
            chatDisabled.remove(uuid);
            return true;
        } else {
            chatDisabled.add(uuid);
            return false;
        }
    }

    // --- NOWE METODY ---
    public boolean isTitleVisible(UUID uuid) {
        return !titlesDisabled.contains(uuid);
    }

    public boolean toggleTitle(UUID uuid) {
        if (titlesDisabled.contains(uuid)) {
            titlesDisabled.remove(uuid);
            return true;
        } else {
            titlesDisabled.add(uuid);
            return false;
        }
    }
}