package com.fengjr.sauron.converger.kafka.decoder;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.fengjr.sauron.commons.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fengjr.sauron.converger.kafka.handler.BaseHandler;
import com.fengjr.sauron.converger.kafka.handler.H5Handler;
import com.fengjr.sauron.converger.kafka.handler.JvmHandler;
import com.fengjr.sauron.converger.kafka.handler.PhoneDataHandler;
import com.fengjr.sauron.converger.kafka.handler.SaveLogForEsHandler;
import com.fengjr.sauron.converger.kafka.handler.SystemHandler;

@Component("logMsgDecoder")
public class LogMsgDecoder implements MsgDecoder ,InitializingBean {

	private Logger logger = LoggerFactory.getLogger(LogMsgDecoder.class);

	private static final String REGEX = "\\|";

	public static ConcurrentHashSet<String> h5DataCache = new ConcurrentHashSet<>();
	public static ConcurrentHashSet<String> appDataCache = new ConcurrentHashSet<>();
	public static ConcurrentHashSet<String> userCodeBulkCache = new ConcurrentHashSet<>();
	public static ConcurrentHashSet<String> phoneDataCache = new ConcurrentHashSet<>();
//	public static final String COLLECTION_NAME = "metrics_ori_data";
//	public static final String COLLECTION_NAME_CODEBULK = "metrics_ori_data_codebulk";
//	public static final String COLLECTION_NAME_CODEBULK_ALARM = "metrics_ori_data_codebulk_alarm";

	private final  static Map<String,BaseHandler> handlers = new HashMap<String,BaseHandler>();
	@Autowired
	@Qualifier("appHandler")
	private BaseHandler appHandler;

	@Autowired
	@Qualifier("alarmHandler")
	private BaseHandler alarmHandler;
	@Autowired
	@Qualifier("codeBulkHandler")
	private BaseHandler codeBulkHandler;
	@Autowired
	@Qualifier("phoneDataHandler")
	private BaseHandler phoneDataHandler;

	@Override
	public void afterPropertiesSet() throws Exception {
		registerHandler();
	}

