/**
 * 
 */
package org.enartsystems.lowlatency.vertx.infraestructure;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import org.enartsystems.lowlatency.vertx.MainVerticle;
import org.enartsystems.lowlatency.vertx.context.IBackUp;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.impl.BufferImpl;
import io.vertx.core.file.FileSystem;
import lombok.extern.slf4j.Slf4j;

/**
 * @author manuel
 *
 */
@Slf4j
@ApplicationScoped
public class BackUpFileImpl implements IBackUp {
	Vertx vertx;
	StringBuffer sb ;
	/**
	 * 
	 */
	public BackUpFileImpl() {
		// TODO Auto-generated constructor stub
	}
	public void start(Vertx vertx) {
		this.vertx = vertx;
	}
	@Override
	public void addRow(String s) {
		if(sb==null) {
			sb = new StringBuffer();
			sb.append(s);
		}
		
	}

	@Override
	public long persist() {
		FileSystem fs = vertx.fileSystem();
		String uuid = UUID.randomUUID().toString();
		fs.writeFile("/"+uuid+".copy", new BufferImpl().appendString(sb.toString()), h->{
			if(h.failed()) {
				log.error(h.cause().getMessage());
			}
		});
		return 0;
	}

}
