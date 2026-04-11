package pl.batek.util;

public final class FormatUtil {
    private FormatUtil() {
    }

    public static String formatTime(int totalSeconds) {
        if (totalSeconds <= 0) {
            return "0sek";
        } else {
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;
            StringBuilder formattedTime = new StringBuilder();
            if (minutes > 0) {
                formattedTime.append(minutes).append("min");
            }

            if (seconds > 0) {
                if (minutes > 0) {
                    formattedTime.append(" ");
                }

                formattedTime.append(seconds).append("sek");
            }

            return formattedTime.toString();
        }
    }

    // Metoda do formatowania gotówki (1000 -> 1k, 1500000 -> 1.5m)
    public static String formatMoney(double amount) {
        if (amount >= 1_000_000_000) return formatValue(amount / 1_000_000_000.0) + "b";
        if (amount >= 1_000_000) return formatValue(amount / 1_000_000.0) + "m";
        if (amount >= 1_000) return formatValue(amount / 1_000.0) + "k";
        return formatValue(amount);
    }

    // Metoda pomocnicza usuwająca niepotrzebne zera po przecinku
    private static String formatValue(double value) {
        String str = String.format(java.util.Locale.US, "%.2f", value);
        if (str.endsWith(".00")) return str.substring(0, str.length() - 3);
        if (str.endsWith("0")) return str.substring(0, str.length() - 1);
        return str;
    }
}