/**
 * 
 */
package org.enartsystems.lowlatency.vertx.services;

import org.enartsystems.lowlatency.vertx.MainVerticle;
import org.enartsystems.lowlatency.vertx.context.Topic;

import io.vertx.core.Handler;
import io.vertx.kafka.client.consumer.KafkaConsumerRecord;
import lombok.extern.slf4j.Slf4j;

/**
 * @author manuel
 *
 */
@Slf4j
public class KafkaListeners {

	
	@Topic(topic = "indicators")
	public void ListenIndicators(Handler<KafkaConsumerRecord> handler) {
		 log.info(((KafkaConsumerRecord)handler).toString());
	}
	
	@Topic(topic = "dispositivos")
	public void ListenDispositivos(Handler<KafkaConsumerRecord> handler) {
		 log.info(((KafkaConsumerRecord)handler).toString());
	}

}
