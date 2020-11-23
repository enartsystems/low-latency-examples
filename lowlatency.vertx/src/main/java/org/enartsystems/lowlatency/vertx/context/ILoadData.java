/**
 * 
 */
package org.enartsystems.lowlatency.vertx.context;

import java.net.URL;

import io.vertx.core.Vertx;

/**
 * @author manuel
 *
 */
public interface ILoadData {

	void loadFile(Vertx vertx,URL path,IBackUp backup);
}
