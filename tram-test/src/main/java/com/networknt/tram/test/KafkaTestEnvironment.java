/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.networknt.tram.test;

import kafka.server.KafkaServer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.*;

/**
 * Abstract class providing a Kafka test environment.
 */
public interface KafkaTestEnvironment {

	class Config {
		private int kafkaServersNumber = 1;
		private Properties kafkaServerProperties = null;
		private boolean secureMode = false;

		/**
		 * Please use {@link KafkaTestEnvironment#createConfig()} method.
		 */
		private Config() {
		}

		public int getKafkaServersNumber() {
			return kafkaServersNumber;
		}

		public Config setKafkaServersNumber(int kafkaServersNumber) {
			this.kafkaServersNumber = kafkaServersNumber;
			return this;
		}

		public Properties getKafkaServerProperties() {
			return kafkaServerProperties;
		}

		public Config setKafkaServerProperties(Properties kafkaServerProperties) {
			this.kafkaServerProperties = kafkaServerProperties;
			return this;
		}

		public boolean isSecureMode() {
			return secureMode;
		}

		public Config setSecureMode(boolean secureMode) {
			this.secureMode = secureMode;
			return this;
		}
	}

	String KAFKA_HOST = "localhost";

	static Config createConfig() {
		return new Config();
	}

	void prepare(Config config);

	void shutdown();

	void deleteTestTopic(String topic);

	void createTestTopic(String topic, int numberOfPartitions, short replicationFactor, Properties properties);

	void createTestTopic(String topic, int numberOfPartitions, short replicationFactor);

	Properties getStandardProperties();

	Properties getSecureProperties();

	String getBrokerConnectionString();

	String getVersion();

	List<KafkaServer> getBrokers();

	<K, V> Collection<ConsumerRecord<K, V>> getAllRecordsFromTopic(
			Properties properties,
			String topic,
			int partition,
			long timeout);

	// -- offset handlers

	/**
	 * Simple interface to commit and retrieve offsets.
	 */
	interface KafkaOffsetHandler {
		Long getCommittedOffset(String topicName, int partition);

		void setCommittedOffset(String topicName, int partition, long offset);

		void close();
	}

	KafkaOffsetHandler createOffsetHandler();

	// -- leader failure simulation

	void restartBroker(int leaderId) throws Exception;

	int getLeaderToShutDown(String topic) throws Exception;

	int getBrokerId(KafkaServer server);

	boolean isSecureRunSupported();
}
