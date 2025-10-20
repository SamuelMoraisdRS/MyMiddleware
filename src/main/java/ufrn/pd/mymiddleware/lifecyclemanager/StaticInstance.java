package ufrn.pd.mymiddleware.lifecyclemanager;

import java.lang.reflect.Constructor;

public class StaticInstance extends Instance {
    private final InstanceAcquisitionStrategy lazyAcquisitionStrategy = new LazyAcquisitionStrategy();

    private final Constructor<?> instanceConstructor;

    private Object staticInstance;

    public StaticInstance(Constructor<?> instanceConstructor) {
        this.instanceConstructor = instanceConstructor;
    }


    @Override
    public Object getInstance() {
        if (staticInstance == null) {
            staticInstance = lazyAcquisitionStrategy.activateInstance(instanceConstructor);
        }
        return staticInstance;
    }

    @Override
    public void releaseInstance(Object instance) {
        // Do nothing
    }
}
