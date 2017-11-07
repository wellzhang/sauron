package com.feng.sauron.tracer.impl;

import com.feng.sauron.tracer.Tracer;

public class TimerTracer implements Tracer {
    private final static String TIMER_TRACER = "TimerTracer";
    private long startTime;
    private long endTime;

    public TimerTracer() {
        init();
    }

    public String getTracerName() {
        return TIMER_TRACER;
    }

    @Override
    public void beforeMethodExecute() {
        startTimer();
    }

    @Override
    public void afterMethodExecute() {

    }

    @Override
    public void catchMethodException(Exception ex) {

    }

    @Override
    public void catchMethodExceptionFinally() {
        stopTimer();
    }

    public void catchDubboMethodExceptionFinally() {
        stopTimer();
    }

    @Override
    public void catchMethodExceptionFinally(Class<?> returnType, Object returnValue) {
        stopTimer();
    }

    @Override
    public String printTraceLog() {
        StringBuilder traceLog = new StringBuilder();
        traceLog.append("\"time\":\"").append(getDuration()).append("\"");
        return traceLog.toString();
    }

    public void startTimer() {
        this.startTime = System.currentTimeMillis();
        this.endTime = 0;
    }

    public void stopTimer() {
        this.endTime = System.currentTimeMillis();
    }

    public long getDuration() {
        return endTime - startTime;
    }

    public void init() {
        this.startTime = 0;
        this.endTime = 0;
    }

}
