package ufrn.pd.mymiddleware.srh.protocols;

import ufrn.pd.mymiddleware.srh.ApplicationProtocol;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HTTPProtocol implements ApplicationProtocol {
    private static final Map<ResponseStatus, String> CODES = new HashMap<>();
    static {
        CODES.put(ResponseStatus.SUCCESS,"200 Ok");
        CODES.put(ResponseStatus.REMOTE_METHOD_NOT_FOUND, "404 Not Found");
        CODES.put(ResponseStatus.UNMARSHALLING_ERROR,"400 Bad Request");
        CODES.put(ResponseStatus.REMOTE_EXECUTION_ERROR,"500 Internal Server Error");
    }

    static enum HTTPMethod {
        GET, POST, PUT, DELETE;
    }
    private Map<String, String> parseQueryParams(String resource) {
        Map<String, String> queryParams = new HashMap<>();
        String[] params = resource.split("\\?")[1].split("&");
        // TODO Use streams
        for (String param : params) {
            String paramName = param.split("=")[0];
            String paramValue = param.split("=")[1];
            queryParams.put(paramName, paramValue);
        }
        return queryParams;
    }
    @Override
    public RequestData parseRequest(String request) {
        String[] splitMessage = request.split("\n\n");
        // Get the header info
        String header = splitMessage[0];
        String[] headerLines = header.split("\n");
        String method = headerLines[0].split(" ")[0];
        String resource = headerLines[0].split(" ")[1];
        Map<String, String> payload = new HashMap<>();
        String objectId = "";
        String methodId = "";
        if (resource.contains("?")) {
            // todo : Capture erro em que o metodo e incompativel com query parameters
            String cleanedResource = resource.split("\\?")[0];
            payload = parseQueryParams(resource);
            objectId = "/" + cleanedResource.substring(1).split("/")[0];
            methodId = "/" + cleanedResource.substring(objectId.length() + 1).split("/")[0];
        } else {
            String body = splitMessage[1];
            String[] bodyLines = body.split("\n");
            // The body is a series of parameters to the remote method call
            for (String line : bodyLines) {
                String[] lineSplit = line.split(":");
                payload.put(lineSplit[0], lineSplit[1]);
            }
            objectId = "/" + resource.substring(1).split("/")[0];
            methodId = "/" + resource.substring(objectId.length() + 1).split("/")[0];
        }
        return new RequestData(method, objectId, methodId, payload);
    }

    @Override
    public String createResponse(ResponseData message) {
        // TODO : STUB
        StringBuilder stringBuilder = new StringBuilder();
        String statusCode = CODES.get(message.responseStatus());
        stringBuilder.append("HTTP/1.1 " + statusCode + "\r\n");
        stringBuilder.append("Server: STUB\r\n");
        stringBuilder.append("Content-Type: text\r\n");
        String date = String.format("Date: %s\r\n", new Date());
        stringBuilder.append(date);

        stringBuilder.append("\r\n");

        stringBuilder.append(message.payload() + "\r\n");

        return stringBuilder.toString();

    }
}
