package net.purelic.spring.utils;

import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.purelic.spring.Spring;

import java.util.concurrent.TimeUnit;

public class TaskUtils {

    private static TaskScheduler getScheduler() {
        return Spring.getPlugin().getProxy().getScheduler();
    }

    public static ScheduledTask scheduleTask(Runnable runnable, long delay) {
        return getScheduler().schedule(Spring.getPlugin(), runnable, delay, TimeUnit.SECONDS);
    }

    public static ScheduledTask runAsync(Runnable runnable) {
        return getScheduler().runAsync(Spring.getPlugin(), runnable);
    }

    public static ScheduledTask runTimer(Runnable runnable, long interval) {
        return runTimer(runnable, 0L, interval);
    }

    public static ScheduledTask runTimer(Runnable runnable, long delay, long interval) {
        return getScheduler().schedule(Spring.getPlugin(), runnable, delay, interval, TimeUnit.SECONDS);
    }

}
