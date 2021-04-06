package net.purelic.spring.utils;

import de.exceptionflug.protocolize.items.ItemFlag;
import de.exceptionflug.protocolize.items.ItemStack;
import net.querz.nbt.tag.CompoundTag;

public enum ItemAction {

    CREATE,
    JOIN,
    STOP,
    BETA,
    VIEW_PLAYLISTS,
    SELECT_PLAYLIST,
    SELECT_PUBLIC,
    BROWSE_PUBLIC,
    SELECT_LEAGUE,
    BROWSE_LEAGUE,
    QUEUE,
    BROWSE_PRIVATE,
    LEADERBOARD,
    STATS,
    MATCH,
    PRIVATE_SERVER,
    NOTHING,
    ;

    public void apply(ItemStack item) {
        this.apply(item, "");
    }

    public void apply(ItemStack item, String value) {
        CompoundTag itemTag = (CompoundTag) item.getNBTTag();
        itemTag.putBoolean("spring", true);
        itemTag.putString("action", this.name());
        itemTag.putString("value", value);
        item.setFlag(ItemFlag.HIDE_ATTRIBUTES, true);
    }

}
