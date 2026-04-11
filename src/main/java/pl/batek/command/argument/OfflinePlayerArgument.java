package pl.batek.command.argument;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.parser.Parser;
import dev.rollczi.litecommands.argument.suggester.Suggester;
import dev.rollczi.litecommands.input.raw.RawInput;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.range.Range; // Import potrzebny dla metody getRange
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OfflinePlayerArgument implements Parser<CommandSender, OfflinePlayer>, Suggester<CommandSender, OfflinePlayer> {

    @Override
    public ParseResult<OfflinePlayer> parse(Invocation<CommandSender> invocation, Argument<OfflinePlayer> argument, RawInput input) {
        // Używamy .next() aby pobrać wpisany przez gracza tekst (nick)
        String playerName = input.next();

        return ParseResult.success(Bukkit.getOfflinePlayer(playerName));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<OfflinePlayer> argument, SuggestionContext context) {
        // Zwraca listę graczy online pod klawiszem TAB
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(SuggestionResult.collector());
    }

    // TA METODA NAPRAWIA BŁĄD Z 'Rangeable'
    @Override
    public Range getRange(Argument<OfflinePlayer> argument) {
        // Mówimy frameworkowi, że nick to dokładnie JEDNO słowo
        return Range.ONE;
    }
}