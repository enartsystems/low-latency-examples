package org.enartsystems.lowlatency.vertx.context;

import io.vertx.core.Vertx;

public interface IBackUp {
	public void start(Vertx vertx);
	public void addRow(String s);
	public long persist();
	
}
