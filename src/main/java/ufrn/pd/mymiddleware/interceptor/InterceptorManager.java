package ufrn.pd.mymiddleware.interceptor;

import ufrn.pd.mymiddleware.invoker.InvocationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO : Tornar abstract
public  class InterceptorManager {

    static enum InterceptorType {
        BEFORE_INVOCATION,
        AFTER_INVOCATION
    }

    private Map<InterceptorType, List<Interceptor>> interceptors;

    public InterceptorManager(List<Interceptor> beforeInterceptors, List<Interceptor> afterInterceptors) {

        this.interceptors = new HashMap<>();
        this.interceptors.put(InterceptorType.BEFORE_INVOCATION, beforeInterceptors);
        this.interceptors.put(InterceptorType.AFTER_INVOCATION, afterInterceptors);

    }

    public InvocationContext beforeInvocation(InvocationContext context) {
        for (Interceptor interceptor : interceptors.get(InterceptorType.BEFORE_INVOCATION)) {
            interceptor.intercept(context);
        }
        return context;
    }

    public InvocationContext afterInvocation(InvocationContext context) {
        for (Interceptor interceptor : interceptors.get(InterceptorType.BEFORE_INVOCATION)) {
            interceptor.intercept(context);
        }
        return context;
    }

}
