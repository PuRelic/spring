package net.purelic.spring.punishment;

import de.exceptionflug.protocolize.items.ItemType;

public enum PunishmentType {

    WARN("Warning", "warned", ItemType.PAPER),
    KICK("Kick", "kicked", ItemType.IRON_BOOTS),
    TEMP_BAN("Temporary Ban", "temporarily banned", ItemType.IRON_AXE),
    PERMA_BAN("Permanent Ban", "permanently banned", ItemType.DIAMOND_AXE);

    private final String name;
    private final String pastTense;
    private final ItemType material;

    PunishmentType(String name, String pastTense, ItemType material) {
        this.name = name;
        this.pastTense = pastTense;
        this.material = material;
    }

    public String getName() {
        return this.name;
    }

    public String getPastTense() {
        return this.pastTense;
    }

    public ItemType getMaterial() {
        return this.material;
    }

    public static PunishmentType fromString(String string) {
        for (PunishmentType punishmentType : values()) {
            if (punishmentType.getName().equalsIgnoreCase(string)) {
                return punishmentType;
            }
        }

        return null;
    }

}
