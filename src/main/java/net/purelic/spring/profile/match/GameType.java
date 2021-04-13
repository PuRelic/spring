package net.purelic.spring.profile.match;

public enum GameType {

    BED_WARS("Bed Wars"),
    DEATHMATCH("Deathmatch"),
    HEAD_HUNTER("Head Hunter"),
    KING_OF_THE_HILL("King of the Hill"),
    CAPTURE_THE_FLAG("Capture the Flag"),
    SURVIVAL_GAMES("Survival Games"),
    ;

    private final String name;

    GameType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
