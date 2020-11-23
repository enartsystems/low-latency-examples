package org.enartsystems.lowlatency.vertx.context;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.kafka.client.common.PartitionInfo;
import io.vertx.kafka.client.common.TopicPartition;
import io.vertx.kafka.client.consumer.KafkaConsumerRecord;

public interface IAMQClient<V, K> {
	public static enum DESERIALIZER{DEFAULT,BUFFER,JSON_OBJECT,JSON_ARRAY}
	/**
	 * 
	 * @param vertx
	 * @param config
	 * @param deserializer
	 */
	void create(Vertx vertx, Map<String, String> config, DESERIALIZER deserializer);

	/**
	 * 
	 * @param topics
	 * @param topicPartitions
	 * @param handler
	 */
	void subscribe(Set<String> topics, Handler<KafkaConsumerRecord<V, K>> handler);
	/**
	 * 
	 * @param topics
	 * @param clazz
	 */
	void subscribe(Set<String> topics, Class clazz);

	/**
	 * 
	 * @param topics
	 * @param topicPartitions
	 * @param result
	 */
	void subscribe(Set<String> topics, Set<TopicPartition> topicPartitions, Handler<AsyncResult<Void>> result);

	/**
	 * 
	 * @param topic
	 * @param result
	 */
	void partitionsFor(String topic, Handler<AsyncResult<List<PartitionInfo>>> result);

	/**
	 * 
	 * @param topic
	 * @param msg
	 * @param result
	 */
	void send(String topic, String msg, Handler<AsyncResult<Void>> result);

}