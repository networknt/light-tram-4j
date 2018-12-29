package com.networknt.tram.producer;

import com.networknt.config.JsonMapper;
import com.networknt.tram.message.common.IdGenerator;
import com.networknt.tram.message.common.Message;
import com.networknt.tram.message.producer.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class MessageProducerJdbcImpl implements MessageProducer {

    final static private Logger logger = LoggerFactory.getLogger(MessageProducerJdbcImpl.class);

    private DataSource dataSource;
    private IdGenerator idGenerator;
    private String psInsert = "insert into message(id, destination, headers, payload) values(?, ?, ?, ?)";
    /**
     * This class should be constructed in service.yml and before MessageProducer binding,
     * we need to have DataSource and IdGenerator bindings defined in service.yml
     *
     * @param dataSource  DataSource
     * @param idGenerator IdGenerator
     */
    public MessageProducerJdbcImpl(DataSource dataSource, IdGenerator idGenerator) {
        this.dataSource = dataSource;
        this.idGenerator = idGenerator;
    }

    /**
     * This class should be constructed in service.yml and before MessageProducer binding,
     * we need to have DataSource and IdGenerator bindings defined in service.yml
     *
     * @param dataSource  DataSource
     * @param idGenerator IdGenerator
     * @param psInsert message insert String
     */
    public MessageProducerJdbcImpl(DataSource dataSource, IdGenerator idGenerator, String psInsert) {
        this.dataSource = dataSource;
        this.idGenerator = idGenerator;
        this.psInsert = psInsert;
    }

    @Override
    public void send(String destination, Message message) {
        Objects.requireNonNull(destination);
        Objects.requireNonNull(message);

        int count;
        String id = idGenerator.genId().asString();
        message.getHeaders().put(Message.ID, id);

        try (final Connection connection = dataSource.getConnection(); final PreparedStatement stmt = connection.prepareStatement(this.psInsert)) {
            stmt.setString(1, id);
            stmt.setString(2, destination);
            stmt.setString(3, JsonMapper.toJson(message.getHeaders()));
            stmt.setString(4, message.getPayload());

            count = stmt.executeUpdate();

            if (count != 1) {
                logger.error("Failed to insert Message: {}", message.getPayload());
            }
        } catch (SQLException e) {
            logger.error("SqlException:", e);
        }
    }
}
