package com.networknt.tram.cdc.mysql.connector;

import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import com.networknt.eventuate.jdbc.EventuateSchema;
import com.networknt.eventuate.server.common.BinlogFileOffset;
import com.networknt.eventuate.cdc.mysql.binlog.IWriteRowsEventDataParser;
import com.networknt.eventuate.common.impl.JSonMapper;
import com.networknt.tram.message.common.Message;
import com.networknt.tram.message.common.MessageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class WriteRowsEventDataParser implements IWriteRowsEventDataParser<MessageWithDestination> {

  private DataSource dataSource;
  private final String sourceTableName;
  private Logger logger = LoggerFactory.getLogger(getClass());
  private static final String ID = "id";
  private static final String DESTINATION = "destination";
  private static final String HEADERS = "headers";
  private static final String PAYLOAD = "payload";

  private Map<String, Integer> columnOrders = new HashMap<>();

  private EventuateSchema eventuateSchema;

  public WriteRowsEventDataParser(DataSource dataSource, String sourceTableName, EventuateSchema eventuateSchema) {
    this.dataSource = dataSource;
    this.eventuateSchema = eventuateSchema;
    this.sourceTableName  = sourceTableName;
  }

  @Override
  public MessageWithDestination parseEventData(WriteRowsEventData eventData, String binlogFilename, long position) throws IOException {
    if (columnOrders.isEmpty()) {
      try {
        getColumnOrders();
      } catch (SQLException e) {
        logger.error("Error getting metadata", e);
        throw new RuntimeException(e);
      }
    }

    String id = (String)getValue(eventData, ID);
    String destination = (String)getValue(eventData, DESTINATION);
    String payload = (String)getValue(eventData, PAYLOAD);
    Map<String, String> headers = JSonMapper.fromJson((String)getValue(eventData, HEADERS), Map.class);
    headers.put(Message.ID, id);
    headers.put("binlogfile", binlogFilename);
    headers.put("binlogposition", Long.toString(position));
    return new MessageWithDestination(destination, new MessageImpl(payload, headers), new BinlogFileOffset(binlogFilename, position));
  }

  private Serializable getValue(WriteRowsEventData eventData, String columnName) {
    if(columnOrders.containsKey(columnName)) {
      return eventData.getRows().get(0)[columnOrders.get(columnName) - 1];
    }
    throw new RuntimeException("Column with name [" + columnName + "] not found. Have " + columnOrders.keySet());
  }

  private void getColumnOrders() throws SQLException {
    try (Connection connection = dataSource.getConnection()) {
      DatabaseMetaData metaData = connection.getMetaData();

      try (ResultSet columnResultSet =
                   metaData.getColumns(eventuateSchema.isEmpty() ? null : eventuateSchema.getEventuateDatabaseSchema(), "public", sourceTableName.toLowerCase(), null)) {

        while (columnResultSet.next()) {
          columnOrders.put(columnResultSet.getString("COLUMN_NAME").toLowerCase(),
                  columnResultSet.getInt("ORDINAL_POSITION"));
        }
      }
    }
  }

}
