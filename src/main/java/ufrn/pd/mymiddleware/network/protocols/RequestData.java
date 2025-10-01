package ufrn.pd.mymiddleware.network.protocols;

import java.util.Map;

// TODO : Add the InvocationContext (Extension patterns)
public record RequestData(
        String method,
        String remoteObjectRoute,
        String remoteMethodRoute,
        String sender,
        Map<String, String> payload
) {
}
