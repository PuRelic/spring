package net.purelic.spring.server;

public enum ServerSize {

    LITE("Lite", "s-1vcpu-1gb"),
    BASIC("Basic", "s-1vcpu-2gb-amd"),
    PREMIUM("Premium", "s-2vcpu-2gb-amd"),
    PREMIUM_PLUS("Premium+", "s-2vcpu-4gb-amd"),
    ;

    private final String name;
    private final String slug;

    ServerSize(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

    public String getName() {
        return this.name;
    }

    public String getSlug() {
        return this.slug;
    }

}
