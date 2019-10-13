import java.util.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class Multi extends Thread {
    private Socket sock;
    private DataOutputStream out;
    private DataInputStream in;

    Multi(Socket sock, DataOutputStream out) throws IOException {
        this.sock = sock;
        this.out = out;
        in = new DataInputStream(sock.getInputStream());
    }

    static void print() {
        for(int i = 0; i < 8; i++) {
            System.out.print("1" + i + ": ");
            if(Server.list[i].equals("0"))
                System.out.println("Free");
            else
                System.out.println("Occupied");
        }
        System.out.println("\n");
    }
    void show() throws IOException { //pokazuje listę wolnych i zajętych miejsc od razu jak się "zaloguje" klient
        String list;
        out.writeUTF("\nReservations: ");
        for(int i = 0; i < 8; i++) {
            list = "1" + i + ": ";
            if(Server.list[i].equals("0"))
                list += "Free";
            else
                list += "Occupied";
            out.writeUTF(list);
        }
        out.writeUTF("\n");
    }

    public void send() throws IOException {
        print();
        String send;
        broadcast("\n=====================================================\n");
        broadcast("Reservations:");
        for (int i = 0; i < 8; i++) {
            send = "1" + i + ": ";
            if(Server.list[i].equals("0")) send += "Free";
            else send += "Occupied";
            broadcast(send);
        }
        broadcast("\n");
    }

    private void broadcast(String s) {
        List clients = Server.clients;
        DataOutputStream out = null;
        for(Iterator i=clients.iterator(); i.hasNext(); ) {
            out = (DataOutputStream)i.next();
            try {
                out.writeUTF(s);
            } catch (IOException e) {
                e.printStackTrace();
                //System.out.println("Failed to broadcast to client.");
                Server.removeClient(out);
            }
        }
    }

    boolean validate(int hour) throws IOException {
        if (hour < 10 || hour > 18){
            out.writeUTF("Hairdresser doesn't work at this hour.");
            return false;
        }
        return true;
    }

    public void reserve(String client) throws IOException {
        out.writeUTF("Give hour you want to reservate: ");
        String s = in.readUTF();
        int hour = Integer.parseInt(s);
        validate(hour);
        if(Server.check(hour)) {
            Server.update(hour, client);
            out.writeUTF("You reserved place for " + hour + " o'clock.");
            send();
        } else out.writeUTF("Please choose different hour, this is already occupied.");
    }
    public void decline(String client) throws IOException {
        out.writeUTF("Give hour you want to decline your reservation: ");
        String s = in.readUTF();
        int hour = Integer.parseInt(s);
        validate(hour);
        if(Server.check(hour,client)) {
            Server.update(hour);
            out.writeUTF("You reservation for " + hour + " o'clock was declined.");
            send();
        } else out.writeUTF("You didn't have reservation for this hour.");
    }

    void service(String what, String client) throws Exception {
        if(what.equals("r") || what.equals("R")){
            reserve(client);
            //jeśli r lub R to zrobić rezerwacje
        }
        else if(what.equals("d") || what.equals("D")){
            decline(client);
            //jeśli jest d lub D to usunąć rezerwacje
        }
        /*else if(what.equals("e") || what.equals("E"))
            check = true; //zakończyć program*/
        else out.writeUTF("Option you choose didn't exist. Try again.");

    }

    public void run() {
        System.out.println("Connected to " + sock.getPort() +".");
        String client = null;
        try {
            client = in.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to read client username.");
        }

        try {
            show();
            while(true) {
                out.writeUTF("You want to reserve(r) or decline(d) a service, or exit(e) a program: ");//to w pętli
                String what = in.readUTF();
                if(what.equals("e") || what.equals("E")){
                    System.out.println("Disconnecting...");
                    break;
                }
                service(what, client);

            }
        } catch (Exception e) {
            Server.removeClient(out);
        } finally {
            try {
                Server.removeClient(out);
                out.close();
                in.close();
                sock.close();
            } catch (IOException x) { }
        }
    }
}

public class Server {
    public static String[] list = new String[8];
    public static List<DataOutputStream> clients = new ArrayList<>();

    public static void initialize() {
        for(int i=0; i<8; i++)
            list[i] = "0";
    }

    static synchronized boolean check(int hour){ //_free
        if(list[hour%10].equals("0"))
            return true;
        //System.out.println(hour%10);
        return false;
    }
    static synchronized boolean check(int hour, String client){ //_client
        if(list[hour%10].equals(client))
            return true;
        //System.out.println(hour%10);
        return false;
    }

