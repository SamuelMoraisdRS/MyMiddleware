package ufrn.pd.mymiddleware.main;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteMethod {
    public RemoteMethod(String route, String name, Map<String, Class<?>> params, Method methodObject) {
        this.route = route;
        this.name = name;
        this.parameters = params;
        this.methodObject = methodObject;
    }

    private final String route;
    private final String name;
    private final Map<String, Class<?>> parameters;
    private final Method methodObject;

    public void addParameter(String paramRoute, Class<?> param) {
        this.parameters.put(paramRoute, param);
    }
    public Map<String, Class<?>> getParameterTypes() {
        return new HashMap<>(this.parameters);

    }

    public String getRoute() {
        return route;
    }

    public String getName() {
        return name;
    }

    public Map<String, Class<?>> getParameters() {
        return parameters;
    }

    public Method getMethodObject() {
        return methodObject;
    }
}
