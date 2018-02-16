package com.networknt.tram.cdc.polling.connector;


import com.networknt.tram.cdc.mysql.connector.MessageWithDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class PollingDao<EVENT_BEAN, EVENT, ID> {
  private Logger logger = LoggerFactory.getLogger(getClass());

  private MessagePollingDataProvider pollingDataParser;
  private DataSource dataSource;
  private int maxEventsPerPolling;
  private int maxAttemptsForPolling;
  private int pollingRetryIntervalInMilliseconds;

  public PollingDao(MessagePollingDataProvider pollingDataParser,
                    DataSource dataSource,
                    int maxEventsPerPolling,
                    int maxAttemptsForPolling,
                    int pollingRetryIntervalInMilliseconds) {

    if (maxEventsPerPolling <= 0) {
      throw new IllegalArgumentException("Max events per polling parameter should be greater than 0.");
    }

    this.pollingDataParser = pollingDataParser;
    this.dataSource = dataSource;
    this.maxEventsPerPolling = maxEventsPerPolling;
    this.maxAttemptsForPolling = maxAttemptsForPolling;
    this.pollingRetryIntervalInMilliseconds = pollingRetryIntervalInMilliseconds;
  }

  public int getMaxEventsPerPolling() {
    return maxEventsPerPolling;
  }

  public void setMaxEventsPerPolling(int maxEventsPerPolling) {
    this.maxEventsPerPolling = maxEventsPerPolling;
  }

  public List<MessageWithDestination> findEventsToPublish() {

    String query = String.format("SELECT * FROM %s WHERE %s = 0 and ROWNUM <= ? ORDER BY %s ASC",
      pollingDataParser.table(), pollingDataParser.publishedField(), pollingDataParser.idField());

    List<PublishedMessageBean> messageBeans = handleConnectionLost(() -> handleFindQuery(query, maxEventsPerPolling));

    return messageBeans.stream().map(pollingDataParser::transformEventBeanToEvent).collect(Collectors.toList());

  }

  private List<PublishedMessageBean> handleFindQuery(String query, int maxEventsPerPolling) throws SQLException{
    logger.info("cdc polling query:"  + query);

    List<PublishedMessageBean> messages = new ArrayList<>();
    try (final Connection connection = dataSource.getConnection()) {
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setInt(1, maxEventsPerPolling);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        PublishedMessageBean publishedEventBean = new PublishedMessageBean(rs.getString(pollingDataParser.idField()), rs.getString(pollingDataParser.destinationField()),
                rs.getString(pollingDataParser.headersField()), rs.getString(pollingDataParser.payloadField()));
        messages.add(publishedEventBean);
      }
    } catch (SQLException e) {
      logger.error("SqlException:", e);
      throw new SQLException(e);
    }
     return messages;
  }

  public void markEventsAsPublished(List<MessageWithDestination> events) {

    List<String> ids = events.stream().map(message -> pollingDataParser.getId(message)).collect(Collectors.toList());

    String query = String.format("UPDATE %s SET %s = 1 WHERE %s in (%s)",
        pollingDataParser.table(), pollingDataParser.publishedField(), pollingDataParser.idField(), preparePlaceHolders(ids.size()));

    handleConnectionLost(() -> handleUpdatePublished(query, ids));
  }

  private int  handleUpdatePublished  (String query, List<String> ids) throws SQLException{
    logger.info("mark Events As Published query:"  + query);
    int count = 0;
    try (final Connection connection = dataSource.getConnection()) {
      PreparedStatement stmt = connection.prepareStatement(query);
      setValues(stmt, ids.toArray());
      count = stmt.executeUpdate();
      System.out.println("result:" + count);
    } catch (SQLException e) {
      logger.error("SqlException:", e);
      throw new SQLException(e);
    }
    return count;
  }

  private <T> T handleConnectionLost(Callable<T> query) {
    int attempt = 0;

    while(true) {
      try {
        T result = query.call();
        if (attempt > 0)
          logger.info("Reconnected to database");
        return result;
      } catch (SQLException e) {
        logger.error(String.format("Could not access database %s - retrying in %s milliseconds", e.getMessage(), pollingRetryIntervalInMilliseconds), e);

        if (attempt++ >= maxAttemptsForPolling) {
          throw new RuntimeException(e);
        }

        try {
          Thread.sleep(pollingRetryIntervalInMilliseconds);
        } catch (InterruptedException ie) {
          logger.error(ie.getMessage(), ie);
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static String preparePlaceHolders(int length) {
    return String.join(",", Collections.nCopies(length, "?"));
  }

  public static void setValues(PreparedStatement preparedStatement, Object... values) throws SQLException {
    for (int i = 0; i < values.length; i++) {
      preparedStatement.setObject(i + 1, values[i]);
    }
  }
}
