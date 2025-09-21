package ufrn.pd;

import ufrn.pd.business.User;
import ufrn.pd.mymiddleware.main.MyMiddleware;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        MyMiddleware app = new MyMiddleware();
        app.addMethods(new User());
        app.run();
    }
}