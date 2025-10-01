package ufrn.pd.mymiddleware.interceptor;

import ufrn.pd.mymiddleware.invoker.InvocationContext;

public interface Interceptor {
    void intercept(InvocationContext context);
}
