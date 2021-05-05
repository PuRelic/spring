package net.purelic.spring.punishment;

import java.util.Calendar;

public enum BanUnit {

    HOUR(Calendar.HOUR),
    DAY(Calendar.DATE),
    MONTH(Calendar.MONTH),
    YEAR(Calendar.YEAR);

    private final int unit;

    BanUnit(int unit) {
        this.unit = unit;
    }

    public int getUnit() {
        return unit;
    }

}
