package pl.batek.manager;

import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TpaManager {
    private final Map<UUID, Map<UUID, Long>> requests = new ConcurrentHashMap<>();
    private static final long EXPIRATION_TIME = 120_000L;

    public boolean sendRequest(Player sender, Player target) {
        Map<UUID, Long> targetRequests = requests.computeIfAbsent(target.getUniqueId(), k -> new HashMap<>());
        long now = System.currentTimeMillis();

        // Jeśli gracz wysłał już prośbę i jeszcze nie wygasła - blokujemy spam
        if (targetRequests.containsKey(sender.getUniqueId()) && targetRequests.get(sender.getUniqueId()) > now) {
            return false;
        }

        targetRequests.put(sender.getUniqueId(), now + EXPIRATION_TIME);
        return true;
    }

    public boolean hasValidRequest(Player target, Player sender) {
        Map<UUID, Long> targetRequests = requests.get(target.getUniqueId());
        if (targetRequests == null) return false;

        Long expiration = targetRequests.get(sender.getUniqueId());
        return expiration != null && expiration > System.currentTimeMillis();
    }

    public void removeRequest(Player target, Player sender) {
        Map<UUID, Long> targetRequests = requests.get(target.getUniqueId());
        if (targetRequests != null) {
            targetRequests.remove(sender.getUniqueId());
        }
    }

    public List<UUID> getValidRequests(Player target) {
        Map<UUID, Long> targetRequests = requests.get(target.getUniqueId());
        if (targetRequests == null) return Collections.emptyList();

        long now = System.currentTimeMillis();
        List<UUID> valid = new ArrayList<>();
        targetRequests.entrySet().removeIf(entry -> entry.getValue() <= now);

        valid.addAll(targetRequests.keySet());
        return valid;
    }

    public void clearRequests(Player target) {
        requests.remove(target.getUniqueId());
    }
}