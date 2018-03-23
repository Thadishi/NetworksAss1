import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Server {
    static int PORT_NUMBER;
    String FILE_PATH;
    boolean started = false;
    ServerSocket listener;
    int clientNum = 1;
    String clientName;
    List<ClientThread> list = new ArrayList<ClientThread>();
    HashMap<Integer, String> clients = new HashMap<>();

    public static void main(String[] args) {
        //System.out.println("enter port you wish to binf to");
        BufferedReader bufR = new BufferedReader(new InputStreamReader(System.in));
        //PORT_NUMBER = Integer.valueOf(args[0]);
        System.out.println("waiting for clients");
        try {
            PORT_NUMBER = Integer.parseInt(bufR.readLine());
        }catch (IOException ioe){ioe.printStackTrace();}
        new Server().start(PORT_NUMBER);
    }


    public void start(int port) {
        try {
            listener = new ServerSocket(port);
            started = true;
        } catch (BindException e1) {
            System.out.println("Port is in use.");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //synchronized (this) {
            try {
                while (started) {
                    Socket socket = listener.accept();
                    ClientThread c = new ClientThread(socket);
                    new Thread(c).start();
                    list.add(c);
                    System.out.println("Client client" + clientNum + " connected");

                    clients.put(clientNum, "Thabo");
                    clientNum++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    listener.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    class ClientThread implements Runnable {
        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private boolean bconnected;
        private int number = clientNum;
        private HashMap<Integer, String> clientList = clients;
        private String name = clientName;
        String str;

        public ClientThread(Socket socket) {
            this.socket = socket;
        }



            public synchronized void send (String str){
            try {
                out.writeObject(str);
                out.flush();
            } catch (IOException e) {
                list.remove(this);
                System.out.println("I/O exception");
            }
        }

        //send file method
        private void sendFile(String path) {
            try {
                File file = new File(path);
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);
                OutputStream os = socket.getOutputStream();

                byte[] contents;
                long fileLength = file.length();
                String fileClientString = Long.toString(fileLength);
                send(fileClientString);
                long current = 0;

                synchronized (this) {
                    while (current != fileLength) {
                        int size = 10000;
                        if (fileLength - current >= size)
                            current += size;
                        else {
                            size = (int) (fileLength - current);
                            current = fileLength;
                        }
                        contents = new byte[size];
                        bis.read(contents, 0, size);
                        os.write(contents);
                    }
                    os.flush();
                }
            } catch (IOException e) {
                list.remove(this);
                System.out.println("I/O exception");
            }
        }

        public void run() {

            try {
                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());
                //Try prompting name
                out.writeObject("What is your name: ");
                out.flush();
                //read in the name
                clientName = (String)in.readObject();

                bconnected = true;
                    while (bconnected) {
                        str = (String) in.readObject();
                        String[] temp = str.split("\\s+");
                        String method = temp[0];
                        String MOF = temp[1];
                        if (MOF.equals("file")) {
                            FILE_PATH = temp[2].substring(1, temp[2].length() - 1);
                        }
                        String message = content(str);// extract t from str
                        if (method.equals("broadcast")) {  //broadcast method
                            if (MOF.equals("message")) {
                                synchronized (this) {
                                    for (int i = 0; i < list.size(); i++) {
                                        if (i == number - 1) {
                                            continue;
                                        }
                                        ClientThread c = list.get(i);
                                        clientList.get(i);
                                        c.send("@client "+number + ":" + message);
                                    }
                                }
                            }
                            if (MOF.equals("file")) {
                                //synchronized (this) {
                                    for (int i = 0; i < list.size(); i++) {
                                        if (i == number - 1) {
                                            continue;
                                        }
                                        ClientThread c = list.get(i);
                                        c.send("file");
                                        c.send(Integer.toString(number));    //send file's size
                                        c.send(extractFileName(temp[2]));    // send file name
                                        c.sendFile(FILE_PATH);    //send file
                                    }
                            }


                            System.out.println("client" + number + " " + method + " " + MOF);
                        } else if (method.equals("private")) {  //private method
                            int target = 0;
                            if (MOF.equals("message")) {
                                String client = temp[temp.length - 1];
                                target = getTarget(client);
                                synchronized (this) {
                                    for (int i = 0; i < list.size(); i++) {
                                        if (i == target) {
                                            ClientThread c = list.get(i);
                                            c.send("@client" + number + ":" + message);
                                        }
                                    }
                                }
                            }
                            if (MOF.equals("file")) {
                                String client = temp[temp.length - 1];
                                target = getTarget(client);
                                synchronized (this) {
                                    for (int i = 0; i < list.size(); i++) {
                                        if (target == i) {
                                            ClientThread c = list.get(i);
                                            c.send("file");
                                            c.send(Integer.toString(number));
                                            c.send(extractFileName(temp[2]));
                                            c.sendFile(FILE_PATH);
                                        }

                                    }
                                }
                            }
                            int receClient = target + 1;
                            System.out.println("client" + number + " " + method + " " + MOF + " to [client" + receClient + "]");
                        } else if (method.equals("blockcast")) { //blockcast method
                            int target = 0;
                            if (MOF.equals("message")) {
                                String client = temp[temp.length - 1];
                                target = getTarget(client);
                                synchronized (this) {
                                    for (int i = 0; i < list.size(); i++) {
                                        if (target == i || i == number - 1) {
                                            continue;
                                        }
                                        ClientThread c = list.get(i);
                                        c.send("@client" + number + ":" + message);
                                    }
                                }
                            }
                            if (MOF.equals("file")) {
                                String client = temp[temp.length - 1];
                                target = getTarget(client);
                                synchronized (this) {
                                    for (int i = 0; i < list.size(); i++) {
                                        if (target == i || i == number - 1) {
                                            continue;
                                        }
                                        ClientThread c = list.get(i);
                                        c.send("file");
                                        c.send(Integer.toString(number));
                                        c.send(extractFileName(temp[2]));
                                        c.sendFile(FILE_PATH);
                                    }
                                }
                            }
                            int blockClient = target + 1;
                            System.out.println();
                        } else {
                            System.out.println("Please enter valid command");
                            out.writeUTF("Please enter a valid commad");
                            out.flush();
                        }
                    }
            } catch (EOFException e) {
                list.remove(this);
                clientNum--;
                System.out.println("Client " + this.number + " was disconnected");
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                list.remove(this);
                clientNum--;
                System.out.println("Looks like client" + this.number + " was disconnected");
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (socket != null)
                    try {
                        socket.close();
                        socket = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }

        //method to extract content
        private String content(String sentence) {
            int cursor = 0;
            while (sentence.charAt(cursor) != '\"') {
                cursor++;
            }
            int start = cursor++;

            while (sentence.charAt(cursor) != '\"') {
                cursor++;
            }
            int end = cursor;
            return sentence.substring(start + 1, end);
        }

        //get Client target
        public int getTarget(String client) {
            return (client.charAt(client.length() - 1) - '0') - 1;
        }
    }

    /*extract file name, typically there are two kinds of file name*/
    private String extractFileName(String str) {
        String[] a = str.split("/");
        int length = a.length;
        String temp = a[length - 1];
        if (temp.charAt(0) == '"') {
            return temp.substring(1, temp.length() - 1);
        } else {
            return temp.substring(0, temp.length() - 1);
        }
    }
}