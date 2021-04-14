package net.purelic.spring.server;

public enum ServerRegion {

    NYC("nyc3", "New York"),
    SFO("sfo3", "San Francisco"),
    LON("lon1", "London"),
    TOR("tor1", "Toronto"),
    FRA("fra1", "Frankfurt"),
    SGP("sgp1", "Singapore"),
    AMS("ams3", "Amsterdam"),
    BLR("blr1", "Bangalore"),
    ;

    private final String slug;
    private final String name;

    ServerRegion(String slug, String name) {
        this.slug = slug;
        this.name = name;
    }

    public String getSlug() {
        return this.slug;
    }

    public String getName() {
        return this.name;
    }

}
