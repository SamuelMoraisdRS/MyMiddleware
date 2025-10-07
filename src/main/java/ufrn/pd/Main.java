package ufrn.pd;

import ufrn.pd.business.BM25;
import ufrn.pd.business.User;
import ufrn.pd.mymiddleware.main.MyMiddleware;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        MyMiddleware app = new MyMiddleware("grpc", 3001);
//        app.addMethods(new User());
        app.addMethods(new BM25());
        app.run();
    }
}