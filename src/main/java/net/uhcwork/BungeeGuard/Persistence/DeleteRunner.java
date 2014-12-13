package net.uhcwork.BungeeGuard.Persistence;

import org.javalite.activejdbc.Model;

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
