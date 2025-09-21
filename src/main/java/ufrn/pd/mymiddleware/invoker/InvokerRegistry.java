package ufrn.pd.mymiddleware.invoker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InvokerRegistry {
    private final Map<String, Invoker> invokers;

    public InvokerRegistry() {
        this.invokers = new ConcurrentHashMap<>();
    }

    public void addInvoker(String invokerId, Invoker invoker) {
        this.invokers.put(invokerId, invoker);
    }

    public Invoker getInvoker(String invokerId) {
        return this.invokers.get(invokerId);
    }
}
