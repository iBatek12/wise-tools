package pl.batek.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatManager {
    private boolean chatEnabled = true;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final int delaySeconds = 5;

    public boolean isChatEnabled() {
        return chatEnabled;
    }

    public void setChatEnabled(boolean chatEnabled) {
        this.chatEnabled = chatEnabled;
    }

    public long getRemainingCooldown(UUID uuid) {
        if (!cooldowns.containsKey(uuid)) return 0;

        long lastShot = cooldowns.get(uuid);
        long now = System.currentTimeMillis();
        long diff = (now - lastShot) / 1000;

        if (diff >= delaySeconds) return 0;
        return delaySeconds - diff;
    }

    public void updateCooldown(UUID uuid) {
        cooldowns.put(uuid, System.currentTimeMillis());
    }
}