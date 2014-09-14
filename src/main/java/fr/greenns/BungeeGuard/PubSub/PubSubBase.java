package fr.greenns.BungeeGuard.PubSub;

/**
 * Part of fr.greenns.BungeeGuard.PubSub
 * Date: 30/08/2014
 * Time: 00:13
 * May be open-source & be sold (by PunKeel, of course !)
 */
public abstract class PubSubBase {
    public void handle(String channel, String message, String[] args) {
        System.out.println("[PubSub]~Unknown~ Channel:" + channel + "; message: " + message);
    }

    public boolean ignoreSelfMessage() {
        return false;
    }
}
