package ufrn.pd.business;

import ufrn.pd.mymiddleware.annotation.PathParam;
import ufrn.pd.mymiddleware.annotation.method.endpoint.Get;
import ufrn.pd.mymiddleware.annotation.type.LifeCycle;
import ufrn.pd.mymiddleware.annotation.type.RequestMapping;
import ufrn.pd.mymiddleware.lifecyclemanager.Acquisition;
import ufrn.pd.mymiddleware.lifecyclemanager.Scope;

@LifeCycle(scope = Scope.STATIC, acquisition = Acquisition.LAZY)
@RequestMapping(route = "/user")
public class User {
    @Get(route = "/")
    public String getAllUsers() {
        return "All users";
    }

    @Get(route = "/id")
    public String getUserById(@PathParam(name = "id") String id) {
        return "Retornado usuario " + id;

    }

}
