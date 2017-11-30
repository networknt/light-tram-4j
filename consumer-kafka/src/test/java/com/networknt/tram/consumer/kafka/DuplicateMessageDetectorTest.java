package com.networknt.tram.consumer.kafka;

import com.networknt.service.SingletonServiceFactory;
import junit.framework.TestCase;
import org.h2.tools.RunScript;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Junit test class for SagaLockManagerImplTest.
 * use H2 test database for data source
 *
 * @author Gavin Chen
 */
public class DuplicateMessageDetectorTest {

    private static DataSource ds;

    static {
        ds = SingletonServiceFactory.getBean(DataSource.class);
        try (Connection connection = ds.getConnection()) {
            // Runscript doesn't work need to execute batch here.
            String schemaResourceName = "/message_repository_ddl.sql";
            InputStream in = DuplicateMessageDetectorTest.class.getResourceAsStream(schemaResourceName);

            if (in == null) {
                throw new RuntimeException("Failed to load resource: " + schemaResourceName);
            }
            InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
            RunScript.execute(connection, reader);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private DuplicateMessageDetector duplicateMessageDetector = SingletonServiceFactory.getBean(DuplicateMessageDetector.class);

    @Test
    public void shouldDetectDuplicate() {

        String consumerId = getClass().getName();
        String messageId = Long.toString(System.currentTimeMillis());

        assertFalse(duplicateMessageDetector.isDuplicate(consumerId, messageId));
        assertTrue(duplicateMessageDetector.isDuplicate(consumerId, messageId));
    }

    @Test
    public void testIsDuplicate() {
        boolean firstMessage = duplicateMessageDetector.isDuplicate("111", "222");
        TestCase.assertFalse(firstMessage);
        boolean sesondMessage = duplicateMessageDetector.isDuplicate("111", "333");
        TestCase.assertFalse(sesondMessage);
        boolean duplicatedFirst = duplicateMessageDetector.isDuplicate("111", "222");
        assertTrue(duplicatedFirst);
    }

}