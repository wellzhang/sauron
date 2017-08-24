package com.feng.sauron.client.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface TraceMethod {
	boolean isTraceParam() default true;
}
