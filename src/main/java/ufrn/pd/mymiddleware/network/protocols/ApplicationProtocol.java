package ufrn.pd.mymiddleware.network.protocols;

public interface ApplicationProtocol {
    public RequestData parseRequest(String request);
    // # : Recebemos o paylaod como string pois ja sofreu serializcao pelo marshaller
    public String createResponse(ResponseData payload);
}
