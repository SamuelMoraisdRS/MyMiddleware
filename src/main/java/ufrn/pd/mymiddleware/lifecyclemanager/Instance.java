package ufrn.pd.mymiddleware.lifecyclemanager;

public abstract class Instance {
    protected InstanceAcquisitionStrategy acquisitionStrategy;

    abstract public Object getInstance();
    abstract public void releaseInstance(Object instance);
}
