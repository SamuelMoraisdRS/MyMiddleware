package ufrn.pd.mymiddleware.lifecyclemanager;

import java.lang.reflect.Constructor;

public interface LifecycleManager {
    Object getInstance(String className);
    void releaseInstance(String objectId, Object instance);
    void registerNewInstance(String className, Constructor<?> instance, Acquisition acquisition, Scope scope);
    // The implementation must provide default behaviour in case of the scope and acquisition strategies arent defined
    void registerNewInstance(String className, Constructor<?> instance);
}
