package ufrn.pd.mymiddleware.network;

import ufrn.pd.mymiddleware.network.protocols.ApplicationProtocol;
import ufrn.pd.mymiddleware.network.protocols.RequestData;
import ufrn.pd.mymiddleware.network.protocols.ResponseData;
import ufrn.pd.mymiddleware.srh.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServerSocket implements ServerSocketAdapter {

    ServerSocket serverSocket;
    ExecutorService executorService;
    private int THREAD_POOL_SIZE = 100;
    private int connectionPoolSize = 100;
    private int port;

    public TCPServerSocket(int port, int connectionPoolSize) {
        this.port = port;
        this.connectionPoolSize = connectionPoolSize;
    }

    // TODO : Support keep-alive
    protected void processRequest(Handler service, Socket socket, ApplicationProtocol protocol) {
        try (PrintWriter socketWriter = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader socketReader = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()))) {
            String messageString = Codec.decodeHttpMessage(socketReader);
            RequestData requestData = protocol.parseRequest(messageString);
            Optional<ResponseData> response = Optional.ofNullable(service.handle(requestData));
            if (response.isEmpty()) {
                return;
            }
            String responseRaw = protocol.createResponse(response.get());
            socketWriter.println(responseRaw);
            System.out.println("Enviado: " + response.get());
        } catch (IOException e) {
            System.err.println("TCP Server - Error acessing socket streams: " + e.getMessage());
        }
    }

    @Override
    public void handleConnection(Handler service, ApplicationProtocol protocol) {
        try {
            Socket socket = serverSocket.accept();
            this.executorService.execute(() -> processRequest(service, socket, protocol));
        } catch (IOException e) {
            System.err.println(" TCP Server - Error stablishing client connection: " + e.getMessage());
        }
    }

    @Override
    public void open() {
        try {
            serverSocket = new ServerSocket(port, connectionPoolSize);
            this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        } catch (IOException e) {
            System.err.println("TCP Server - Error binding server socket : " + e.getMessage());
        }
    }

    @Override
    public void close() throws Exception {
        serverSocket.close();
        executorService.shutdownNow();
    }
}
