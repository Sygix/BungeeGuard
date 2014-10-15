package net.uhcwork.BungeeGuard.Persistence;

import java.util.concurrent.Callable;

/**
 * Part of net.uhcwork.BungeeGuard.Persistence (BungeeGuard)
 * Date: 15/10/2014
 * Time: 18:21
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class PersistenceRunnable<T> implements Callable<T> {
    public T call() {
        /*
         *
		 *  Discussion:
		 *  This runnable should be assumed to be running on an instance of PersistenceThread, meaning that there should be a database connection implicitly.
		 *  Furthermore, cleanup should also be left alone, as the thread should take care of it.
		 *
		 */

        assert (Thread.currentThread() instanceof PersistenceThread);
        return null;
    }
}

