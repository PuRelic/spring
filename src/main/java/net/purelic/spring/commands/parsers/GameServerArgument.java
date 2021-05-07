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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;

public final class GameServerArgument<C> extends CommandArgument<C, GameServer> {

    private GameServerArgument(
        final boolean required,
        final @NotNull String name,
        final @Nullable BiFunction<CommandContext<C>, String, List<String>> suggestionsProvider,
        final @NotNull ArgumentDescription defaultDescription,
        final @NotNull Collection<@NotNull BiFunction<@NotNull CommandContext<C>, @NotNull Queue<@NotNull String>,
                    @NotNull ArgumentParseResult<Boolean>>> argumentPreprocessors
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

    public static <C> CommandArgument.@NotNull Builder<C, GameServer> newBuilder(
        final @NotNull String name
    ) {
        return new Builder<C>(
            name
        ).withParser(
            new GameServerParser<>()
        );
    }

    public static <C> @NotNull CommandArgument<C, GameServer> of(
        final @NotNull String name
    ) {
        return GameServerArgument.<C>newBuilder(name).asRequired().build();
    }

    public static <C> @NotNull CommandArgument<C, GameServer> optional(
        final @NotNull String name
    ) {
        return GameServerArgument.<C>newBuilder(name).asOptional().build();
    }

    public static final class Builder<C> extends CommandArgument.Builder<C, GameServer> {

        private Builder(
            final @NotNull String name
        ) {
            super(TypeToken.get(GameServer.class), name);
        }

        @Override
        public @NotNull CommandArgument<@NotNull C, @NotNull GameServer> build() {
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
        public @NotNull ArgumentParseResult<@NotNull GameServer> parse(
            final @NotNull CommandContext<@NotNull C> commandContext,
            final @NotNull Queue<@NotNull String> inputQueue
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
        public @NotNull List<@NotNull String> suggestions(
            final @NotNull CommandContext<C> commandContext,
            final @NotNull String input
        ) {
            return new ArrayList<>(ServerManager.getGameServers().keySet());
        }

    }

    public static final class GameServerParseException extends ParserException {

        private static final long serialVersionUID = -3825941611365494659L;

        private GameServerParseException(
            final @NotNull String input,
            final @NotNull CommandContext<?> context
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