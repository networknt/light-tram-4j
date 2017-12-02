package com.networknt.tram.consumer.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlTableBasedDuplicateMessageDetector implements DuplicateMessageDetector {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private DataSource dataSource;

    /**
     * This class can only be constructed from service.yml binding. As there is a parameter
     * dataSource in the constructor, it needs the DataSource binding before it in service.yml
     *
     * @param dataSource DataSource
     */
    public SqlTableBasedDuplicateMessageDetector(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean isDuplicate(String consumerId, String messageId) {
        String psInsert = "insert into received_messages(consumer_id, message_id) values(?, ?)";
        try (final Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(psInsert);
            stmt.setString(1, consumerId);
            stmt.setString(2, messageId);
            stmt.executeUpdate();
            return false;
        } catch (SQLException e) {
            logger.debug("duplicated message, consumerId : {} and message-id: {}", consumerId, messageId);
            return true;
        }
    }
}