    static synchronized void update(int hour, String client) { //_reserve
        list[hour%10] = client;
    }
    static synchronized void update(int hour) { //_delete
        list[hour%10] = "0";
    }

    static synchronized void removeClient(DataOutputStream remove) {
        clients.remove(remove);
    }

    public static void main(String args[]) {
        int port = 3002;
        ServerSocket serv= null;
        try {
            serv = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to create ServerSocket.");
        }
        System.out.println("Server is running...");
        initialize();
        while (true) {
            try {
                Socket sock = serv.accept();
                DataOutputStream out = new DataOutputStream(sock.getOutputStream());
                clients.add(out);
                new Multi(sock, out).start();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to connect to client.");
            }
        }
    }
}









/*
import java.util.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.*;

class Multi extends Thread{
    Socket socket;
    ServerSocket server;
    Scanner ser;
    PrintWriter pw;

    Multi(ServerSocket server, Socket socket) {
        this.server=server;
        this.socket=socket;
    }


    public void send() throws IOException{
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(Server.list);
    }

    boolean check(int hour){ //_free
        if(Server.list[hour%10].equals("0"))
            return true;
        //System.out.println(hour%10);
        return false;
    }
    boolean check(int hour, String client){ //_client
        if(Server.list[hour%10].equals(client))
            return true;
        //System.out.println(hour%10);
        return false;
    }

    void update(int hour, String client) throws IOException { //_reserve
        Server.list[hour%10] = client;
        Server.change = true; //zmienia flagę
        //System.out.println("Test:"+Server.change);

        //check_send();
    }
    void update(int hour) throws IOException { //_delete
        Server.list[hour%10] = "0";
        Server.change = true; //zmienia flagę
        //System.out.println("Test:"+Server.change);
        //check_send();
    }

    void service(boolean what, int hour, String client) throws IOException {
        if(what){
            if(check(hour)){
                update(hour, client);
                pw.println(true);
            }
            else pw.println(false);
        }
        else{
            if(check(hour, client)){
                update(hour);
                pw.println(true);
            }
            else pw.println(false);
        }
    }

    public void run(){
        try {
            try {
                ser = new Scanner(socket.getInputStream());
                pw = new PrintWriter(socket.getOutputStream(), true);

                System.out.println("Connected to " + socket.getPort() +".");

                send();//wysła listę wolnych miejsc, ZAWSZE! - informacja o sendig- zmienić żeby było na podstawie flagi

                //zmienie flagę(trzeba dorobić) że była zmiana,
                //jeśli flaga jest zmieniona to rozsyła liste --- nowy wątek? notify all czy jakoś tak?

                String client = ser.nextLine();

                while(true){
                    //check_send();
                    boolean what = ser.nextBoolean();//to sprawdza czy klient rezerwuje czy odmawia
                    //pw.println("OK");
                    int hour = ser.nextInt();

                    service(what, hour, client);//w tym miejscu robi to co jest polecone przez what - true-rezerwacja/false-usunięcie

                    //send();
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Connection error");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Server error");
        }
    }
}

public class Server{
    public static String[] list = new String[8];
    static Socket sock;
    public static List<OutputStream> clients;

    public static void initiate(){
        for(int i=0; i<8; i++)
            list[i] = new Integer(0).toString();
    }

    public static void main(String[] args) throws IOException {
        int port = 3002;
        ServerSocket serv= new ServerSocket(port);
        initiate();
        System.out.println("Server is running...");

        //new Send().start();
        //new ObservableValue(list);

        while(true){
            sock = serv.accept();
            new Multi(serv, sock).start();
            clients = new ArrayList<OutputStream>();
            clients.add(sock.getOutputStream()); // na podstawie tej listy wysyłać wszystkim po kolei

            //https://www.dreamincode.net/forums/topic/312513-send-message-to-all-connected-clients/
            //może się przydać

            //if(change) System.out.print(".");//sendToAll(); //nie chce wysyłać

            //https://www.dreamincode.net/forums/topic/256685-sending-a-message-to-multiple-clients/
            //https://docs.oracle.com/javase/7/docs/api/java/util/Observer.html
            //https://docs.oracle.com/javase/7/docs/api/java/util/Observable.html
        }
    }
}
*/