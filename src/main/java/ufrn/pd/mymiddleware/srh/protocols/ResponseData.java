package ufrn.pd.mymiddleware.srh.protocols;

public record ResponseData(
        ResponseStatus responseStatus,
        String payload
) {
}
