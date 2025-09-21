package ufrn.pd.mymiddleware.invoker;

import ufrn.pd.mymiddleware.Marshaller;
import ufrn.pd.mymiddleware.lifecyclemanager.LifecycleManager;
import ufrn.pd.mymiddleware.main.RemoteMethod;
import ufrn.pd.mymiddleware.srh.protocols.RequestData;
import ufrn.pd.mymiddleware.srh.protocols.ResponseData;
import ufrn.pd.mymiddleware.srh.protocols.ResponseStatus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InvokerImpl extends Invoker{
    private final String id;
    private final Marshaller marshaller;
    Map<String, RemoteMethod> remoteMethods;
    private final LifecycleManager lifecycleManager;

    public InvokerImpl(String id, Map<String, RemoteMethod> remoteMethods, Marshaller marshaller, LifecycleManager lifecycleManager) {
        this.id = id;
        this.marshaller = marshaller;
        this.remoteMethods = remoteMethods;
        this.lifecycleManager = lifecycleManager;
    }


    // Receives a request, passes it along to the business class and returns
    // returns the serialized response
    public ResponseData invoke(RequestData requestData) {

        String methodId = String.format("%s:%s", requestData.method(), requestData.remoteMethodRoute());
        RemoteMethod remoteMethod = remoteMethods.get(methodId);
        // Expected parameter type for each named parameter
        Map<String, Class<?>> expectedParams = remoteMethod.getParameterTypes();
        List<Object> unmarshaledParams = new ArrayList<>();
        for (Map.Entry<String, Class<?>> expectedParam : expectedParams.entrySet())  {
            String paramName = expectedParam.getKey();
            Class<?> paramType = expectedParam.getValue();
            String paramValue = requestData.payload().get(paramName);
            // TODO : handle the marshalling exception
            unmarshaledParams.add(marshaller.unmarshal(paramValue, paramType));
        }
        Object[] params = unmarshaledParams.toArray();

        Object remoteObject = lifecycleManager.getInstance(requestData.remoteObjectRoute());
        Method methodObject = remoteMethod.getMethodObject();
        try {
            Object methodReturn = methodObject.invoke(remoteObject, params);
            lifecycleManager.releaseInstance(requestData.remoteObjectRoute(), remoteObject);
            return new ResponseData(ResponseStatus.SUCCESS, marshaller.marshal(methodReturn));
//            return marshaller.marshal(methodReturn);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }


        // Get the business method to be executed
        // Unmarshall the request data into the appropriate data types for the parameters
        // With the parameters at hand, fetch the remote object -> call the lifecycle manager
        // Perform the beforeInvocation actions
        // Invoke the method
        // Perform the afterInvocation actions
        // Marshall the response data the response payload format


    }
}
