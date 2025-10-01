package ufrn.pd.mymiddleware.invoker;

import ufrn.pd.mymiddleware.network.protocols.RequestData;

import java.util.HashMap;
import java.util.Map;

public class InvocationContext {
    private RequestData requestData;
    private Object remoteObject;
    private String remoteMethodId;
    private String remoteObjectId;
    private Object instance;

    Map<String, Class<?>> expectedParams;

    private final Map<String, String> invocationInfo = new HashMap<>();

    public InvocationContext(RequestData requestData, Object remoteObject, String remoteMethodId, String remoteObjectId, Object instance) {
        this.requestData = requestData;
        this.remoteObject = remoteObject;
        this.remoteMethodId = remoteMethodId;
        this.remoteObjectId = remoteObjectId;
        this.instance = instance;
    }

    public void addInvocationInfo(String key, String value) {
        this.invocationInfo.put(key, value);
    }

    public String getInvocationInfo(String key) {
        return (this.invocationInfo.get(key));
    }

    public RequestData getRequestData() {
        return this.requestData;
    }

    public void setRequestData(RequestData requestData) {
        this.requestData = requestData;
    }

    public Object getRemoteObject() {
        return remoteObject;
    }

    public void setRemoteObject(Object remoteObject) {
        this.remoteObject = remoteObject;
    }

    public String getRemoteMethodId() {
        return remoteMethodId;
    }

    public void setRemoteMethodId(String remoteMethodId) {
        this.remoteMethodId = remoteMethodId;
    }

    public String getRemoteObjectId() {
        return remoteObjectId;
    }

    public void setRemoteObjectId(String remoteObjectId) {
        this.remoteObjectId = remoteObjectId;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Map<String, Class<?>> getExpectedParams() {
        return expectedParams;
    }

    public void setExpectedParams(Map<String, Class<?>> expectedParams) {
        this.expectedParams = expectedParams;
    }

    public Map<String, String> getInvocationInfo() {
        return invocationInfo;
    }
}
