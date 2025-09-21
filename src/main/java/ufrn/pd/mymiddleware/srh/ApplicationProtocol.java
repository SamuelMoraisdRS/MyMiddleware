package ufrn.pd.mymiddleware.srh;

import ufrn.pd.mymiddleware.srh.protocols.RequestData;
import ufrn.pd.mymiddleware.srh.protocols.ResponseData;

public interface ApplicationProtocol {
    public RequestData parseRequest(String request);
    // # : Recebemos o paylaod como string pois ja sofreu serializcao pelo marshaller
    public String createResponse(ResponseData payload);
}
