import java.io.*;
import java.net.*;

public class Client {
    //String name="";
    String host = "localhost";
    int port = 8888;
    Socket socket = null;
    public static void main(String[] args) throws Exception{
        Client client = new Client();

        BufferedReader bufR = new BufferedReader(new InputStreamReader(System.in));
        while(!bufR.equals("quit")){
            String reading = bufR.readLine();
            client.SendToServer(reading);
            System.out.println("Server Said(1): "+client.RecieveFromServer());

        }
        /*client.SendToServer("Hey dude 1");
        System.out.println("Server Said(1): "+client.RecieveFromServer());
        client.SendToServer("Hey dude 2");
        System.out.println("Server Said(2): "+client.RecieveFromServer());*/
        client.close();
    }

    Client(String _host, int _port) throws Exception{
        host = _host;
        port = _port;
        socket = new Socket(host, port);
    }
    Client() throws Exception{
        socket = new Socket(host, port);
    }
    void SendToServer(String msg) throws Exception{
        //create output stream attached to socket
        PrintWriter outToServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        //send msg to server
        outToServer.print(msg + '\n');
        outToServer.flush();
    }
    String RecieveFromServer() throws Exception{
        //create input stream attached to socket
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader (socket.getInputStream()));
        //read line from server
        String res = inFromServer.readLine(); // if connection closes on server end, this throws java.net.SocketException
        return res;
    }
    void close() throws IOException{
        socket.close();
    }
}



      /*class ListenFromServer extends Thread{
          public void run(){
              while(true){
                  try{
                      String mess = (String) streamIn.readObject();
                      //gui here
                      }catch(Exception e){
                          System.out.println("Error: " + e);
                          }

                 }
            }
        }

      class ServerReceiver implements Runnable {
          public void run() {
              while (true) {
                  try {
                      clientSocket = serverSocket.accept();
                      InputStream in = clientSocket.getInputStream();
                      int data = in.read();
                  } catch (IOException io) {
                      io.printStackTrace();
                      continue;

                  }

              }
          }
      }

      class ClientReceiver implements Runnable{
        public void run(){
            while (true) {
                try {
                    clientSocket = serverSocket.accept();
                    InputStream in = clientSocket.getInputStream();
                    int data = in.read();
                } catch (IOException io) {
                    io.printStackTrace();
                    continue;

                }

            }
        }
      }

      class stillOnline extends TimerTask{
        @Override
          public void run(){
            throughSocket("Stillbreathing"+" "+clientSocket.getLocalPort(), serverSocket, ipAddress, PORT);
        }
      }*/


                       
