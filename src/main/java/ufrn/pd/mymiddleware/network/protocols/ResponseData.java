package ufrn.pd.mymiddleware.network.protocols;

public record ResponseData(
        ResponseStatus responseStatus,
        String payload
) {
}
