package com.mchern1kov.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mchern1kov.config.DbConnectionManager;
import com.mchern1kov.database.AccountDao;
import com.mchern1kov.database.TransferDao;
import com.mchern1kov.model.Account;
import com.mchern1kov.model.Transfer;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.NotFoundResponse;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Slf4j
@Singleton
public class OnlineBankingService {

    private final DbConnectionManager dbConnectionManager;
    private final AccountDao accountDao;
    private final TransferDao transferDao;

    @Inject
    public OnlineBankingService(DbConnectionManager dbConnectionManager, AccountDao accountDao, TransferDao transferDao) {
        this.dbConnectionManager = dbConnectionManager;
        this.accountDao = accountDao;
        this.transferDao = transferDao;
    }

    public Transfer makeTransfer(long fromId, long toId, BigDecimal amount) {
        try (Connection connection = dbConnectionManager.getDbConnection()) {
            try {
                connection.setAutoCommit(false);
                connection.beginRequest();
                Map<Long, Account> accountsToUpdate = accountDao.readForTransfer(connection, fromId, toId);
                Account accountFrom = accountsToUpdate.get(fromId);
                Account accountTo = accountsToUpdate.get(toId);

                if (accountFrom == null) {
                    throw new NotFoundResponse("Sender is not found");
                }
                if (accountTo == null) {
                    throw new NotFoundResponse("Receiver is not found");
                }

                if (accountFrom.getBalance().compareTo(amount) < 0) {
                    connection.rollback();
                    throw new BadRequestResponse("Not enough money");
                }

                Transfer transfer = new Transfer();
                transfer.setAmount(amount);
                transfer.setFromId(fromId);
                transfer.setToId(toId);
                transferDao.create(connection, transfer);

                accountDao.updateBalance(connection, accountFrom.getId(), accountFrom.getBalance().subtract(amount));
                accountDao.updateBalance(connection, accountTo.getId(), accountTo.getBalance().add(amount));

                connection.commit();
                return transfer;
            } catch (SQLException e) {
                log.error("Exception during making a transfer:", e);
                connection.rollback();
                throw new InternalServerErrorResponse();
            }
        } catch (SQLException e) {
            log.error("Exception during making a transfer:", e);
            throw new InternalServerErrorResponse();
        }
    }

    public List<Transfer> listTransfersByUser(Integer userId) {
        try (Connection connection = dbConnectionManager.getDbConnection()) {
            return transferDao.readUserTransfers(connection, userId);
        } catch (SQLException e) {
            log.error("Exception during account transfers listing:", e);
            throw new InternalServerErrorResponse();
        }
    }

    public Account getAccountInfo(Integer userId) {
        try (Connection connection = dbConnectionManager.getDbConnection()) {
            return accountDao.read(connection, userId);
        } catch (SQLException e) {
            log.error("Exception during getting account info:", e);
            throw new InternalServerErrorResponse();
        }
    }
}
