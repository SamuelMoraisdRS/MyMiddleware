package ufrn.pd.mymiddleware.lifecyclemanager;

import java.lang.reflect.Constructor;

public class PerRequestInstance extends Instance {

    private final Constructor<?> constructor;
    private final InstanceAcquisitionStrategy acquisitionStrategy;

    public PerRequestInstance(InstanceAcquisitionStrategy acquisitionStrategy, Constructor<?> constructor) {
        this.acquisitionStrategy = acquisitionStrategy;
        this.constructor = constructor;
    }

    @Override
    public Object getInstance() {
        return acquisitionStrategy.activateInstance(constructor);
    }

    @Override
    public void releaseInstance(Object instance) {

    }
}
