package net.purelic.spring.commands.parsers;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Command Argument for {@link Role}
 *
 * @param <C> Command sender type
 */
@SuppressWarnings("unused")
public final class RoleArgument<C> extends CommandArgument<C, Role> {

    private final Set<ParserMode> modes;

    private RoleArgument(
        final boolean required, final @NotNull String name,
        final @NotNull Set<ParserMode> modes
    ) {
        super(required, name, new MessageParser<>(modes), Role.class);
        this.modes = modes;
    }

    /**
     * Create a new builder
     *
     * @param name Name of the component
     * @param <C>  Command sender type
     * @return Created builder
     */
    public static <C> Builder<C> newBuilder(final @NotNull String name) {
        return new Builder<>(name);
    }

    /**
     * Create a new required command component
     *
     * @param name Component name
     * @param <C>  Command sender type
     * @return Created component
     */
    public static <C> CommandArgument<C, Role> of(final @NotNull String name) {
        return RoleArgument.<C>newBuilder(name).asRequired().build();
    }

    /**
     * Create a new optional command component
     *
     * @param name Component name
     * @param <C>  Command sender type
     * @return Created component
     */
    public static <C> CommandArgument<C, Role> optional(final @NotNull String name) {
        return RoleArgument.<C>newBuilder(name).asOptional().build();
    }

    /**
     * Get the modes enabled on the parser
     *
     * @return List of Modes
     */
    public @NotNull
    Set<ParserMode> getModes() {
        return modes;
    }

    public enum ParserMode {
        MENTION,
        ID,
        NAME
    }

    public static final class Builder<C> extends CommandArgument.Builder<C, Role> {

        private Set<ParserMode> modes = new HashSet<>(Arrays.asList(ParserMode.values()));

        private Builder(final @NotNull String name) {
            super(Role.class, name);
        }

        /**
         * Set the modes for the parsers to use
         *
         * @param modes List of Modes
         * @return Builder instance
         */
        public @NotNull Builder<C> withParsers(final @NotNull Set<ParserMode> modes) {
            this.modes = modes;
            return this;
        }

        /**
         * Builder a new example component
         *
         * @return Constructed component
         */
        @Override
        public @NotNull RoleArgument<C> build() {
            return new RoleArgument<>(this.isRequired(), this.getName(), this.modes);
        }

    }

    public static final class MessageParser<C> implements ArgumentParser<C, Role> {

        private final Set<ParserMode> modes;

        /**
         * Construct a new argument parser for {@link Role}
         *
         * @param modes List of parsing modes to use when parsing
         * @throws java.lang.IllegalStateException If no parsing modes were provided
         */
        public MessageParser(final @NotNull Set<ParserMode> modes) {
            if (modes.isEmpty()) {
                throw new IllegalArgumentException("At least one parsing mode is required");
            }

            this.modes = modes;
        }

        @Override
        public @NotNull
        ArgumentParseResult<Role> parse(
            final @NotNull CommandContext<C> commandContext,
            final @NotNull Queue<String> inputQueue
        ) {
            final String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(
                    MessageParser.class,
                    commandContext
                ));
            }

            if (!commandContext.contains("MessageReceivedEvent")) {
                return ArgumentParseResult.failure(new IllegalStateException(
                    "MessageReceivedEvent was not in the command context."
                ));
            }

            final MessageReceivedEvent event = commandContext.get("MessageReceivedEvent");
            Exception exception = null;

            if (!event.isFromGuild()) {
                return ArgumentParseResult.failure(new IllegalArgumentException("Role arguments can only be parsed in guilds"));
            }

            if (this.modes.contains(ParserMode.MENTION)) {
                if (input.startsWith("<#") && input.endsWith(">")) {
                    final String id = input.substring(2, input.length() - 1);

                    try {
                        final ArgumentParseResult<Role> role = this.roleFromId(event, input, id);
                        inputQueue.remove();
                        return role;
                    } catch (final RoleNotFoundException | NumberFormatException e) {
                        exception = e;
                    }
                } else {
                    exception = new IllegalArgumentException(
                        String.format("Input '%s' is not a role mention.", input)
                    );
                }
            }

            if (this.modes.contains(ParserMode.ID)) {
                try {
                    final ArgumentParseResult<Role> result = this.roleFromId(event, input, input);
                    inputQueue.remove();
                    return result;
                } catch (final RoleNotFoundException | NumberFormatException e) {
                    exception = e;
                }
            }

            if (this.modes.contains(ParserMode.NAME)) {
                final List<Role> roles = event.getGuild().getRolesByName(input, true);

                if (roles.size() == 0) {
                    exception = new RoleNotFoundException(input);
                } else if (roles.size() > 1) {
                    exception = new TooManyRolesFoundParseException(input);
                } else {
                    inputQueue.remove();
                    return ArgumentParseResult.success(roles.get(0));
                }
            }

            assert exception != null;
            return ArgumentParseResult.failure(exception);
        }

        @Override
        public boolean isContextFree() {
            return true;
        }

        private @NotNull ArgumentParseResult<Role> roleFromId(
            final @NotNull MessageReceivedEvent event,
            final @NotNull String input,
            final @NotNull String id
        )
            throws RoleNotFoundException, NumberFormatException {
            final Role role = event.getGuild().getRoleById(id);

            if (role == null) {
                throw new RoleNotFoundException(input);
            }

            return ArgumentParseResult.success(role);
        }

    }

    public static class RoleParseException extends IllegalArgumentException {

        private static final long serialVersionUID = 2724288304060572202L;
        private final String input;

        /**
         * Construct a new role parse exception
         *
         * @param input String input
         */
        public RoleParseException(final @NotNull String input) {
            this.input = input;
        }

        /**
         * Get the users input
         *
         * @return users input
         */
        public final @NotNull String getInput() {
            return input;
        }

    }

    public static final class TooManyRolesFoundParseException extends RoleParseException {

        private static final long serialVersionUID = -507783063742841507L;

        /**
         * Construct a new role parse exception
         *
         * @param input String input
         */
        public TooManyRolesFoundParseException(final @NotNull String input) {
            super(input);
        }

        @Override
        public @NotNull String getMessage() {
            return String.format("Too many roles found for '%s'.", getInput());
        }

    }

    public static final class RoleNotFoundException extends RoleParseException {

        private static final long serialVersionUID = -8299458048947528494L;

        /**
         * Construct a new role parse exception
         *
         * @param input String input
         */
        public RoleNotFoundException(final @NotNull String input) {
            super(input);
        }

        @Override
        public @NotNull String getMessage() {
            return String.format("Role not found for '%s'.", getInput());
        }

    }

}