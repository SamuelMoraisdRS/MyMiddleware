package ufrn.pd.mymiddleware.network;


import ufrn.pd.mymiddleware.network.protocols.ApplicationProtocol;
import ufrn.pd.mymiddleware.srh.Handler;

public interface Server {
    void runServer(Handler handler, ApplicationProtocol protocol);
}
