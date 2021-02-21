package net.purelic.spring.utils;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public enum Protocol {

    MINECRAFT_1_7_2(4, "1.7"),
    MINECRAFT_1_7_4(MINECRAFT_1_7_2),
    MINECRAFT_1_7_5(MINECRAFT_1_7_4),
    MINECRAFT_1_7_6(5, "1.7"),
    MINECRAFT_1_7_7(MINECRAFT_1_7_6),
    MINECRAFT_1_7_8(MINECRAFT_1_7_7),
    MINECRAFT_1_7_9(MINECRAFT_1_7_8),
    MINECRAFT_1_7_10(MINECRAFT_1_7_9),
    MINECRAFT_1_8(47, "1.8"),
    MINECRAFT_1_9(107, "1.9"),
    MINECRAFT_1_9_1(108, "1.9.1"),
    MINECRAFT_1_9_2(109, "1.9.2"),
    MINECRAFT_1_9_3(110, "1.9"),
    MINECRAFT_1_9_4(MINECRAFT_1_9_3),
    MINECRAFT_1_10(210, "1.10"),
    MINECRAFT_1_11(315, "1.11"),
    MINECRAFT_1_11_1(316, "1.11"),
    MINECRAFT_1_11_2(MINECRAFT_1_11_1),
    MINECRAFT_1_12(335, "1.12"),
    MINECRAFT_1_12_1(338, "1.12.1"),
    MINECRAFT_1_12_2(340, "1.12.2"),
    MINECRAFT_1_13(393, "1.13"),
    MINECRAFT_1_13_1(401, "1.13.1"),
    MINECRAFT_1_13_2(404, "1.13.2"),
    MINECRAFT_1_14(477, "1.14"),
    MINECRAFT_1_14_1(480, "1.14.1"),
    MINECRAFT_1_14_2(485, "1.14.2"),
    MINECRAFT_1_14_3(490, "1.14.3"),
    MINECRAFT_1_14_4(498, "1.14.4"),
    MINECRAFT_1_15(573, "1.15"),
    MINECRAFT_1_15_1(575, "1.15.1"),
    MINECRAFT_1_15_2(578, "1.15.2"),
    MINECRAFT_1_16(735, "1.16"),
    MINECRAFT_1_16_1(736, "1.16.1"),
    MINECRAFT_1_16_2(751, "1.16.2"),
    MINECRAFT_1_16_3(753, "1.16.3"),
    MINECRAFT_1_16_4(754, "1.16.4"),
    ;

    public final static Protocol MINECRAFT_LATEST = MINECRAFT_1_16_4;

    private final int protocol;
    private final String fullLabel;
    private final String label;

    Protocol(Protocol protocol) {
        this(protocol.value(), protocol.getFullLabel());
    }

    Protocol(int protocol, String fullLabel) {
        this.protocol = protocol;
        this.fullLabel = fullLabel;
        String[] labelParts = this.name().split("_");
        this.label = labelParts[1] + "." + labelParts[2];
    }

    public int value() {
        return this.protocol;
    }

    public String getFullLabel() {
        return this.fullLabel;
    }

    public String getLabel() {
        return this.label;
    }

    public static Protocol getProtocol(int version) {
        Protocol protocol = Protocol.MINECRAFT_LATEST;

        for (Protocol p : Protocol.values()) {
            if (p.value() <= version) protocol = p;
            else break;
        }

        return protocol;
    }

    public static Protocol getProtocol(ProxiedPlayer player) {
        return Protocol.getProtocol(player.getPendingConnection().getVersion());
    }

    public static boolean isLegacy(ProxiedPlayer player) {
        return getProtocol(player).value() < MINECRAFT_1_8.value();
    }

}
