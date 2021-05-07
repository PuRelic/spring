package net.purelic.spring.commands.parsers;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.captions.StandardCaptionKeys;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.managers.PlaylistManager;
import net.purelic.spring.server.Playlist;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

@SuppressWarnings("unused")
public final class PlaylistArgument<C> extends CommandArgument<C, Playlist> {

    private PlaylistArgument(
        final boolean required,
        final @NotNull String name,
        final @NotNull String defaultValue,
        final @Nullable BiFunction<@NotNull CommandContext<C>, @NotNull String,
            @NotNull List<@NotNull String>> suggestionsProvider,
        final @NotNull ArgumentDescription defaultDescription
    ) {
        super(required, name, new PlaylistParser<>(), defaultValue, Playlist.class, suggestionsProvider, defaultDescription);
    }

    public static <C> @NotNull Builder<C> newBuilder(final @NotNull String name) {
        return new Builder<>(name);
    }

    public static <C> @NotNull CommandArgument<C, Playlist> of(final @NotNull String name) {
        return PlaylistArgument.<C>newBuilder(name).asRequired().build();
    }

    public static <C> @NotNull CommandArgument<C, Playlist> optional(final @NotNull String name) {
        return PlaylistArgument.<C>newBuilder(name).asOptional().build();
    }

    public static <C> @NotNull CommandArgument<C, Playlist> optional(
        final @NotNull String name,
        final @NotNull String defaultPlaylist
    ) {
        return PlaylistArgument.<C>newBuilder(name).asOptionalWithDefault(defaultPlaylist).build();
    }

    public static final class Builder<C> extends CommandArgument.Builder<C, Playlist> {

        private Builder(final @NotNull String name) {
            super(Playlist.class, name);
        }

        @Override
        public @NotNull PlaylistArgument<C> build() {
            return new PlaylistArgument<>(
                this.isRequired(),
                this.getName(),
                this.getDefaultValue(),
                this.getSuggestionsProvider(),
                this.getDefaultDescription()
            );
        }

    }

    public static final class PlaylistParser<C> implements ArgumentParser<C, Playlist> {

        @Override
        public @NotNull ArgumentParseResult<Playlist> parse(
            final @NotNull CommandContext<C> commandContext,
            final @NotNull Queue<@NotNull String> inputQueue
        ) {
            final String input = inputQueue.peek();

            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(
                    PlaylistParser.class,
                    commandContext
                ));
            }

            inputQueue.remove();

            Playlist playlist = PlaylistManager.getPlaylistById(input);

            if (playlist == null) {
                return ArgumentParseResult.failure(new PlaylistParseException(input, commandContext));
            }

            return ArgumentParseResult.success(playlist);
        }

        @Override
        public @NotNull List<@NotNull String> suggestions(
            final @NotNull CommandContext<C> commandContext,
            final @NotNull String input
        ) {
            if (commandContext.getSender() instanceof ProxiedPlayer) {
                List<String> output = new ArrayList<>();

                for (Playlist playlist : PlaylistManager.getPlaylists()) {
                    output.add(playlist.getId());
                }

                return output;
            } else {
                return new ArrayList<>();
            }
        }

    }

    public static final class PlaylistParseException extends ParserException {

        private final String input;

        public PlaylistParseException(
            final @NotNull String input,
            final @NotNull CommandContext<?> context
        ) {
            super(
                PlaylistParser.class,
                context,
                StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_STRING,
                CaptionVariable.of("input", input)
            );
            this.input = input;
        }

        public @NotNull String getInput() {
            return input;
        }

    }

}