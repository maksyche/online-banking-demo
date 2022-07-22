package com.mchern1kov.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mchern1kov.config.GuiceModule;
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJackson;
import io.javalin.testtools.JavalinTest;
import okhttp3.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OnlineBankingServerFunctionalTest {

    private JavalinJackson jackson = new JavalinJackson();
    private Javalin app;

    @BeforeEach
    public void init() {
        Injector injector = Guice.createInjector(new GuiceModule());
        OnlineBankingServer server = injector.getInstance(OnlineBankingServer.class);
        app = server.configure();
    }

    @Test
    public void testHealthCheck() {
        JavalinTest.test(app, ((server, client) -> {
            Response healthCheckResponse = client.get("/api/health");
            Assertions.assertEquals(200, healthCheckResponse.code());
            Assertions.assertEquals("healthy", healthCheckResponse.body().string());
        }));
    }

    // Do more tests here
}
