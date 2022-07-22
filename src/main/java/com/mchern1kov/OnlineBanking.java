package com.mchern1kov;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mchern1kov.config.GuiceModule;
import com.mchern1kov.server.OnlineBankingServer;

public class OnlineBanking {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new GuiceModule());
        OnlineBankingServer server = injector.getInstance(OnlineBankingServer.class);
        server.start();
    }
}
