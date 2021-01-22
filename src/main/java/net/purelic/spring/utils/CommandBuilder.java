package net.purelic.spring.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

import java.util.ArrayList;
import java.util.List;

public class CommandBuilder {

    private final String command;
    private final String description;
    private final List<BaseComponent[]> arguments;
    private final ChatColor color;

    public CommandBuilder(String command, String description, ChatColor color) {
        this.command = command;
        this.description = description;
        this.arguments = new ArrayList<>();
        this.color = color;
    }

    public CommandBuilder addArgument(String argument, String description) {
        return this.addArgument(argument, description, true);
    }

    public CommandBuilder addArgument(String argument, String description, boolean required) {
        ComponentBuilder builder = new ComponentBuilder(required ? "<" + argument + ">" : "[" + argument + "]");
        builder.color(required ? this.color : ChatColor.GRAY);
        // builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder((required ? "" : "(Optional) ") + description).create()));
        this.arguments.add(builder.create());
        return this;
    }

    public BaseComponent[] toComponent() {
        ComponentBuilder builder =
                new ComponentBuilder("\n")
                    .append(" • ").color(ChatColor.GRAY)
                    .append(this.command).color(this.color);

        for (BaseComponent[] argument : this.arguments) {
            builder.append(" ").append(argument);
        }

        builder.append(" - " + this.description).color(ChatColor.RESET);

        return builder.create();
    }

}
