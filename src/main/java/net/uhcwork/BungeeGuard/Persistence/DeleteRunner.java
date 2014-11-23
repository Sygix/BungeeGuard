package net.uhcwork.BungeeGuard.Persistence;

import org.javalite.activejdbc.Model;

/**
 * Part of net.uhcwork.BungeeGuard.Persistence (BungeeGuard)
 * Date: 23/11/2014
 * Time: 22:44
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class DeleteRunner extends VoidRunner {
    private final Model model;

    public DeleteRunner(Model m) {
        this.model = m;
    }

    @Override
    protected void run() {
        model.delete();
    }
}
