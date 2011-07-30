/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf2.demo.scrum.infra.logging;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 *
 * @author Ryo
 */
@Traced @Interceptor
public class TraceLogInterceptor implements Serializable {
    
    @Inject
    transient Logger logger;

    @AroundInvoke
    public Object trace(InvocationContext ic) throws Exception {

        long start = 0;
        long end;
        Method method = ic.getMethod();
        Object[] params = ic.getParameters();

        try {
            if (params == null || params.length == 0) {
                logger.log(Level.INFO, "Start to execute {0}", method);
            } else {
                logger.log(Level.INFO, "Start to execute {0}, params = {1}", new Object[]{method, Arrays.asList(params)});
            }
            
            start = System.nanoTime();
            Object result = ic.proceed();
            end = System.nanoTime();
            if (method.getReturnType() == void.class) {
                logger.log(Level.INFO, "Execution of {0} normally in {1}", new Object[]{method, formatTimeInMs(start, end)});
            } else {
                logger.log(Level.INFO, "Execution of {0} normally in {1}, result = {2}", new Object[]{method, formatTimeInMs(start, end), result});
            }
            
            return result;
            
        } catch (Exception ex) {
            end = System.nanoTime();
            logger.log(Level.INFO, "Execution of {0} failed in {1}", new Object[]{method, formatTimeInMs(start, end)});
            logger.log(Level.SEVERE, "Exception was:", ex);
            
            throw ex;
        }
    }

    protected String formatTimeInMs(long start, long end) {
        return String.format("%,d ms", (end - start) / 1000);
    }
}
