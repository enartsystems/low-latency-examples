/**
 * 
 */
package org.enartsystems.lowlatency.vertx.integration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.enartsystems.lowlatency.vertx.MainVerticle;
import org.enartsystems.lowlatency.vertx.context.IAMQClient;
import org.enartsystems.lowlatency.vertx.context.IAMQClient.DESERIALIZER;
import org.enartsystems.lowlatency.vertx.context.Topic;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.kafka.client.common.PartitionInfo;
import io.vertx.kafka.client.common.TopicPartition;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.consumer.KafkaConsumerRecord;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import lombok.extern.slf4j.Slf4j;

/**
 * @author manuel
 * 
 *
 */
@Slf4j
@ApplicationScoped
public class KafkaClte<V,K> implements IAMQClient<V, K> {
	
	DESERIALIZER deserializer = DESERIALIZER.DEFAULT;
    Vertx vertx;
	Map<String, String> config;
	KafkaConsumer<V, K> consumer ;
	KafkaProducer<V, K> producer ;
	/**
	 * 
	 * @param vertx
	 * @param config
	 * @param deserializer
	 */
	@Override
	public void create(Vertx vertx,Map<String,String> config,DESERIALIZER deserializer) {
		if(config==null || config.isEmpty()) {
			configureLocal();
		}else {
			this.config=config;
		}
		this.vertx=vertx;
		if(deserializer!=null) {
			this.deserializer=deserializer;
		}
		
	};
	private void configDeserializer() {
		switch (deserializer) {
		case DEFAULT:
			config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	    	config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	    	config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
	    	config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
	    	break;
		case BUFFER:
			config.put("key.deserializer", "io.vertx.kafka.client.serialization.BufferDeserializer");
	    	config.put("value.deserializer", "io.vertx.kafka.client.serialization.BufferDeserializer");
	    	config.put("key.serializer", "io.vertx.kafka.client.serialization.BufferSerializer");
	    	config.put("value.serializer", "io.vertx.kafka.client.serialization.BufferSerializer");
			break;
		case JSON_OBJECT:
			config.put("key.deserializer", "io.vertx.kafka.client.serialization.JsonObjectDeserializer");
	    	config.put("value.deserializer", "io.vertx.kafka.client.serialization.JsonObjectDeserializer");
	    	config.put("key.serializer", "io.vertx.kafka.client.serialization.JsonObjectSerializer");
	    	config.put("value.serializer", "io.vertx.kafka.client.serialization.JsonObjectSerializer");
			break;
		case JSON_ARRAY:
			config.put("key.deserializer", "io.vertx.kafka.client.serialization.JsonArrayDeserializer");
	    	config.put("value.deserializer", "io.vertx.kafka.client.serialization.JsonArrayDeserializer");
	    	config.put("key.serializer", "io.vertx.kafka.client.serialization.JsonArraySerializer");
	    	config.put("value.serializer", "io.vertx.kafka.client.serialization.JsonArraySerializer");
			break;
		default:
			break;
		}	
	}
	private void initConsumer() {
		if(consumer==null) {
		   consumer = KafkaConsumer.create(vertx, config);
		}
	}
	private void initProducer() {
		if(producer==null) {
			producer = KafkaProducer.create(vertx, config);
		}
		
	}
	/**
	 * 
	 * @param topics
	 * @param topicPartitions
	 * @param handler
	 */
	@Override
	public void subscribe(Set<String> topics,Handler<KafkaConsumerRecord<V, K>> handler) {
		initConsumer();
		consumer.handler(h->{
			
			 handler.handle(h);
		});
	    consumer.subscribe(topics,h->{
	    	if(h.succeeded()) {
	    		
	    	}
	    });
	    
	}
	
	public void subscribe(Set<String> topics, Class clazz) {
		initConsumer();
		try {
			Object	listeners = clazz.getDeclaredConstructor().newInstance();
			consumer.handler(h->{
				log.info(h.toString());
				for(Method m:listeners.getClass().getDeclaredMethods()) {
					if(m.isAnnotationPresent(Topic.class)) {
						if(m.getAnnotation(Topic.class).topic().equals(h.topic())) {
							try {
								m.invoke(h);
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								log.error(e.getMessage());
							}
						}
					}
				}
			});
		    consumer.subscribe(topics,h->{
		    	if(h.succeeded()) {
		    		
		    	}
		    });
			
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e1) {
			log.error(e1.getMessage());
		}
		
	}
	/**
	 * 
	 * @param topics
	 * @param topicPartitions
	 * @param result
	 */
	@Override
	public void subscribe(Set<String> topics,Set<TopicPartition> topicPartitions,Handler<AsyncResult<Void>> result) {
		initConsumer();
	    consumer.assign(topicPartitions,result);
	    
	}
	/**
	 * 
	 * @param topic
	 * @param result
	 */
	@Override
	public void partitionsFor(String topic,Handler<AsyncResult<List<PartitionInfo>>> result){
		consumer.partitionsFor(topic, info -> {
	    	  if (info.succeeded()) {
                   result.handle((AsyncResult<List<PartitionInfo>>) info.result());
	    	  }
	    	});
	}
	/**
	 * 
	 * @param topic
	 * @param msg
	 * @param result
	 */
	@Override
	public void send(String topic,String msg,Handler<AsyncResult<Void>> result) {
		initProducer();
		KafkaProducerRecord<V, K> record =  (KafkaProducerRecord<V, K>) KafkaProducerRecord.create(topic, msg);
  	    producer.write(record,result);
  	   log.info(topic + "\t"+msg);
	}
	/**
	 * 
	 */
    private void configureLocal() {
        config = new HashMap<>();
		configDeserializer();
    	config.put("bootstrap.servers", "localhost:9092,192.168.1.36:9092");
    	config.put("group.id", "my_group");
    	config.put("auto.offset.reset", "earliest");
    	config.put("enable.auto.commit", "true");
    	config.put("acks", "1");
    }
}
