package net.purelic.spring.discord;

public class Role {

    public static final String ADMIN = "749529798291750974";
    public static final String MAP_DEVELOPER = "791772612216750121";
    public static final String HELPER = "791772096865894440";
    public static final String MUTED = "818950420931805235";

    public static String any(String... roles) {
        return String.join("::", roles);
    }

    public static String staff() {
        return any(ADMIN, MAP_DEVELOPER, HELPER);
    }

}
