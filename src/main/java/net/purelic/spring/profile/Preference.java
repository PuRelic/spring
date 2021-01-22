package net.purelic.spring.profile;

public enum Preference {

    BETA_FEATURES,
    PLAYLIST,
    ;

    public static final String PATH = "preferences";
    private final String key;

    Preference() {
        this.key = this.name().toLowerCase();
    }

    public String getKey() {
        return this.key;
    }

    public String getFullPath() {
        return PATH + "." + this.key;
    }

}
