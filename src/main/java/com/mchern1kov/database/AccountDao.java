package com.mchern1kov.database;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mchern1kov.config.DbConnectionManager;
import com.mchern1kov.model.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static java.math.RoundingMode.HALF_UP;

@Singleton
public class AccountDao {

    private static final String TABLE_INIT = "create table account (" +
            "id bigint auto_increment primary key, " +
            "full_name varchar(255) not null unique, " +
            "balance numeric(20,2) not null);";

    private static final String ADD_INITIAL_ACCOUNTS =
            "insert into account (id, full_name, balance) values (0, 'First User', 2000);" +
                    "insert into account (id, full_name, balance) values (1, 'Second User', 1500);" +
                    "insert into account (id, full_name, balance) values (2, 'Third User', 2222.22);";

    private static final String SELECT_ACCOUNT_BY_ID = "select * from account where account.id = ?;";
    private static final String SELECT_ACCOUNTS_BY_ID_FOR_UPDATE = "select * from account where account.id in (?,?) for update;";
    private static final String UPDATE_BALANCE = "update account set balance = ? where id = ?;";

    @Inject
    public AccountDao(DbConnectionManager dbConnectionManager) throws SQLException {
        try (Connection connection = dbConnectionManager.getDbConnection()) {
            connection.createStatement().execute(TABLE_INIT);
            connection.createStatement().execute(ADD_INITIAL_ACCOUNTS);
        }
    }

    public Account read(Connection connection, long id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ACCOUNT_BY_ID);
        preparedStatement.setLong(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            Account account = new Account();
            account.setId(resultSet.getLong("id"));
            account.setFullName(resultSet.getString("full_name"));
            account.setBalance(resultSet.getBigDecimal("balance").setScale(2, HALF_UP));
            return account;
        }
        return null;
    }

    /**
     * To avoid database deadlocks both accounts must be selected in a single query.
     *
     * @return a list of "from" and "to" accounts
     */
    public Map<Long, Account> readForTransfer(Connection connection, long idFrom, long idTo) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ACCOUNTS_BY_ID_FOR_UPDATE);
        preparedStatement.setLong(1, idFrom);
        preparedStatement.setLong(2, idTo);
        ResultSet resultSet = preparedStatement.executeQuery();
        Map<Long, Account> accounts = new HashMap<>();
        while (resultSet.next()) {
            Account account = new Account();
            account.setId(resultSet.getLong("id"));
            account.setFullName(resultSet.getString("full_name"));
            account.setBalance(resultSet.getBigDecimal("balance").setScale(2, HALF_UP));
            accounts.put(account.getId(), account);
        }
        return accounts;
    }

    public void updateBalance(Connection connection, long id, BigDecimal newBalance) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BALANCE);
        preparedStatement.setBigDecimal(1, newBalance);
        preparedStatement.setLong(2, id);
        preparedStatement.executeUpdate();
    }
}
