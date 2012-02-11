/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 - 2012 GraphWalker
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package org.graphwalker.core.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;


/**
 * <p>LoggerAspect class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
@Aspect
public class LoggerAspect {

    private ILoggerFactory myLoggerFactory = LoggerFactory.getILoggerFactory();

    static {
        MDC.put("traceId", UUID.randomUUID().toString());
    }

    @Pointcut("execution(public * *(..))")
    void publicMethod() {}

    @Pointcut("within(org.graphwalker.core.GraphWalker+)")
    void graphWalker() {}

    /**
     * <p>graphWalkerAnnotation.</p>
     */
    @Pointcut("within(@org.graphwalker.core.annotations.GraphWalker *)")
    public void graphWalkerAnnotation() {}

    @Pointcut("within(org.graphwalker.core.machine.Machine+)")
    void machine() {}

    @Pointcut("within(org.graphwalker.core.filter.EdgeFilter+)")
    void edgeFilter() {}

    @Pointcut("within(org.graphwalker.core.conditions.StopCondition+)")
    void stopCondition() {}

    /**
     * <p>logInfoBefore.</p>
     *
     * @param joinPoint a {@link org.aspectj.lang.JoinPoint} object.
     */
    @Before("publicMethod() && graphWalker()")
    public void logInfoBefore(JoinPoint joinPoint) {
        MDC.put("traceId", UUID.randomUUID().toString());
        getLogger(joinPoint).info(joinPoint.toLongString());
    }

    /**
     * <p>logImplementation.</p>
     *
     * @param joinPoint a {@link org.aspectj.lang.JoinPoint} object.
     */
    @Before("graphWalkerAnnotation()")
    public void logImplementation(JoinPoint joinPoint) {
        getLogger(joinPoint).info(joinPoint.toLongString());
    }

    /**
     * <p>logDebug.</p>
     *
     * @param joinPoint a {@link org.aspectj.lang.JoinPoint} object.
     */
    @Before("publicMethod() && (machine() || edgeFilter() || stopCondition())")
    public void logDebug(JoinPoint joinPoint) {
        getLogger(joinPoint).debug(joinPoint.toLongString());
    }

    private Logger getLogger(JoinPoint joinPoint) {
        return myLoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType().getSimpleName());
    }

    private Logger getLogger(Throwable throwable) {
        return myLoggerFactory.getLogger(throwable.getClass().getName());
    }

