package ufrn.pd.mymiddleware.annotation.type;

import ufrn.pd.mymiddleware.lifecyclemanager.Acquisition;
import ufrn.pd.mymiddleware.lifecyclemanager.Scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LifeCycle {
    public Scope scope() default Scope.STATIC;
    public Acquisition acquisition() default Acquisition.LAZY;
}
