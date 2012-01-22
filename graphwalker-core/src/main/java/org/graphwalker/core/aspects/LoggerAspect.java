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
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.graphwalker.core.Bundle;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Requirement;
import org.graphwalker.core.model.RequirementStatus;
import org.graphwalker.core.util.Resource;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>LoggerAspect class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
@Aspect
public class LoggerAspect {

    private ILoggerFactory myLoggerFactory = LoggerFactory.getILoggerFactory();

    @Pointcut("execution(* org.graphwalker.core..*Factory.create(..))")
    void anyFactory() {}

    @Pointcut("within(org.graphwalker.core.GraphWalker)")
    void anyGraphWalker() {}

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

    @Pointcut("execution(* org.graphwalker.core.machine.Machine+.executeElement(..)) && args(element)")
    void executeElement(Element element) {}

    @Before("executeElement(element)")
    public void logExecuteElementCalls(JoinPoint joinPoint, Element element) {
        getLogger(joinPoint).info(Resource.getText(Bundle.NAME, "log.method.call", (element instanceof Edge?"EDGE":"VERTEX"), element.getId(), element.getName(), element.getVisitCount()));
    }    

    @Pointcut("execution(* org.graphwalker.core.machine.Machine+.setRequirementStatus(..)) && args(requirement, status)")
    void setRequirementStatus(Requirement requirement, RequirementStatus status) {}

    @Before("setRequirementStatus(requirement, status)")
    public void logSetRequirementStatusCalls(JoinPoint joinPoint, Requirement requirement, RequirementStatus status) {
        if (!requirement.getStatus().equals(status)) {
            getLogger(joinPoint).info(Resource.getText(Bundle.NAME, "log.requirement", requirement.getId(), status));
        }
    }

    private Logger getLogger(JoinPoint joinPoint) {
        return myLoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType().getName());
    }

    private Logger getLogger(Throwable throwable) {
        return myLoggerFactory.getLogger(throwable.getClass().getName());
    }
}
