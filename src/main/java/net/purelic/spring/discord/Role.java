package net.purelic.spring.discord;

public class Role {

    public static final String ADMIN = "749529798291750974";
    public static final String MAP_DEVELOPER = "791772612216750121";
    public static final String HELPER = "791772096865894440";
    public static final String OG_PLAYER = "832332545252130836";
    public static final String VERIFIED = "832151395112058890";
    public static final String BETA_TESTER = "830694257392091147";
    public static final String LOOKING_TO_PLAY = "830673260952944660";
    public static final String ANNOUNCEMENTS = "835997424900112384";
    public static final String EVENTS = "839769166075789312";
    public static final String MOVIES = "835989590740697098";
    public static final String MUSIC = "835989682953256960";
    public static final String CREATIVE = "839769296120971306";
    public static final String MUTED = "818950420931805235";

    public static String any(String... roles) {
        return String.join("::", roles);
    }

    public static String staff() {
        return any(ADMIN, MAP_DEVELOPER, HELPER);
    }

}
