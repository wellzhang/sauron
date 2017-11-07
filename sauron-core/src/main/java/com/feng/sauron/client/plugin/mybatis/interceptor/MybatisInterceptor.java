package com.feng.sauron.client.plugin.mybatis.interceptor;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;

import com.feng.sauron.client.context.SauronSessionContext;
import com.feng.sauron.config.SauronConfig;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年8月31日 下午6:31:14
 * 
 */

@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
		@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }) })
public class MybatisInterceptor implements Interceptor, MybatisInterceptorTracerName {

	@SuppressWarnings("unused")
	private Properties properties;

	public Object intercept(Invocation invocation) throws Throwable {

		try {

			MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];

			String className = "";

			Configuration configuration = mappedStatement.getConfiguration();

			Object parameter = null;

			if (invocation.getArgs().length > 1) {

				parameter = invocation.getArgs()[1];
			}

			BoundSql boundSql = mappedStatement.getBoundSql(parameter);

			String sql = getSql(configuration, boundSql, false);

			String detail_sql = getSql(configuration, boundSql, true);

			String methodName = sql;

			Class<?>[] parameterTypes = new Class[] { String.class, String.class };

			Object[] paramVal = new Object[] { mappedStatement.getId(), detail_sql };

			if (SauronSessionContext.isTraceEntry()) {
				SauronSessionContext.initSessionContext();
			}

			SauronSessionContext.allocCurrentTracerAdapter(TRACERNAME_STRING, className, methodName, SauronConfig.getAPP_NAME(), parameterTypes, paramVal);
			SauronSessionContext.getCurrentTracerAdapter().beforeMethodExecute();

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			Object returnValue = invocation.proceed();// 让调用链往下执行
			try {
				SauronSessionContext.getCurrentTracerAdapter().afterMethodExecute();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return returnValue;
		} catch (Exception e) {
			try {
				SauronSessionContext.getCurrentTracerAdapter().catchMethodException(e);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			throw e;
		} finally {
			try {
				SauronSessionContext.getCurrentTracerAdapter().catchMethodExceptionFinally();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static String getSql(Configuration configuration, BoundSql boundSql, boolean isPrintParams) {
		String sql = showSql(configuration, boundSql, isPrintParams);
		StringBuilder str = new StringBuilder(200);
		str.append(sql);
		return str.toString();
	}

	private static String getParameterValue(Object obj) {
		String value = null;
		if (obj instanceof String) {
			value = "'" + obj.toString() + "'";
		} else if (obj instanceof Date) {
			DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
			value = "'" + formatter.format(new Date()) + "'";
		} else {
			if (obj != null) {
				value = obj.toString();
			} else {
				value = "";
			}
		}
		return value;
	}

	public static String showSql(Configuration configuration, BoundSql boundSql, boolean isPrintParams) {
		Object parameterObject = boundSql.getParameterObject();
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
		if (parameterMappings.size() > 0 && parameterObject != null) {
			TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
			if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
				if (isPrintParams) {
					sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));
				}
			} else {
				MetaObject metaObject = configuration.newMetaObject(parameterObject);
				for (ParameterMapping parameterMapping : parameterMappings) {
					String propertyName = parameterMapping.getProperty();
					if (metaObject.hasGetter(propertyName)) {
						Object obj = metaObject.getValue(propertyName);
						if (isPrintParams) {
							sql = sql.replaceFirst("\\?", getParameterValue(obj));
						}
					} else if (boundSql.hasAdditionalParameter(propertyName)) {
						Object obj = boundSql.getAdditionalParameter(propertyName);
						if (isPrintParams) {
							sql = sql.replaceFirst("\\?", getParameterValue(obj));
						}
					}
				}
			}
		}
		return sql;
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties properties0) {
		this.properties = properties0;
	}
}
