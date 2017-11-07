package com.wangwei.cs.sauron.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface TraceMethod {
	boolean isTraceParam() default true;
}
