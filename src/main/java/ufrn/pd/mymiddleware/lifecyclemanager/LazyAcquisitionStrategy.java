package ufrn.pd.mymiddleware.lifecyclemanager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class LazyAcquisitionStrategy implements InstanceAcquisitionStrategy {


    @Override
    public Object activateInstance(Constructor<?> constructor) {
        try {
            return constructor.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deactivateInstance(Object instance) {

    }
}
