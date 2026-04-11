package pl.batek.command.argument;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.parser.Parser;
import dev.rollczi.litecommands.argument.suggester.Suggester;
import dev.rollczi.litecommands.input.raw.RawInput;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.range.Range;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;
import pl.batek.config.Configuration;

public class KeyTypeArgument implements Parser<CommandSender, KeyTypeWrapper>, Suggester<CommandSender, KeyTypeWrapper> {

    private final Configuration config;

    public KeyTypeArgument(Configuration config) {
        this.config = config;
    }

    @Override
    public ParseResult<KeyTypeWrapper> parse(Invocation<CommandSender> invocation, Argument<KeyTypeWrapper> argument, RawInput input) {
        // Zwracamy to co gracz wpisał, zapakowane w nasz Wrapper
        return ParseResult.success(new KeyTypeWrapper(input.next()));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<KeyTypeWrapper> argument, SuggestionContext context) {
        // Zwracamy listę kluczy z configu pod klawiszem TAB!
        return config.keyAllKeys.keySet().stream().collect(SuggestionResult.collector());
    }

    @Override
    public Range getRange(Argument<KeyTypeWrapper> argument) {
        // Jeden argument (jedno słowo)
        return Range.ONE;
    }
}