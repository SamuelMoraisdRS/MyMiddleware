package ufrn.pd.mymiddleware.annotation.method.endpoint;

import ufrn.pd.mymiddleware.annotation.method.MethodAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@MethodAnnotation(id = "POST")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Post {
    public String route() default "/";
}
