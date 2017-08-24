package com.feng.sauron.client.plugin;

import java.util.HashMap;
import java.util.Map;

import com.feng.sauron.client.plugin.httpclient.httpclient4.HttpClient4Transformer;
import com.feng.sauron.client.plugin.local.annotation.AnnotationTransformer;
import com.feng.sauron.client.plugin.local.methodname.MethodNameTransformer;
import com.feng.sauron.client.plugin.mybatis.MybatisTransformer;
import com.feng.sauron.client.plugin.mybatis.interceptor.MybatisInterceptorTransformer;
import com.feng.sauron.client.plugin.mysql.MysqlTracerName;
import com.feng.sauron.client.plugin.mysql.MysqlTransformer;
import com.feng.sauron.client.plugin.redis.JedisTransformer;
import com.feng.sauron.client.plugin.rocketmq.RocketMQProducerTransformer;
import com.feng.sauron.client.plugin.spring.mvc.SpringMVCTransformer;

/**
 * @author wei.wang@fengjr.com
 * @version 2016年10月28日 下午2:13:49
 */
public class TransformerFactory {

	static Map<String, AbstractTransformer> hashMap = new HashMap<>();

	static {

		try {

			// local
			hashMap.put(MethodNameTransformer.TRACERNAME_STRING, MethodNameTransformer.class.newInstance());

			hashMap.put(AnnotationTransformer.TRACERNAME_STRING, AnnotationTransformer.class.newInstance());

			// mybatis
			hashMap.put(MybatisTransformer.TRACERNAME_STRING, MybatisTransformer.class.newInstance());

			hashMap.put(MybatisInterceptorTransformer.TRACERNAME_STRING, MybatisInterceptorTransformer.class.newInstance());// 暂时关闭

			// spring
			// hashMap.put(SpringTransformer.TRACERNAME_STRING, SpringTransformer.class.newInstance());//暂时关闭
			hashMap.put(SpringMVCTransformer.TRACERNAME_STRING, SpringMVCTransformer.class.newInstance());

			// httpclient4
			hashMap.put(HttpClient4Transformer.TRACERNAME_STRING, HttpClient4Transformer.class.newInstance());

			// rocketmq
			hashMap.put(RocketMQProducerTransformer.TRACERNAME_STRING, RocketMQProducerTransformer.class.newInstance());

			// jdbc
			hashMap.put(MysqlTracerName.TRACERNAME_STRING, MysqlTransformer.class.newInstance());

			// redis
			hashMap.put(JedisTransformer.TRACERNAME_STRING, JedisTransformer.class.newInstance());

			// jdkHttp
			// hashMap.put(JdkHttpClientTransformer.TRACERNAME_STRING, JdkHttpClientTransformer.class.newInstance());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Map<String, AbstractTransformer> getAllTransformer() {
		return hashMap;
	}

}
