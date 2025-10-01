package ufrn.pd.mymiddleware.srh;

import ufrn.pd.mymiddleware.network.protocols.RequestData;
import ufrn.pd.mymiddleware.network.protocols.ResponseData;

public interface Handler {
//    public String handle(String request);
    public ResponseData handle(RequestData request);
    public void run();
}
