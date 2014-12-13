package net.uhcwork.BungeeGuard.Persistence;

import org.javalite.activejdbc.Model;

public class SaveRunner extends VoidRunner {
    private final Model model;

    public SaveRunner(Model m) {
        this.model = m;
    }

    @Override
    protected void run() {
        model.saveIt();
    }
}
