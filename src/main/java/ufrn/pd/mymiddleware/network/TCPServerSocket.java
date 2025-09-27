package ufrn.pd.mymiddleware.network;

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

    protected void processRequest(Handler service, Socket socket) {
        try (PrintWriter socketWriter = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader socketReader = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()))) {
            // TODO : Encapsulate on a codec class
            String messageString = Codec.decodeHttpMessage(socketReader);
//            System.out.println("Recebido: " + messageString);
//            RequestPayload request = protocol.parseRequest(messageString);
            Optional<String> response = Optional.ofNullable(service.handle(messageString));
            if (response.isEmpty()) {
                return;
            }
            System.out.println("Resposta enviada" + response);
            socketWriter.println(response.get());
            System.out.println("Enviado: " + response.get());

        } catch (IOException e) {
            System.err.println("TCP Server - Error acessing socket streams: " + e.getMessage());
        }
    }

    @Override
    public void handleConnection(Handler service) {
        try {
            Socket socket = serverSocket.accept();
            this.executorService.execute(() -> processRequest(service, socket));
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
