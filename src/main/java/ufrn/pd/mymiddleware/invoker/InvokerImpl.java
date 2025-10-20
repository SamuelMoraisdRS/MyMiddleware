package ufrn.pd.mymiddleware.invoker;

import ufrn.pd.mymiddleware.Marshaller;
import ufrn.pd.mymiddleware.interceptor.InterceptorManager;
import ufrn.pd.mymiddleware.lifecyclemanager.LifecycleManager;
import ufrn.pd.mymiddleware.main.RemoteMethod;
import ufrn.pd.mymiddleware.network.protocols.RequestData;
import ufrn.pd.mymiddleware.network.protocols.ResponseData;
import ufrn.pd.mymiddleware.network.protocols.ResponseStatus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InvokerImpl extends Invoker {
    private final String id;
    private final Marshaller marshaller;
    Map<String, RemoteMethod> remoteMethods;
    private final LifecycleManager lifecycleManager;
    private final InterceptorManager interceptorManager;

    public InvokerImpl(String id, Map<String, RemoteMethod> remoteMethods, Marshaller marshaller,
                       LifecycleManager lifecycleManager, InterceptorManager interceptorManager) {
        this.id = id;
        this.marshaller = marshaller;
        this.remoteMethods = remoteMethods;
        this.lifecycleManager = lifecycleManager;
        this.interceptorManager = interceptorManager;
    }

    public ResponseData invoke(RequestData requestData) {

        String methodId = String.format("%s:%s", requestData.method(), requestData.remoteMethodRoute());

        RemoteMethod remoteMethod = remoteMethods.get(methodId);
        Map<String, Class<?>> expectedParams = remoteMethod.getParameterTypes();
        List<Object> unmarshaledParams = new ArrayList<>();

        for (Map.Entry<String, Class<?>> expectedParam : expectedParams.entrySet()) {
            String paramName = expectedParam.getKey();
            Class<?> paramType = expectedParam.getValue();
            String paramValue = requestData.payload().get(paramName);
            try {
                unmarshaledParams.add(marshaller.unmarshal(paramValue, paramType));
            } catch (IllegalArgumentException e) {
                return new ResponseData(ResponseStatus.UNMARSHALLING_ERROR, String.format("Failed to convert paramater: %s to data type %s",
                        paramName, paramType.getName()));
            }
        }

        Object[] params = unmarshaledParams.toArray();

        Object remoteObject = lifecycleManager.getInstance(requestData.remoteObjectRoute());
        Method methodObject = remoteMethod.getMethodObject();
//        System.out.println("Metodo pego da instancia : " + methodObject.getName());

        InvocationContext invocationContext = new InvocationContext(requestData, remoteObject, methodId,
                requestData.remoteObjectRoute(), remoteObject);

        interceptorManager.beforeInvocation(invocationContext);

        try {
            Object methodReturn = methodObject.invoke(remoteObject, params);
            lifecycleManager.releaseInstance(requestData.remoteObjectRoute(), remoteObject);
            interceptorManager.afterInvocation(invocationContext);
            return new ResponseData(ResponseStatus.SUCCESS, marshaller.marshal(methodReturn));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
