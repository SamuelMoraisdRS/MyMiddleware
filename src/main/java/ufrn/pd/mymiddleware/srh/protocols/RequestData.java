package ufrn.pd.mymiddleware.srh.protocols;

import java.util.Map;

// TODO : Add the InvocationContext
public record RequestData(
        String method,
        String remoteObjectRoute,
        String remoteMethodRoute,
        Map<String, String> payload
) {
}
