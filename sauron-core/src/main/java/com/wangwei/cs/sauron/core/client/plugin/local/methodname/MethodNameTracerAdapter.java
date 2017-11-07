package com.wangwei.cs.sauron.core.client.plugin.local.methodname;

import com.wangwei.cs.sauron.core.client.context.SauronSessionContext;
import com.wangwei.cs.sauron.core.client.plugin.AbstractTracerAdapterFactory;
import com.wangwei.cs.sauron.core.tracer.impl.TimerTracer;

public class MethodNameTracerAdapter extends AbstractTracerAdapterFactory implements MethodNameTracerName {

    public MethodNameTracerAdapter() {
    }

    private MethodNameTracerAdapter(String spanId, String className, String methodName, String sourceAppName, Class<?>[] paramClazz, Object[] params) {
        this.spanId = spanId;
        this.spanCount = 0;
        this.status = STATUS.SUCCESS;
        this.methodName = methodName;
        this.className = className;
        this.sourceAppName = sourceAppName;
        this.paramClazz = paramClazz;
        this.params = params;
        this.tracerPool.put(TimerTracer.class.getName(), new TimerTracer());
    }
    @Override
    public MethodNameTracerAdapter getAdapter(String spanId, String className, String methodName, String sourceAppName, Class<?>[] paramClazz, Object[] params) {
        return new MethodNameTracerAdapter(spanId, className, methodName, sourceAppName, paramClazz, params);
    }

    @Override
    public void catchMethodExceptionFinally() {
        for (String tracerName : tracerPool.keySet()) {
            if (isEnabledToTrace(tracerName)) {
                tracerPool.get(tracerName).catchMethodExceptionFinally();
            }
        }
        if (isMethodEnabledToTrace(className, methodName)) {
            logger.info(printTraceLog());
        }
        SauronSessionContext.removeTracerAdapter();
    }

    /**
     * @param className
     * @param methodName
     * @return
     */
    private boolean isMethodEnabledToTrace(String className, String methodName) {
        return true;
    }
}
