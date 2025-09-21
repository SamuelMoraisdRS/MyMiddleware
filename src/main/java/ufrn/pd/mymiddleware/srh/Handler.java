package ufrn.pd.mymiddleware.srh;

public interface Handler {
    public String handle(String request);
    public void run();
}
