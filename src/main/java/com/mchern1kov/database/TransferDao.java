package com.mchern1kov.database;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mchern1kov.config.DbConnectionManager;
import com.mchern1kov.model.Transfer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.math.RoundingMode.HALF_UP;

@Singleton
public class TransferDao {

    private static final String TABLE_INIT = "create table transfer (" +
            "id bigint auto_increment primary key, " +
            "from_id bigint not null, " +
            "to_id bigint not null, " +
            "amount numeric(20,2) not null," +
            "foreign key (from_id) references account(id)," +
            "foreign key (to_id) references account(id))";

    private static final String CREATE_TRANSFER = "insert into transfer (from_id, to_id, amount) values (?,?,?)";
    private static final String SELECT_TRANSFER_BY_FROM_TO_ID = "select * from transfer where from_id = ? or to_id = ?";

    @Inject
    public TransferDao(DbConnectionManager dbConnectionManager) throws SQLException {
        try (Connection connection = dbConnectionManager.getDbConnection()) {
            connection.createStatement().execute(TABLE_INIT);
        }
    }

    public Transfer create(Connection connection, Transfer transfer) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(CREATE_TRANSFER);
        preparedStatement.setLong(1, transfer.getFromId());
        preparedStatement.setLong(2, transfer.getToId());
        preparedStatement.setBigDecimal(3, transfer.getAmount());
        long id = preparedStatement.executeUpdate();
        transfer.setId(id);
        return transfer;
    }

    public List<Transfer> readUserTransfers(Connection connection, int id) throws SQLException {
        List<Transfer> transfers = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_TRANSFER_BY_FROM_TO_ID);
        preparedStatement.setInt(1, id);
        preparedStatement.setInt(2, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Transfer transfer = new Transfer();
            transfer.setId(resultSet.getLong("id"));
            transfer.setFromId(resultSet.getLong("from_id"));
            transfer.setToId(resultSet.getLong("to_id"));
            transfer.setAmount(resultSet.getBigDecimal("amount").setScale(2, HALF_UP));
            transfers.add(transfer);
        }
        return transfers;
    }
}
