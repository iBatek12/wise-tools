package pl.batek.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MessageManager {
    private final Set<UUID> globalIgnores = new HashSet<>();
    private final Map<UUID, Set<UUID>> ignoredPlayers = new HashMap<>();
    private final Map<UUID, UUID> lastMessaged = new HashMap<>();

    public void setLastConversation(UUID player1, UUID player2) {
        lastMessaged.put(player1, player2);
        lastMessaged.put(player2, player1);
    }

    public UUID getLastMessaged(UUID player) {
        return lastMessaged.get(player);
    }

    public boolean toggleGlobalIgnore(UUID uuid) {
        if (globalIgnores.contains(uuid)) {
            globalIgnores.remove(uuid);
            return false;
        } else {
            globalIgnores.add(uuid);
            return true;
        }
    }

    public boolean toggleIgnore(UUID ignorer, UUID ignored) {
        ignoredPlayers.putIfAbsent(ignorer, new HashSet<>());
        Set<UUID> ignores = ignoredPlayers.get(ignorer);

        if (ignores.contains(ignored)) {
            ignores.remove(ignored);
            return false;
        } else {
            ignores.add(ignored);
            return true;
        }
    }

    public boolean canReceiveMessage(UUID sender, UUID receiver) {
        if (globalIgnores.contains(receiver)) {
            return false;
        }

        Set<UUID> receiverIgnores = ignoredPlayers.get(receiver);
        if (receiverIgnores != null && receiverIgnores.contains(sender)) {
            return false;
        }

        return true;
    }
}