package net.purelic.spring.profile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public enum Rank {

    ADMIN("Admin", true),
    DEVELOPER("Developer", true),
    MAP_DEVELOPER("Map Developer", true),
    MODERATOR("Moderator", true),
    HELPER("Helper", true),
    PREMIUM("Premium", false),
    CREATOR("Creator", false),
    ;

    public static final String PATH = "ranks";

    private final String name;
    private final boolean staff;

    Rank(String name, boolean staff) {
        this.name = name;
        this.staff = staff;
    }

    public String getName() {
        return this.name;
    }

    public boolean isStaff() {
        return this.staff;
    }

    public static Set<Rank> parseRanks(List<Object> rankValues) {
        Set<Rank> ranks = new HashSet<>();

        for (Rank rank : values()) {
            if (rankValues.contains(rank.name)) {
                ranks.add(rank);
            }
        }

        return ranks;
    }

    public static Set<Rank> getStaffRanks() {
        return Arrays.stream(values()).filter(Rank::isStaff).collect(Collectors.toSet());
    }

}
