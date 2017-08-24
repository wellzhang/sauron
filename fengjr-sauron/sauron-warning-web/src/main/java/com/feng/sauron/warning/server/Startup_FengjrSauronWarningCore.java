package com.feng.sauron.warning.server;

/**
 * Created by bingquan.an@fengjr.com on 2015/7/03. start jetty
 */
public class Startup_FengjrSauronWarningCore extends AbstractServer {

	public Startup_FengjrSauronWarningCore(String[] anArgs) {
		super(anArgs);
	}

	public static void main(String... anArgs) throws Exception {
		try {
			new Startup_FengjrSauronWarningCore(anArgs).run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(Config config) {
	}

}
