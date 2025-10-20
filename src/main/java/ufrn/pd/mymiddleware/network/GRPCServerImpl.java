package ufrn.pd.mymiddleware.network;

import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import mymiddleware.*;
import mymiddleware.ResponseDataGRPC;
import ufrn.pd.mymiddleware.network.protocols.RequestData;
import ufrn.pd.mymiddleware.network.protocols.ResponseData;
import ufrn.pd.mymiddleware.srh.Handler;

import java.io.IOException;
import java.util.Optional;

public class GRPCServerImpl implements Server {

    private final int port;

    public GRPCServerImpl(int port) {
        this.port = port;
    }

    @Override
    public void runServer(Handler service) {
        int numAttempts = 5;
        for (int i = 1; i <= numAttempts; i++) {
            try {
                var serverBuilder = ServerBuilder.forPort(port).
                        addService(new GeneralServer(service)).build().start();
                serverBuilder.awaitTermination();
            } catch (IOException e) {
                System.err.println("ServerGRPCImpl - Error starting server: " + e.getMessage() + " - Attempt " + i);
            } catch (InterruptedException e) {
                System.err.println("ServerGRPCImpl - The Server has been shutdown: " + e.getMessage());
                return;
            }

        }
    }


    class GeneralServer extends GeneralServiceGrpc.GeneralServiceImplBase {
        private final Handler service;

        public GeneralServer(Handler service) {
            this.service = service;
        }

        @Override
        public void sendRequest(RequestDataGRPC request, StreamObserver<ResponseDataGRPC> responseObserver) {
            RequestData requestData = new RequestData(request.getMethod(), request.getRemoteObjectRoute(),
                    request.getRemoteMethodRoute(), request.getSender(), request.getPayloadMap());
            Optional<ResponseData> responseData = Optional.ofNullable(service.handle(requestData));
            if (responseData.isEmpty()) {
                System.err.println("Chegou um erro no grpc");
                var response = ResponseDataGRPC.newBuilder().setResponseStatus(ResponseStatusGRPC.REMOTE_EXECUTION_ERROR)
                        .setPayload("Error performing search").build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }

            ResponseStatusGRPC responseStatus = ResponseStatusGRPC.valueOf(responseData.get().responseStatus().
                    toString());

            var response = ResponseDataGRPC.newBuilder().setResponseStatus(responseStatus)
                    .setPayload(responseData.get().payload()).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
