package fr.PunKeel.BungeeGuard.Persistence;

import org.javalite.activejdbc.Model;

public class InsertRunner extends VoidRunner {
    private final Model model;

    public InsertRunner(Model m) {
        this.model = m;
    }

    @Override
    protected void run() {
        model.insert();
    }
}