	private  void registerHandler(){
		handlers.put("app",appHandler );
		handlers.put("alarm",alarmHandler);
		handlers.put("codeBulk",codeBulkHandler);
		handlers.put("jvm",new JvmHandler());
		handlers.put("phone", phoneDataHandler);
		handlers.put("traceLog",new SaveLogForEsHandler());
		handlers.put("h5",new H5Handler());
		handlers.put("system",new SystemHandler());

	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> decodeMsg(String line) throws ParseException {

		Map<String, Object> logData = new HashMap<>();
		try {
			if (line.length() > 1000) {
//				logger.info("decodeMsg 999 : {}", line.substring(0, 999));
			} else {
//				logger.info("decodeMsg : {}", line);
			}

			if (this.filterLog(line)) {

				String[] iterms = line.split(REGEX);
				String hostName = "default";
				String logtime = "";
				String log = "";
				String type = "";
				String version = "";

				if(iterms.length == 3){

					Map<String, Object> mapData = JsonUtils.getObject(iterms[2], Map.class);
					Map<String, Object> sauronMap = (Map<String, Object>) mapData.get("Sauron");
					if(sauronMap !=null && sauronMap.get("Type").equals("traceLog")) {
						handlers.get("traceLog").handle(iterms[2], iterms[0], iterms[1], "1");
					}else{
						if(logger.isDebugEnabled())
							logger.debug("skipping old data:{}",line);
					}
					return null;
				}

				if (iterms.length == 5) {
					hostName = iterms[0];
					logtime = iterms[1];
					type    = iterms[2];
					version = iterms[3];
					log = iterms[4];

					BaseHandler handler = handlers.get(type);
					if(handler == null){
						if(logger.isDebugEnabled()){
							logger.debug("unkown data type , type is:{} ,data:{}",type,log);
						}
					}else{
						handler.handle(log,hostName,logtime,version);
					}
				}else{
					if(logger.isDebugEnabled()){
						logger.debug("unkown data format ,data:{}",line);
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return logData;
	}



	public boolean filterLog(String log) {
		if (StringUtils.contains(log, "Sauron") || StringUtils.contains(log, "sauron")) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) throws Exception {
		LogMsgDecoder logMsgDecoder = new LogMsgDecoder();
		logMsgDecoder.registerHandler();
		long currentTimeMillis = System.currentTimeMillis();
		for (int i = 0; i < 1; i++) {
			String str ="127.0.0.1|2017-01-13 19:28:30|{\"Sauron\":{\"Type\":\"traceLog\",\"Version\":\"1\",\"Key\":\"pay_log.201701\",\"EsType\":\"pay_log_type\",\"Traceid\":\"68faf840-d983-11e6-92b2-2047478df540\",\"Result\":{\"agentCode\":\"ump\",\"amount\":\"4002.88\",\"bankCode\":\"PSBC\",\"cardType\":\"B2CDEBITBANK\",\"details\":[{\"endTime\":1484306910660,\"retCode\":\"1000\",\"startTime\":1484306910655,\"status\":\"true\",\"step\":\"个人充值\"}],\"endTime\":1484306910660,\"function\":\"RECHAGE_PERSONRECHARGE\",\"orderId\":\"1701131100039968539\",\"rechargeType\":\"common\",\"rechargeUserType\":\"person\",\"retCode\":\"1000\",\"retMsg\":\"成功\",\"source\":\"WEB\",\"startTime\":1484306910617,\"system\":\"biz-pay\",\"time\":43,\"userId\":\"CDEC5F07-836C-42E7-A632-E7BD9CBD6CFB\"}}}";
//			logMsgDecoder
//					.decodeMsg("127.0.0.1|2016-07-08 14:26:08|{\"Sauron\":{\"Type\":\"traceLog\",\"Version\":\"1\",\"Key\":\"financial_support_log\",\"Traceid\":\"db6629c0-44d4-11e6-83ef-f0000aff343b\",\"Result\":{\"sysCode\":null,\"performerId\":\"cca8ea3f-df10-441e-972d-999d1d12c832\",\"performerName\":\"柳永明\",\"type\":\"CREATE\",\"targetRealm\":\"LOAN_REQUEST\",\"targetId\":\"EF5A784B-65EA-4C6D-A6FF-51931EF9F6FD\",\"queryData\":null,\"currentData\":null,\"newData\":{\"id\":\"EF5A784B-65EA-4C6D-A6FF-51931EF9F6FD\",\"amount\":2,\"clientpriv\":null,\"description\":\"凤溢盈-FWF-20160708-0001\",\"employeeid\":\"cca8ea3f-df10-441e-972d-999d1d12c832\",\"guaranteeinfo\":\"凤溢盈-FWF-20160708-0001\",\"guarantystyle\":\"Guarantee\",\"hidden\":false,\"method\":\"MonthlyInterest\",\"mortgageinfo\":\"凤溢盈-FWF-20160708-0001\",\"mortgaged\":false,\"productid\":\"FE1ADE8D-73F7-405D-A5DD-C4B90C5BA334\",\"purpose\":\"SHORTTERM\",\"rate\":800,\"comprehensiverate\":0,\"reviewcomment\":null,\"riskinfo\":\"凤溢盈-FWF-20160708-0001\",\"serial\":null,\"source\":\"BACK\",\"status\":\"UNASSIGNED\",\"timesubmit\":1467959168509,\"title\":\"凤溢盈-FWF-20160708-0001\",\"userId\":\"6A4D8BDC-DA8E-45F4-A449-A2B98DDD263D\",\"days\":0,\"months\":0,\"years\":1,\"guaranteeId\":\"9DD92D8D-071E-4336-98EA-176EE10E6F18\",\"guaranteeRealm\":\"CORPORATIONUSER\",\"maxamount\":2,\"maxtimes\":100000000,\"maxtotalamount\":100000000,\"minamount\":2,\"stepamount\":2,\"entityid\":\"6A4D8BDC-DA8E-45F4-A449-A2B98DDD263D\",\"realm\":\"CORPORATION\",\"assignable\":\"NORMALASSIGNABLE\",\"subproducttype\":\"BENEFITEASSIGN\",\"penaltytype\":\"EXIST_PENALTYINTEREST\",\"receiptno\":null,\"interestbegin\":null,\"loanend\":null,\"adjustrate\":0,\"returnrate\":0},\"description\":\"创建借款请求\",\"createTime\":1467959168603}}}");// ----------

			logMsgDecoder.decodeMsg(str);

		}
		System.out.println("--------------------------------------------------------------------------------------------------------");
		System.out.println(System.currentTimeMillis() - currentTimeMillis);
	}


}
