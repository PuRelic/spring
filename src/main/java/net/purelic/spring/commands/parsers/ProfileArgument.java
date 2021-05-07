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
import net.purelic.spring.managers.ProfileManager;
import net.purelic.spring.profile.Profile;
import net.purelic.spring.utils.Fetcher;
import net.purelic.spring.utils.NickUtils;
import net.purelic.spring.utils.ServerUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.function.BiFunction;

@SuppressWarnings("unused")
public final class ProfileArgument<C> extends CommandArgument<C, Profile> {

    private ProfileArgument(
        final boolean required,
        final @NotNull String name,
        final @NotNull String defaultValue,
        final @Nullable BiFunction<@NotNull CommandContext<C>, @NotNull String,
            @NotNull List<@NotNull String>> suggestionsProvider,
        final @NotNull ArgumentDescription defaultDescription
    ) {
        super(required, name, new ProfileParser<>(), defaultValue, Profile.class, suggestionsProvider, defaultDescription);
    }

    public static <C> @NotNull Builder<C> newBuilder(final @NotNull String name) {
        return new Builder<>(name);
    }

    public static <C> @NotNull CommandArgument<C, Profile> of(final @NotNull String name) {
        return ProfileArgument.<C>newBuilder(name).asRequired().build();
    }

    public static <C> @NotNull CommandArgument<C, Profile> optional(final @NotNull String name) {
        return ProfileArgument.<C>newBuilder(name).asOptional().build();
    }

    public static <C> @NotNull CommandArgument<C, Profile> optional(
        final @NotNull String name,
        final @NotNull String defaultProfile
    ) {
        return ProfileArgument.<C>newBuilder(name).asOptionalWithDefault(defaultProfile).build();
    }

    public static final class Builder<C> extends CommandArgument.Builder<C, Profile> {

        private Builder(final @NotNull String name) {
            super(Profile.class, name);
        }

        @Override
        public @NotNull ProfileArgument<C> build() {
            return new ProfileArgument<>(
                this.isRequired(),
                this.getName(),
                this.getDefaultValue(),
                this.getSuggestionsProvider(),
                this.getDefaultDescription()
            );
        }

    }

    public static final class ProfileParser<C> implements ArgumentParser<C, Profile> {

        @Override
        public @NotNull ArgumentParseResult<Profile> parse(
            final @NotNull CommandContext<C> commandContext,
            final @NotNull Queue<@NotNull String> inputQueue
        ) {
            final String input = inputQueue.peek();

            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(
                    ProfileParser.class,
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

            if (player != null) {
                return ArgumentParseResult.success(ProfileManager.getProfile(player));
            }

            UUID uuid = Fetcher.getUUIDOf(input);

            if (uuid == null) {
                return ArgumentParseResult.failure(new ProfileParseException(input, commandContext));
            }

            return ArgumentParseResult.success(ProfileManager.getOfflineProfile(uuid));
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

    public static final class ProfileParseException extends ParserException {

        private final String input;

        public ProfileParseException(
            final @NotNull String input,
            final @NotNull CommandContext<?> context
        ) {
            super(
                ProfileParser.class,
                context,
                BungeeCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER,
                CaptionVariable.of("input", input)
            );
            this.input = input;
        }

        public @NotNull String getInput() {
            return input;
        }

    }

}