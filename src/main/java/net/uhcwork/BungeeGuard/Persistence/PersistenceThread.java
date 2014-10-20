package net.uhcwork.BungeeGuard.Persistence;

/**
 * Part of net.uhcwork.BungeeGuard.Persistence (BungeeGuard)
 * Date: 15/10/2014
 * Time: 18:21
 * May be open-source & be sold (by mguerreiro, of course !)
 */

import net.md_5.bungee.api.ProxyServer;
import org.javalite.activejdbc.Base;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: kolin
 * Date: 12/15/13
 * Time: 4:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class PersistenceThread extends Thread {
    public PersistenceThread(ThreadGroup group, Runnable runnable) {
        super(group, runnable);
    }

    @Override
    public void run() {
        System.out.println("[ORM] Creation de la connexion SQL pour " + Thread.currentThread().toString() + " ... :)");
        this.setup();

        super.run();

        this.cleanup();
    }

    private void setup() {
        String host = getEnv("MYSQL_HOST");
        String database = getEnv("MYSQL_DATABASE");
        String user = getEnv("MYSQL_USER");
        String pass = getEnv("MYSQL_PASS");
        if (host.isEmpty() || database.isEmpty() || user.isEmpty() || pass.isEmpty()) {
            ProxyServer.getInstance().stop();
            throw new RuntimeException("La configuration est mauvaise, chef.");
        }
        Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://" + host + "/" + database, user, pass);
    }

    private static String getEnv(String name) {
        String _ = System.getenv(name);
        if (_ == null) {
            Properties prop = new Properties();
            try {
                prop.load(new FileInputStream("config.properties"));
                _ = prop.getProperty(name, "");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return _ == null ? "" : _;
    }


    private void cleanup() {
        /* Close the shared database connection. */
        Base.close();
    }
}