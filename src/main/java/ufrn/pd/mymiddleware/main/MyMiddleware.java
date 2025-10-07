package ufrn.pd.mymiddleware.main;

import ufrn.pd.mymiddleware.Marshaller;
import ufrn.pd.mymiddleware.annotation.*;
import ufrn.pd.mymiddleware.annotation.method.endpoint.Get;
import ufrn.pd.mymiddleware.annotation.method.MethodAnnotation;
import ufrn.pd.mymiddleware.annotation.method.endpoint.Post;
import ufrn.pd.mymiddleware.annotation.type.LifeCycle;
import ufrn.pd.mymiddleware.annotation.type.RequestMapping;
import ufrn.pd.mymiddleware.interceptor.Interceptor;
import ufrn.pd.mymiddleware.interceptor.InterceptorManager;
import ufrn.pd.mymiddleware.interceptor.LoggingInterceptor;
import ufrn.pd.mymiddleware.invoker.Invoker;
import ufrn.pd.mymiddleware.invoker.InvokerImpl;
import ufrn.pd.mymiddleware.invoker.InvokerRegistry;
import ufrn.pd.mymiddleware.lifecyclemanager.Acquisition;
import ufrn.pd.mymiddleware.lifecyclemanager.LifecycleManager;
import ufrn.pd.mymiddleware.lifecyclemanager.LifecycleManagerImpl;
import ufrn.pd.mymiddleware.lifecyclemanager.Scope;
import ufrn.pd.mymiddleware.network.GRPCServerImpl;
import ufrn.pd.mymiddleware.network.Server;
import ufrn.pd.mymiddleware.network.ServerImpl;
import ufrn.pd.mymiddleware.network.TCPServerSocket;
import ufrn.pd.mymiddleware.srh.Handler;
import ufrn.pd.mymiddleware.srh.ServerRequestHandler;
import ufrn.pd.mymiddleware.network.protocols.HTTPProtocol;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// TODO : Vamos tentar duas abordagen. Primeiro, vamos fazer o invoker ter um map contendo os ids do metodo e sua assinatura
// alem de
public class MyMiddleware {
    // TODO : Encapsulate annotation handling
    private final List<Class<? extends Annotation>> classAnnotations = List.of(RequestMapping.class);
    private final List<Class<? extends Annotation>> methodAnnotations = List.of(Get.class, Post.class);
    private final List<Class<? extends Annotation>> paramAnnotations = List.of(PathParam.class);

    // TODO : This class should have its depedencies defined on a configuration file
    private final LifecycleManager lifecycleManager = new LifecycleManagerImpl();

    private final InvokerRegistry invokerRegistry = new InvokerRegistry();

    private Handler handler;

    private Server server;

    public MyMiddleware(String protocol, int port) {
        this.server = switch (protocol) {
            case "http" -> new ServerImpl(new TCPServerSocket(port, 100), new HTTPProtocol());
            case "grpc" -> new GRPCServerImpl(port);
            default -> null;
        };
        System.out.println("Protocolo :" + protocol);
        this.handler = new ServerRequestHandler(server, invokerRegistry);
    }
    private String getMethodId(Method method) {
        for (Class<? extends Annotation> annotationType : methodAnnotations ) {
//            if (method.isAnnotationPresent(annotationType) || annotationType.isAnnotationPresent(MethodAnnotation.class)) {
            if (method.isAnnotationPresent(annotationType)) {
                MethodAnnotation methodAnnotation = annotationType.getAnnotation(MethodAnnotation.class);
                String annotationId = methodAnnotation.id();
                // TODO Validate if it really is a method annotation
                try {
                    Method routeMethod = annotationType.getMethod("route");
                    String methodRoute = (String) routeMethod.invoke(method.getAnnotation(annotationType));

                    return String.format("%s:%s", annotationId, methodRoute);
                } catch (Exception e) {
                    throw new RuntimeException("Erro ao acessar route() em " + annotationType, e);
                }
            }
        }
//        throw new IllegalArgumentException("Method " + method.getName() + " is not annotated with a valid annotation");
        return null;
    }

    public MyMiddleware() {
    }

    public void addMethods(Object object) {
        Class<?> objectClass = object.getClass();
        System.out.println("Adding methods for " + objectClass.getName());

        // Register the empty constructor and the object instance

        // TODO :  Register the Empty constructor
        Constructor<?> emptyConstructor = null;
        try {
            emptyConstructor = objectClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        emptyConstructor.setAccessible(true);
        Optional<LifeCycle> lifeCycleAnnotation = Optional.ofNullable(objectClass.getAnnotation(LifeCycle.class));

        String objectId = objectClass.getAnnotation(RequestMapping.class).route();

        if (lifeCycleAnnotation.isPresent()) {
            Scope scope = lifeCycleAnnotation.get().scope();
            Acquisition acquisition = lifeCycleAnnotation.get().acquisition();
            lifecycleManager.registerNewInstance(objectId, emptyConstructor, acquisition, scope);
        } else {
            lifecycleManager.registerNewInstance(objectId, emptyConstructor, Acquisition.LAZY, Scope.STATIC);
        }

        // Build the unique identifier for this invoker
        System.out.println("Universal ID for this class: " + objectId);
        Map<String, RemoteMethod> remoteMethods = new HashMap<>();
        for (Method method : objectClass.getDeclaredMethods()) {
            method.setAccessible(true);
            String methodAnnotationId = getMethodId(method);
            if (methodAnnotationId == null) {
                continue;
            }
            Map<String, Class<?>> pathParams = new HashMap<>();
            Parameter[] parameters = method.getParameters();
            for (Parameter parameter : parameters) {
                if (parameter.isAnnotationPresent(PathParam.class)) {
                    String parameterAlias = parameter.getAnnotation(PathParam.class).name();
                    pathParams.put(parameterAlias, parameter.getType());
                }
            }
            System.out.println("Universal ID for this method: " + getMethodId(method));
//            String methodAnnotationId = getMethodId(method);
            RemoteMethod remoteMethod = new RemoteMethod(methodAnnotationId, method.getName(), pathParams, method);
            remoteMethods.put(methodAnnotationId, remoteMethod);
        }
        Interceptor loggingInterceptor = new LoggingInterceptor();
        InterceptorManager interceptorManager = new InterceptorManager(List.of(loggingInterceptor), List.of());
        Invoker invoker = new InvokerImpl(objectId, remoteMethods, new Marshaller(), this.lifecycleManager,
                interceptorManager);
        invokerRegistry.addInvoker(objectId, invoker);
    }

    public void run() {
        System.out.println("Middleware running");
        handler.run();
    }
}
