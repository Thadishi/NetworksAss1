import java.net.InetAddress;

public class ClientDetails {
    private String name;
    private int port;
    private InetAddress ipAddress;

    public ClientDetails(String name){
        this.name = name;
        this.port = port;
        //ipAddress = ip;
    }

    public String getName(){
        return name;
    }
    public int getPort(){
        return port;
    }
    public InetAddress getIP(){
        return ipAddress;
    }
}
