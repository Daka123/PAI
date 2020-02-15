import java.io.*;
import java.net.*;
import java.util.Scanner;

class Recieve extends Thread {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public Recieve(Socket socket, DataInputStream in, DataOutputStream out) {
        this.socket=socket;
        this.in=in;
        this.out=out;
    }

    public void run() {
        while (true) {
            try {
                System.out.println(in.readUTF());
            } catch (java.io.EOFException e) {
                System.out.println("Connection has been lost");
                System.exit(3);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}

public class Client {

    static Socket socket = null;
    static Scanner scan = null;
    static DataInputStream in = null;
    static DataOutputStream out = null;
    static Recieve recieve = null;
    /*static int reserved = 0;
    static String client = "";
    static String[] list = new String[8];
    static boolean check = false;*/

    public static void main(String[] args) {
        try {
            socket = new Socket(InetAddress.getLocalHost(), 3002);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            scan = new Scanner(System.in);
            recieve = new Recieve(socket, in, out);
            recieve.start();
            System.out.println("You are connected by " + socket.getLocalPort() + ".");

            System.out.print("Give name you want to use: ");
            while(true) {
                String msg = scan.nextLine();
                out.writeUTF(msg);
                if(msg.equals("e") || msg.equals("E")) {
                    System.out.println("Ending program.");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Socket write error.");
        } finally {
            if(recieve != null)
                try {
                    recieve.interrupt();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            try {
                socket.close();
                scan.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
