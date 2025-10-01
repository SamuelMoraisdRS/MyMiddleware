package ufrn.pd.mymiddleware.network;

import ufrn.pd.mymiddleware.network.protocols.ApplicationProtocol;
import ufrn.pd.mymiddleware.srh.Handler;


/**
 * This class represents the server application for each of our system's components
 * */ 
public class ServerImpl implements Server {

    // Adapter object to represent the socket interface used in this project
    private final ServerSocketAdapter socket;

    /*
    This object will interpret the message according to the app protocol used here
     */

    private Handler service;
//    private ApplicationProtocol protocol;

    public ServerImpl(ServerSocketAdapter socket) {
        this.socket = socket;
    }

    @Override
    public void runServer(Handler service, ApplicationProtocol protocol) {
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