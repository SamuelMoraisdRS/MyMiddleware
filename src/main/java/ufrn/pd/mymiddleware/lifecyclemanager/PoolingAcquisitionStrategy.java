package ufrn.pd.mymiddleware.lifecyclemanager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PoolingAcquisitionStrategy implements InstanceAcquisitionStrategy{
    private int poolSize = 300;

    private Queue<Object> pool;

    private final ConcurrentLinkedQueue<Object> takenInstances = new ConcurrentLinkedQueue<>();

    public PoolingAcquisitionStrategy() {

    }

    @Override
    public Object activateInstance(Constructor<?> constructor) {
        if (pool == null) {
            for (int i = 0; i < poolSize; i++) {
                this.pool = new ConcurrentLinkedQueue<>();
                // Todo : Understand how to instantiate these objects
                try {
                    boolean add = pool.add(constructor.newInstance());
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        System.out.println("Tamanho do pool : " + pool.size());
        System.out.println("Tamanho do taken : " + takenInstances.size());
        if (pool.isEmpty()) {
            Object repurposedInstance = takenInstances.poll();
            pool.add(repurposedInstance);

        }
        Object takenObject = pool.poll();
        takenInstances.add(takenObject);
        return takenObject;
    }

    @Override
    public void deactivateInstance(Object instance) {
        if (takenInstances.contains(instance)) {
            takenInstances.remove(instance);
            pool.add(instance);
        }
    }
}
