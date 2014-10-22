package net.uhcwork.BungeeGuard.Persistence;

import java.util.concurrent.Callable;

abstract public class VoidRunner implements Callable<Void> {
    public Void call() {
        run();
        return null;
    }

    protected abstract void run();
}
