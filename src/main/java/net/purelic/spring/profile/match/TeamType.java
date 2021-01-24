package net.purelic.spring.profile.match;

public enum TeamType {

    SOLO("Solo"),
    TEAMS("Teams"),
    MULTI_TEAM("Multi-Team"),
    SQUADS("Squads"),
    ;

    private final String name;

    TeamType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
