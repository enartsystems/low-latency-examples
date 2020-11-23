package org.enartsystems.lowlatency.vertx;

import java.util.HashSet;
import java.util.Set;

import org.enartsystems.lowlatency.vertx.context.IAMQClient;
import org.enartsystems.lowlatency.vertx.context.IBackUp;
import org.enartsystems.lowlatency.vertx.context.ILoadData;
import org.enartsystems.lowlatency.vertx.infraestructure.BackUpFileImpl;
import org.enartsystems.lowlatency.vertx.infraestructure.LoadDataFileImpl;
import org.enartsystems.lowlatency.vertx.integration.KafkaClte;
import org.enartsystems.lowlatency.vertx.services.KafkaListeners;
import org.jboss.weld.environment.se.Weld;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.net.NetServer;
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumerRecord;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainVerticle extends AbstractVerticle {

	IAMQClient kc;
	IBackUp back;
	ILoadData loadData;

	@Override
	public void init(Vertx vertx, Context context) {
		super.init(vertx, context);

	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		kc = Loader.spiLoaderGeneric(IAMQClient.class);
		back = Loader.spiLoaderGeneric(IBackUp.class);
		loadData = Loader.spiLoaderGeneric(ILoadData.class);

		// existe un bug con la reflexion pendiente de solucionar
		if (kc == null)
			kc = new KafkaClte();
		if (back == null)
			back = new BackUpFileImpl();
		if (loadData == null)
			loadData = new LoadDataFileImpl();

		kc.create(vertx, null, IAMQClient.DESERIALIZER.BUFFER);
		back.start(vertx);
		startPromise.complete();
		log.info("Cliente creado");
		read();
		startKafka();
		
	}

	private void read() {
		loadData.loadFile(vertx, this.getClass().getResource("/iebs008_kpis.csv"), back);
	}

	private void startKafka() {
		Set<String> topics = new HashSet<>();
		topics.add("dispositivos");
		topics.add("indicadores");
		kc.subscribe(topics, KafkaListeners.class);
//	  kc.subscribe(topics, handler->{
//		  log.info(((KafkaConsumerRecord)handler).toString());
//	  });
		for (int i = 0; i < 10; i++) {
			kc.send("indicators", "indicador_" + i, result -> {
				result.getClass();
			});
		}
	}
}