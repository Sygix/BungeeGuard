package net.uhcwork.BungeeGuard.Persistence;

abstract public class VoidRunner extends PersistenceRunnable<Void> {
    public Void call() {
        super.call();
        run();
        return null;
    }

    protected abstract void run();
}
