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
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.Spring;
import net.purelic.spring.utils.NickUtils;
import net.purelic.spring.utils.ServerUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

/**
 * Argument that parses into a {@link ProxiedPlayer}
 *
 * @param <C> Command sender type
 */
@SuppressWarnings("unused")
public final class PlayerArgument<C> extends CommandArgument<C, ProxiedPlayer> {

    private PlayerArgument(
        final boolean required,
        final @NotNull String name,
        final @NotNull String defaultValue,
        final @Nullable BiFunction<@NotNull CommandContext<C>, @NotNull String,
            @NotNull List<@NotNull String>> suggestionsProvider,
        final @NotNull ArgumentDescription defaultDescription
    ) {
        super(required, name, new PlayerParser<>(), defaultValue, ProxiedPlayer.class, suggestionsProvider, defaultDescription);
    }

    /**
     * Create a new builder
     *
     * @param name Name of the component
     * @param <C>  Command sender type
     * @return Created builder
     */
    public static <C> @NotNull Builder<C> newBuilder(final @NotNull String name) {
        return new Builder<>(name);
    }

    /**
     * Create a new required command component
     *
     * @param name Component name
     * @param <C>  Command sender type
     * @return Created component
     */
    public static <C> @NotNull CommandArgument<C, ProxiedPlayer> of(final @NotNull String name) {
        return PlayerArgument.<C>newBuilder(name).asRequired().build();
    }

    /**
     * Create a new optional command component
     *
     * @param name Component name
     * @param <C>  Command sender type
     * @return Created component
     */
    public static <C> @NotNull CommandArgument<C, ProxiedPlayer> optional(final @NotNull String name) {
        return PlayerArgument.<C>newBuilder(name).asOptional().build();
    }

    /**
     * Create a new required command component with a default value
     *
     * @param name          Component name
     * @param defaultPlayer Default player
     * @param <C>           Command sender type
     * @return Created component
     */
    public static <C> @NotNull CommandArgument<C, ProxiedPlayer> optional(
        final @NotNull String name,
        final @NotNull String defaultPlayer
    ) {
        return PlayerArgument.<C>newBuilder(name).asOptionalWithDefault(defaultPlayer).build();
    }

    public static final class Builder<C> extends CommandArgument.Builder<C, ProxiedPlayer> {

        private Builder(final @NotNull String name) {
            super(ProxiedPlayer.class, name);
        }

        /**
         * Builder a new boolean component
         *
         * @return Constructed component
         */
        @Override
        public @NotNull PlayerArgument<C> build() {
            return new PlayerArgument<>(
                this.isRequired(),
                this.getName(),
                this.getDefaultValue(),
                this.getSuggestionsProvider(),
                this.getDefaultDescription()
            );
        }

    }

    public static final class PlayerParser<C> implements ArgumentParser<C, ProxiedPlayer> {

        @Override
        public @NotNull ArgumentParseResult<ProxiedPlayer> parse(
            final @NotNull CommandContext<C> commandContext,
            final @NotNull Queue<@NotNull String> inputQueue
        ) {
            final String input = inputQueue.peek();

            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(
                    PlayerParser.class,
                    commandContext
                ));
            }

            inputQueue.remove();

            ProxiedPlayer player = Spring.getPlayer(input);

            if (player != null && NickUtils.isNicked(player)) {
                player = null;
            }

            if (player == null) {
                player = NickUtils.getNickedPlayer(input);
            }

            if (player == null) {
                return ArgumentParseResult.failure(new PlayerParseException(input, commandContext));
            }

            return ArgumentParseResult.success(player);
        }

        @Override
        public @NotNull List<@NotNull String> suggestions(
            final @NotNull CommandContext<C> commandContext,
            final @NotNull String input
        ) {
            if (commandContext.getSender() instanceof ProxiedPlayer) {
                ProxiedPlayer sender = (ProxiedPlayer) commandContext.getSender();
                List<String> output = new ArrayList<>();

                // Only suggest local players
                for (ProxiedPlayer player : ServerUtils.getLocalPlayers(sender)) {
                    output.add(NickUtils.getNick(player));
                }

                return output;
            } else {
                return new ArrayList<>();
            }
        }

    }


    /**
     * Player parse exception
     */
    public static final class PlayerParseException extends ParserException {

        private final String input;

        /**
         * Construct a new Player parse exception
         *
         * @param input   String input
         * @param context Command context
         */
        public PlayerParseException(
            final @NotNull String input,
            final @NotNull CommandContext<?> context
        ) {
            super(
                PlayerParser.class,
                context,
                BungeeCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER,
                CaptionVariable.of("input", input)
            );
            this.input = input;
        }

        /**
         * Get the supplied input
         *
         * @return String value
         */
        public @NotNull String getInput() {
            return input;
        }

    }

}