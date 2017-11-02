package com.fengjr.sauron.converger.server;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.webapp.WebAppContext;

public abstract class AbstractServer {

	private static final String WEB_XML = "webapp/WEB-INF/web.xml";

	// private static final String PROJECT_RELATIVE_PATH_TO_WEBAPP = "src/main/webapp";

	private Server server;

	private final Config config = new Config();

	public void run() throws Exception {

		start();
		join();
	}

	public void start() throws Exception {

		server = new Server();

		server.setHandler(createHandlers());
		server.setStopAtShutdown(true);
		server.start();
	}

	public void join() throws InterruptedException {
		server.join();
	}

	public void stop() throws Exception {
		server.stop();
	}

	private HandlerCollection createHandlers() {

		WebAppContext _ctx = new WebAppContext();

		File tempDir = new File(config.temp_work_path);
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}
		_ctx.setTempDirectory(tempDir);

		_ctx.setWar(getShadedWarUrl());

		List<Handler> _handlers = new ArrayList<Handler>();

		_handlers.add(_ctx);

		HandlerList _contexts = new HandlerList();
		_contexts.setHandlers(_handlers.toArray(new Handler[1]));

		HandlerCollection _result = new HandlerCollection();

		_result.setHandlers(new Handler[] { _contexts });

		return _result;
	}

	public URL getResource(String aResource) {
		return Thread.currentThread().getContextClassLoader().getResource(aResource);
	}

	private String getShadedWarUrl() {
		String _urlStr = getResource(WEB_XML).toString();
		// Strip off "WEB-INF/web.xml"
		System.out.println("[AbstractServer.getShadedWarUrl]:url=" + _urlStr);
		return _urlStr.substring(0, _urlStr.length() - 15);
	}
}
