package ufrn.pd.mymiddleware.lifecyclemanager;

import java.lang.reflect.Constructor;

public interface InstanceAcquisitionStrategy {
    public Object activateInstance(Constructor<?> constructor);
    public void deactivateInstance(Object instance);
}
