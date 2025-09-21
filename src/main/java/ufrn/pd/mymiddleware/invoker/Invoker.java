package ufrn.pd.mymiddleware.invoker;

import ufrn.pd.mymiddleware.srh.protocols.RequestData;
import ufrn.pd.mymiddleware.srh.protocols.ResponseData;

import java.util.ArrayList;
import java.util.List;

// TODO : Provide the hooks for cross cutting concerns implementation (logging)
public abstract class Invoker {
    List<ExecutionHook> hooks = new ArrayList<>();
    public abstract ResponseData invoke(RequestData requestData);
}
