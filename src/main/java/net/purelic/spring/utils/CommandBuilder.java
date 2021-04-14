package net.purelic.spring.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

import java.util.ArrayList;
import java.util.List;

public class CommandBuilder {

    private final String command;
    private final String description;
    private final List<BaseComponent[]> arguments;
    private final ChatColor color;

    public CommandBuilder(String command, String description) {
        this(command, description, ChatColor.AQUA);
    }

    public CommandBuilder(String command, String description, ChatColor color) {
        this.command = command;
        this.description = description;
        this.arguments = new ArrayList<>();
        this.color = color;
    }

    public CommandBuilder addArgument(String argument, String description) {
        return this.addArgument(argument, description, true);
    }

    @SuppressWarnings("deprecation")
    public CommandBuilder addArgument(String argument, String description, boolean required) {
        this.arguments.add(
            new ComponentBuilder(required ? "<" + argument + ">" : "[" + argument + "]")
                .color(required ? this.color : ChatColor.GRAY)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder((required ? "" : "(Optional) ") + description).create()))
                .append("").event(this.resetHover())
                .create()
        );
        return this;
    }

    @SuppressWarnings("deprecation")
    public BaseComponent[] toComponent() {
        ComponentBuilder builder =
            new ComponentBuilder("\n")
                .append(" • ").color(ChatColor.GRAY)
                .append(this.command).color(this.color)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, this.command + " "))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to Suggest").color(this.color).create()))
                .append("").event(this.resetHover());

        for (BaseComponent[] argument : this.arguments) {
            builder.append(" ").append(argument);
        }

        builder.append(" - " + this.description).color(ChatColor.RESET);

        return builder.create();
    }

    @SuppressWarnings("deprecation")
    private HoverEvent resetHover() {
        // required to reset the hover for the text following it
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{});
    }

}
