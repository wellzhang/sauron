package com.feng.sauron.tracer;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年10月28日 下午6:37:36
 */

public interface Tracer {

    void beforeMethodExecute();

    void afterMethodExecute();

    void catchMethodException(Exception ex);

    void catchMethodExceptionFinally();

    void catchMethodExceptionFinally(Class<?> returnType, Object returnValue);

    String printTraceLog();
}
