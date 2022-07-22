package com.mchern1kov.server;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mchern1kov.controller.OnlineBankingController;
import io.javalin.Javalin;
import io.javalin.core.validation.JavalinValidation;

import java.math.BigDecimal;

@Singleton
public class OnlineBankingServer {

    private final OnlineBankingController onlineBankingController;

    @Inject
    public OnlineBankingServer(OnlineBankingController onlineBankingController) {
        this.onlineBankingController = onlineBankingController;
    }

    public void start() {
        this.configure().start();
    }

    Javalin configure() {
        Javalin app = Javalin.create();

        // Endpoints
        app.get("/api/health", ctx -> ctx.result("healthy"));

        app.post("/api/transfer", onlineBankingController::makeTransfer);
        app.get("/api/{accountId}", onlineBankingController::getAccountInfo);
        app.get("/api/{accountId}/transfer", onlineBankingController::listTransfersByAccount);

        JavalinValidation.register(BigDecimal.class, BigDecimal::new);

        return app;
    }
}
