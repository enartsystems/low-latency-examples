/**
 * 
 */
package org.enartsystems.lowlatency.vertx;

import java.util.ServiceLoader;

import lombok.extern.slf4j.Slf4j;

/**
 * @author manuel
 *
 */
@Slf4j
public class Loader {
	public static <T> T spiLoaderGeneric(Class<T> clazz){
        ServiceLoader<T> loader = ServiceLoader.load(clazz);
        if (loader.iterator().hasNext()){
            T implement = loader.iterator().next();
            log.info("Implementation for {0} loaded: {1}", clazz.getSimpleName(), implement.getClass().getSimpleName());
            return implement;
        }
        return null;
    }
}
