package net.purelic.spring.commands.parsers;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.bungee.BungeeCaptionKeys;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import io.leangen.geantyref.TypeToken;
import net.purelic.spring.managers.ServerManager;
import net.purelic.spring.server.GameServer;
import net.purelic.spring.utils.ServerUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;

public final class GameServerArgument<C> extends CommandArgument<C, GameServer> {

    private GameServerArgument(
        final boolean required,
        final @NonNull String name,
        final @Nullable BiFunction<CommandContext<C>, String, List<String>> suggestionsProvider,
        final @NonNull ArgumentDescription defaultDescription,
        final @NonNull Collection<@NonNull BiFunction<@NonNull CommandContext<C>, @NonNull Queue<@NonNull String>,
                    @NonNull ArgumentParseResult<Boolean>>> argumentPreprocessors
    ) {
        super(
            required,
            name,
            new GameServerParser<>(),
            "",
            TypeToken.get(GameServer.class),
            suggestionsProvider,
            defaultDescription,
            argumentPreprocessors
        );
    }

    public static <C> CommandArgument.@NonNull Builder<C, GameServer> newBuilder(
        final @NonNull String name
    ) {
        return new Builder<C>(
            name
        ).withParser(
            new GameServerParser<>()
        );
    }

    public static <C> @NonNull CommandArgument<C, GameServer> of(
        final @NonNull String name
    ) {
        return GameServerArgument.<C>newBuilder(name).asRequired().build();
    }

    public static <C> @NonNull CommandArgument<C, GameServer> optional(
        final @NonNull String name
    ) {
        return GameServerArgument.<C>newBuilder(name).asOptional().build();
    }

    public static final class Builder<C> extends CommandArgument.Builder<C, GameServer> {

        private Builder(
            final @NonNull String name
        ) {
            super(TypeToken.get(GameServer.class), name);
        }

        @Override
        public @NonNull CommandArgument<@NonNull C, @NonNull GameServer> build() {
            return new GameServerArgument<>(
                this.isRequired(),
                this.getName(),
                this.getSuggestionsProvider(),
                this.getDefaultDescription(),
                new LinkedList<>()
            );
        }

    }

    public static final class GameServerParser<C> implements ArgumentParser<C, GameServer> {

        @Override
        public @NonNull ArgumentParseResult<@NonNull GameServer> parse(
            final @NonNull CommandContext<@NonNull C> commandContext,
            final @NonNull Queue<@NonNull String> inputQueue
        ) {
            final String input = inputQueue.peek();

            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(
                    GameServerParser.class,
                    commandContext
                ));
            }

            inputQueue.remove();

            GameServer server = ServerUtils.getGameServerByName(input, false);

            if (server == null) {
                return ArgumentParseResult.failure(new GameServerParseException(input, commandContext));
            }

            return ArgumentParseResult.success(server);
        }

        @Override
        public @NonNull List<@NonNull String> suggestions(
            final @NonNull CommandContext<C> commandContext,
            final @NonNull String input
        ) {
            return new ArrayList<>(ServerManager.getGameServers().keySet());
        }

    }

    public static final class GameServerParseException extends ParserException {

        private static final long serialVersionUID = -3825941611365494659L;

        private GameServerParseException(
            final @NonNull String input,
            final @NonNull CommandContext<?> context
        ) {
            super(
                GameServerParser.class,
                context,
                BungeeCaptionKeys.ARGUMENT_PARSE_FAILURE_SERVER,
                CaptionVariable.of("input", input)
            );
        }

    }

}