package com.fengjr.sauron.converger.server;

public class Startup_FengjrSauronConverger extends AbstractServer {

	public static void main(String... anArgs) throws Exception {
		try {
			new Startup_FengjrSauronConverger().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
