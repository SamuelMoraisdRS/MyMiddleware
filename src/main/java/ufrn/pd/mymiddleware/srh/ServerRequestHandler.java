package ufrn.pd.mymiddleware.srh;

import ufrn.pd.mymiddleware.invoker.Invoker;
import ufrn.pd.mymiddleware.invoker.InvokerRegistry;
import ufrn.pd.mymiddleware.network.Server;
import ufrn.pd.mymiddleware.network.protocols.ApplicationProtocol;
import ufrn.pd.mymiddleware.network.protocols.RequestData;
import ufrn.pd.mymiddleware.network.protocols.ResponseData;

public class ServerRequestHandler implements Handler {
    private final Server server;
    private final InvokerRegistry invokerRegistry;
    // TODO : O protocolo deve estar acoplado a camada abaixo desta (camada de transporte)
    ApplicationProtocol protocol;

    // This can be as simple as seeing the name of the resource on the message
    // ex : GET /resource/1 -> invoker id = resource, method name = 1
    private String decodeInvokerId(String message) {
        if (message.equalsIgnoreCase("/")) {
            return "DEFAULT";
        }
        return message.split("/")[1];
    }

    public ServerRequestHandler(Server server, InvokerRegistry invokerRegistry) {
        this.server = server;
        this.invokerRegistry = invokerRegistry;
    }

    public ServerRequestHandler(Server server, InvokerRegistry invokerRegistry,
                                ApplicationProtocol protocol) {
        this(server, invokerRegistry);
        this.protocol = protocol;
    }

    public void run() {
        server.runServer(this,protocol);
    }

//    @Override
//    public String handle(String request) {
//        // Returns a representation containing: method and resource, the payload, the InvocationContext
//        RequestData requestData = protocol.parseRequest(request);
////        System.out.println("SRH: rota do metodo recebido" + requestData.remoteMethodRoute());
//        Invoker chosenInvoker = invokerRegistry.getInvoker(requestData.remoteObjectRoute());
//        ResponseData methodResponse = chosenInvoker.invoke(requestData);
//        return protocol.createResponse(methodResponse);
//    }

    // TODO : Gambiarra: Decomponha o metodo para tirar a chamada a camada de protocolo
    @Override
    public ResponseData handle(RequestData request) {
        // Returns a representation containing: method and resource, the payload, the InvocationContext
        Invoker chosenInvoker = invokerRegistry.getInvoker(request.remoteObjectRoute());
        return chosenInvoker.invoke(request);
    }
}
