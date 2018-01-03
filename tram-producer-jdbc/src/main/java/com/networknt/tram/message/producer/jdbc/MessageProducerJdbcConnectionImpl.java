package com.networknt.tram.message.producer.jdbc;

import com.networknt.eventuate.common.impl.JSonMapper;
import com.networknt.eventuate.jdbc.IdGenerator;
import com.networknt.tram.message.common.Message;
import com.networknt.tram.message.producer.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class MessageProducerJdbcConnectionImpl implements MessageProducer {

    final static private Logger logger = LoggerFactory.getLogger(MessageProducerJdbcConnectionImpl.class);

    private Connection connection;
    private IdGenerator idGenerator;

    /**
     * This class should be constructed in service.yml and before MessageProducer binding,
     * we need to have DataSource and IdGenerator bindings defined in service.yml
     *
     * @param connection  Connection
     * @param idGenerator IdGenerator
     */
    public MessageProducerJdbcConnectionImpl(Connection connection, IdGenerator idGenerator) {
        this.connection = connection;
        this.idGenerator = idGenerator;
    }

    public MessageProducerJdbcConnectionImpl(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }


    @Override
    public void send(String destination, Message message) {
        Objects.requireNonNull(destination);
        Objects.requireNonNull(message);

        String psInsert = "insert into message(id, destination, headers, payload) values(?, ?, ?, ?)";
        int count;
        String id = idGenerator.genId().asString();
        message.getHeaders().put(Message.ID, id);

        try ( final PreparedStatement stmt = connection.prepareStatement(psInsert)) {
            stmt.setString(1, id);
            stmt.setString(2, destination);
            stmt.setString(3, JSonMapper.toJson(message.getHeaders()));
            stmt.setString(4, message.getPayload());

            count = stmt.executeUpdate();

            if (count != 1) {
                logger.error("Failed to insert Message: {}", message.getPayload());
            }
        } catch (SQLException e) {
            logger.error("SqlException:", e);
            throw new RuntimeException(e);
        }
    }
}
