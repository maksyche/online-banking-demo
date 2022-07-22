package com.mchern1kov.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mchern1kov.model.Account;
import com.mchern1kov.model.Transfer;
import com.mchern1kov.service.OnlineBankingService;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.math.BigDecimal;
import java.util.List;

@Singleton
public class OnlineBankingController {

    private final OnlineBankingService onlineBankingService;

    @Inject
    public OnlineBankingController(OnlineBankingService onlineBankingService) {
        this.onlineBankingService = onlineBankingService;
    }

    public void makeTransfer(Context context) {
        long fromId = context.queryParamAsClass("fromId", Long.class).get();
        long toId = context.queryParamAsClass("toId", Long.class).get();
        BigDecimal amount = context.queryParamAsClass("amount", BigDecimal.class).get();
        context.json(onlineBankingService.makeTransfer(fromId, toId, amount));
    }

    public void listTransfersByAccount(Context context) {
        Integer userId = context.pathParamAsClass("accountId", Integer.class).get();
        List<Transfer> transfers = onlineBankingService.listTransfersByUser(userId);
        if (transfers.isEmpty()) {
            throw new NotFoundResponse();
        }
        context.json(transfers);
    }

    public void getAccountInfo(Context context) {
        Integer userId = context.pathParamAsClass("accountId", Integer.class).get();
        Account account = onlineBankingService.getAccountInfo(userId);
        if (account == null) {
            throw new NotFoundResponse();
        }
        context.json(account);
    }
}
