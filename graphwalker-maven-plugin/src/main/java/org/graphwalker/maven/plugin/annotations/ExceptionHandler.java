package org.graphwalker.maven.plugin.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.METHOD)
public @interface ExceptionHandler {

    public Class<? extends Throwable> filter() default Throwable.class;
}
