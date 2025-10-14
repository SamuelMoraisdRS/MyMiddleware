package ufrn.pd.mymiddleware.lifecyclemanager;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LifecycleManagerImpl implements LifecycleManager {

    // TODO : Create an object identifier class or alias to use here
    private final Map<String, Instance> lookupTable = new ConcurrentHashMap<String, Instance>();

    public LifecycleManagerImpl() {
    }

    @Override
    public Object getInstance(String className) {
        return lookupTable.get(className).getInstance();
    }

    // TODO  : Evaluate if this is necessary
    @Override
    public void releaseInstance(String objectId, Object instance) {
        lookupTable.get(objectId).releaseInstance(instance);
    }

    @Override
    public void registerNewInstance(String className, Constructor<?> instanceConstructor,
                                    Acquisition acquisition, Scope scope) {
        InstanceAcquisitionStrategy acquisitionStrategy = switch (acquisition) {
            case LAZY -> new LazyAcquisitionStrategy();
            default -> new PoolingAcquisitionStrategy(instanceConstructor);
        };
        Instance instance = switch (scope) {
            case STATIC -> new StaticInstance(instanceConstructor);
            case PER_REQUEST -> new PerRequestInstance( acquisitionStrategy, instanceConstructor);
        };
        this.lookupTable.put(className, instance);
    }

    @Override
    public void registerNewInstance(String className, Constructor<?> instance) {

    }
}
