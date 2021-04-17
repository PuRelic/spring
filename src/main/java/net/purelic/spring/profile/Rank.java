package net.purelic.spring.profile;

import net.purelic.spring.discord.Role;

import java.util.*;
import java.util.stream.Collectors;

public enum Rank {

    ADMIN("Admin", true, Role.ADMIN),
    DEVELOPER("Developer", true, null),
    MAP_DEVELOPER("Map Developer", true, Role.MAP_DEVELOPER),
    MODERATOR("Moderator", true, null),
    HELPER("Helper", true, Role.HELPER),
    CREATOR("Creator", false, null),
    PREMIUM("Premium", false, null),
    OG_PLAYER("OG Player", false, Role.OG_PLAYER),
    ;

    public static final String PATH = "ranks";

    private final String name;
    private final boolean staff;
    private final String discordRole;

    Rank(String name, boolean staff, String discordRole) {
        this.name = name;
        this.staff = staff;
        this.discordRole = discordRole;
    }

    public String getName() {
        return this.name;
    }

    public boolean isStaff() {
        return this.staff;
    }

    public boolean hasDiscordRole() {
        return this.discordRole != null;
    }

    public String getDiscordRole() {
        return this.discordRole;
    }

    public static List<Rank> parseRanks(List<Object> rankValues) {
        List<Rank> ranks = new ArrayList<>();

        for (Rank rank : values()) {
            if (rankValues.contains(rank.getName())) {
                ranks.add(rank);
            }
        }

        return ranks;
    }

    public static Set<Rank> getStaffRanks() {
        return Arrays.stream(values()).filter(Rank::isStaff).collect(Collectors.toSet());
    }

}
