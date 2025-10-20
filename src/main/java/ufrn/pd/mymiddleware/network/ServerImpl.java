package ufrn.pd.mymiddleware.network;

import ufrn.pd.mymiddleware.network.protocols.ApplicationProtocol;
import ufrn.pd.mymiddleware.srh.Handler;


/**
 * This class represents the server application for each of our system's components
 * */ 
public class ServerImpl implements Server {

    // Adapter object to represent the socket interface used in this project
    private final ServerSocketAdapter socket;

    private Handler service;
    private ApplicationProtocol protocol;

    public ServerImpl(ServerSocketAdapter socket, ApplicationProtocol protocol) {
        this.socket = socket;
        this.protocol = protocol;
    }

    @Override
    public void runServer(Handler service) {
        try (socket) {
            socket.open();
            while (true) {
                socket.handleConnection(service, protocol);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}