/*
    @Pointcut("execution(public * *(..))")
    void publicCall() {}

    @Pointcut("within(org.graphwalker.core.algorithms.Algorithm+)")
    void algorithm() {}

    @Pointcut("within(org.graphwalker.core.algorithms.Algorithm+)")
    void annotation() {}

    @Pointcut("within(org.graphwalker.core.conditions.StopCondition+)")
    void stopCondition() {}

    @Pointcut("within(org.graphwalker.core.configuration.Configuration+)")
    void configuration() {}

    @Pointcut("within(org.graphwalker.core.generators.PathGenerator+)")
    void pathGenerator() {}

    @Pointcut("within(org.graphwalker.core.machine.Machine+)")
    void machine() {}

    @Pointcut("within(org.graphwalker.core.model.Element+)")
    void element() {}

    @Pointcut("within(org.graphwalker.core.model.Model+)")
    void model() {}

    @Pointcut("within(org.graphwalker.core.model.ModelFactory+)")
    void modelFactory() {}

    @Pointcut("within(org.graphwalker.core.GraphWalker+)")
    void graphwalker() {}

    @Before("publicCall() && graphwalker()")
    public void logInfo(JoinPoint joinPoint) {
        MDC.put("traceId", UUID.randomUUID().toString());
        getLogger(joinPoint).info(joinPoint.toLongString());
    }

    //  algorithm() || element() || model() ||
    @Before("publicCall() && (annotation() || stopCondition() || configuration() || edgeFilter() || pathGenerator() || machine() || modelFactory())")
    public void logDebug(JoinPoint joinPoint) {
        //getLogger(joinPoint).debug(joinPoint.toLongString()+":"+joinPoint.getSourceLocation().getLine());
    }
 */


    /*
    @Pointcut("execution(public * *(..))")
    void publicCall() {}

    @Pointcut("within(org.graphwalker.core.GraphWalker+)")
    void info() {}

    @Pointcut("within(org.graphwalker..*)")
    void debug() {}

    @Pointcut("within(@(@org.graphwalker.core.annotations.GraphWalker *) *)")
    public void implementation() {}

    @Before("publicCall() && (info() || implementation())")
    public void logInfo(JoinPoint joinPoint) {
        MDC.put("traceId", UUID.randomUUID().toString());
        getLogger(joinPoint).info(joinPoint.toLongString());
    }

    @Before("publicCall() && debug()")
    public void logDebug(JoinPoint joinPoint) {
        getLogger(joinPoint).debug(joinPoint.toLongString()+":"+joinPoint.getSourceLocation().getLine());
    }
    */


    /*
    @Pointcut("execution(* org.graphwalker.core.machine.Machine+.executeElement(..)) && args(element)")
    void executeElement(Element element) {}

    @Before("executeElement(element)")
    public void logExecuteElementCalls(JoinPoint joinPoint, Element element) {
        getLogger(joinPoint).info(Resource.getText(Bundle.NAME, "log.method.call", (element instanceof Edge?"EDGE":"VERTEX"), element.getId(), element.getName(), element.getVisitCount()));
    }

    @Pointcut("call(* GraphWalker.*(..))")
    void anyGraphWalkerCall() {}

    @Before("anyGraphWalkerCall()")
    public void updateTraceId(JoinPoint joinPoint) {
        getLogger(joinPoint).info(joinPoint.toLongString());
        MDC.put("traceId", UUID.randomUUID().toString());
    }

    @After("anyGraphWalkerCall()")
    public void resetTraceId(JoinPoint joinPoint) {
        getLogger(joinPoint).info(joinPoint.toShortString());
        MDC.put("traceId", "");
    }
    /*
    @Pointcut("execution(* org.graphwalker.core..*Factory.create(..))")
    void anyFactory() {}

    @Before("anyFactory()")
    public void logBeforeFactoryCalls(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        String returnType = methodSignature.getReturnType().getSimpleName();
        String factoryClass = methodSignature.getDeclaringType().getSimpleName();
        getLogger(joinPoint).info(Resource.getText(Bundle.NAME, "log.factory.create.before", factoryClass, returnType));
    }

    @AfterReturning("anyFactory()")
    public void logAfterFactoryCalls(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        String returnType = methodSignature.getReturnType().getSimpleName();
        String factoryClass = methodSignature.getDeclaringType().getSimpleName();
        getLogger(joinPoint).info(Resource.getText(Bundle.NAME, "log.factory.create.after", factoryClass, returnType));
    }

    @Pointcut("execution(* org.graphwalker.core.machine.Machine+.*(..))")
    void machine() {}

    @AfterThrowing(pointcut = "machine()", throwing = "throwable")
    public void logExceptions(Throwable throwable) {
        getLogger(throwable).info(throwable.getLocalizedMessage(), throwable);
    }

    @Pointcut("execution(* org.graphwalker.core.machine.Machine+.setRequirementStatus(..)) && args(requirement, status)")
    void setRequirementStatus(Requirement requirement, RequirementStatus status) {}

    @Before("setRequirementStatus(requirement, status)")
    public void logSetRequirementStatusCalls(JoinPoint joinPoint, Requirement requirement, RequirementStatus status) {
        if (!requirement.getStatus().equals(status)) {
            getLogger(joinPoint).info(Resource.getText(Bundle.NAME, "log.requirement", requirement.getId(), status));
        }
    }

    @Pointcut("within(@org.graphwalker.core.annotations.GraphWalker *)")
    void implementation() {}

    @Before("implementation()")
    public void logImplementationCalls(JoinPoint joinPoint) {
        getLogger(joinPoint).debug(joinPoint.toLongString());
    }
    */

}
