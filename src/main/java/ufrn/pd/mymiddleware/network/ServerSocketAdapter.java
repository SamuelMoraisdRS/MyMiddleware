package ufrn.pd.mymiddleware.network;
import ufrn.pd.mymiddleware.srh.Handler;

/**
 * Abstract adapter interface for the server socket objects used in this project
 */
public interface ServerSocketAdapter extends AutoCloseable {

     // Dont knwo if it will receive the message as string
     void handleConnection(Handler service);

     void open();

}
