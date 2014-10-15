package net.uhcwork.BungeeGuard.Persistence;

import org.javalite.activejdbc.Model;

/**
 * Part of net.uhcwork.BungeeGuard.Persistence (BungeeGuard)
 * Date: 16/10/2014
 * Time: 24:14
 * May be open-source & be sold (by mguerreiro, of course !)
 */

public class SaveRunner extends VoidRunner {
    Model model;

    public SaveRunner(Model m) {
        this.model = m;
    }

    @Override
    protected void run() {
        model.saveIt();
    }
}
