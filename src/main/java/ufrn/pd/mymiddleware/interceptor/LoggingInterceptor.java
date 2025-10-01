package ufrn.pd.mymiddleware.interceptor;

import ufrn.pd.mymiddleware.invoker.InvocationContext;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggingInterceptor implements Interceptor{
    private static final Logger logger = Logger.getLogger(LoggingInterceptor.class.getName());

    public LoggingInterceptor() {

        logger.addHandler(new ConsoleHandler());
//        try {
////            FileHandler fh = new FileHandler("log.txt", true);
////            fh.setFormatter(new SimpleFormatter());
//            logger.addHandler(new ConsoleHandler());
////            logger.setUseParentHandlers(false); // evita duplicar no console
//        } catch (IOException e) {
//            throw new RuntimeException("Erro ao configurar FileHandler para logging", e);
//        }
    }

    @Override
    public void intercept(InvocationContext context) {
//        context.getInvocationInfo().put("log_id", UUID.randomUUID().toString());
//        logger.info("LoggingInterceptor: ID - " + context.getInvocationInfo().get("log_id"));
//        logger.info("Performing remote call");
//        logger.info("Remote Object route " + context.getRemoteMethodId());
//        logger.info("Remote Object ID: " + context.getRemoteObjectId());
//        logger.info("expected params" + context.getExpectedParams());
//
//
    }
}
