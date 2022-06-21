/*
 * Copyright (c) 2014-2016 Janith Bandara, This source is a part of
 * Audit4j - An open source auditing framework.
 * http://audit4j.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.audit4j.integration.spring;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.audit4j.core.AuditManager;
import org.audit4j.core.dto.AnnotationAuditEvent;
import org.audit4j.core.exception.Audit4jRuntimeException;

/**
 * The Class AuditAspect.
 *
 * <pre>
 * {@code
 *
 *     <aop:aspectj-autoproxy>
 *         ...
 *         <aop:include name="auditAspect"/>
 *     </aop:aspectj-autoproxy>
 *
 *     <bean id="auditAspect" class=
"org.audit4j.integration.spring.AuditAspect" />
 *
 * }
 * </pre>
 *
 * @author <a href="mailto:janith3000@gmail.com">Janith Bandara</a>
 */
@Aspect
public class AuditAspect {

    /**
     * Audit Aspect.
     *
     * @param joinPoint the joint point
     * @throws Throwable the throwable
     */
    @Around("@within(org.audit4j.core.annotation.Audit) || @annotation(org.audit4j.core.annotation.Audit)")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        if (method.getDeclaringClass().isInterface()) {
            try {
                method = joinPoint.getTarget().getClass().getDeclaredMethod(joinPoint.getSignature().getName(), method.getParameterTypes());
            } catch (final SecurityException exception) {
                throw new Audit4jRuntimeException("Exception occured while proceding Audit Aspect in Audit4j Spring Integration",
                    exception);
            } catch (final NoSuchMethodException exception) {
                throw new Audit4jRuntimeException("Exception occured while proceding Audit Aspect in Audit4j Spring Integration",
                    exception);
            }
        }

        Object result = null;
        Throwable thrownResult = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable t) {
            thrownResult = t;
        }

        AnnotationAuditEvent event = new AnnotationAuditEvent(joinPoint.getTarget().getClass(), method, joinPoint.getArgs(), result,
            thrownResult);
        AuditManager.getInstance().audit(event);

        if (thrownResult != null) {
            throw thrownResult;
        }
        return result;
    }
}
