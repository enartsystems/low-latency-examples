/**
 * 
 */
package org.enartsystems.lowlatency.vertx.infraestructure;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import org.enartsystems.lowlatency.domain.Dispositivo;
import org.enartsystems.lowlatency.vertx.context.IBackUp;
import org.enartsystems.lowlatency.vertx.context.ILoadData;

import com.opencsv.bean.CsvToBeanBuilder;

import io.reactivex.Flowable;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.FileSystem;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.reactivex.FlowableHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author manuel
 *
 */
@Slf4j
public class LoadDataFileImpl implements ILoadData {

	SharedData sharedData;
	AsyncMap<String, List<Dispositivo>> mapDispositivo;


	private void startSharedData(Vertx vertx) {
		if (sharedData == null) {
			sharedData = vertx.sharedData();
		}
		sharedData.<String, List<Dispositivo>>getAsyncMap("mapDispositivo", res -> {
			if (res.succeeded()) {
				mapDispositivo = res.result();
			} else {
				log.error("mapDispositivo not exists");
			}
		});
	}

	@Override
	public void loadFile(Vertx vertx, URL path, IBackUp backup) {
		startSharedData(vertx);

		FileSystem fileSystem = vertx.fileSystem();
		fileSystem.open(path.getPath(), new io.vertx.core.file.OpenOptions(), result -> {
			AsyncFile file = result.result();
			String uuid = UUID.randomUUID().toString();
			Flowable<Buffer> observable = FlowableHelper.toFlowable(file);
			mapDispositivo.put(uuid, new ArrayList<Dispositivo>(), completion -> {
				if (completion.failed()) {
					log.error("fail collection");
				}else {
					try {
						List<Dispositivo> beans = new CsvToBeanBuilder(new FileReader(path.getFile()))
							       .withType(Dispositivo.class).build().parse();	
						mapDispositivo.get(uuid, resultHandler -> {
							try {
								resultHandler.result().addAll(beans);
							} catch (Exception e) {
								log.error(e.getMessage());
							}

						});
					} catch (IllegalStateException | FileNotFoundException e) {
						log.error("fail parse beans");
					}
				}
			});

			observable.forEach(data -> {
				log.info("Read data: " + data.toString("UTF-8"));
				backup.addRow(data.toString("UTF-8"));
			});
			backup.persist();
		});
	}

	

	
	

	
}
