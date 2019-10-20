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

/*
import java.net.InetAddress;
import java.util.*;
import java.io.*;
import java.net.Socket;

class Recieve extends Thread{
    Socket socket;
    String[] list;
    Scanner ser;

    Recieve(Socket socket, String[] list) throws IOException {
        this.socket=socket;
        this.list=list;
        ser = new Scanner(socket.getInputStream());
    }

    void print(){
        System.out.println("Reservations:\n");
        for(int i = 0; i < 8; i++) {
            System.out.print("1" + i + ": ");
            if(list[i].equals("0"))
                System.out.println("Free");
            else
                System.out.println("Occupied");
        }
        System.out.println();
    }

    void recieve() throws Exception {
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        list = (String[])in.readObject();//ioexception, classnotfoundexception
    }

    public void run(){
        try{
            if(ser.nextLine().equals("SEND")){
                recieve();
                System.out.println("\n==================================================\n");
                print(); //wypisanie wolych miejsc w konsoli
            }

            //Thread.sleep(3000);
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Error while recieving list");
        }
    }
}



public class Client {
    static Socket socket = null;
    static Scanner ser = null;
    static PrintWriter pw = null;
    static Scanner scan = null;
    static int reserved = 0;
    static String client = "";
    static String[] list = new String[8];
    static boolean check = false;

static void print(){
        for(int i = 0; i < 8; i++) {
            System.out.print("1" + i + ": ");
            if(list[i].equals("0"))
                System.out.println("Free");
            else
                System.out.println("Occupied");
        }
    }
static void recieve() throws Exception {
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        list = (String[])in.readObject();//ioexception, classnotfoundexception
    }


    static boolean validate(int hour){
        if (hour < 10 || hour > 18){
            System.out.println("Hairdresser doesn't work at this hour.");
            return false;
        }
        return true;
    }

    static void reserve() {
        while(true){
            while(true){ //walidacja czy reserved jes dobrą godziną
                System.out.print("Give hour you want to reservate: ");
                reserved = scan.nextInt();
                if(validate(reserved)) break;
            }

            pw.println(reserved);

            boolean free = ser.nextBoolean();
            if(free) {
                System.out.println("You reserved place for " + reserved + " o'clock.");
                break;
            } else
                System.out.println("Please choose different hour, this is already occupied.");


            try {
                recieve();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    static void decline(){
        while(true){
            while(true){ //walidacja czy reserved jes dobrą godziną
                System.out.print("Give hour you want to decline your reservation: ");
                reserved = scan.nextInt();
                if(validate(reserved)) break;
            }

            //pw.println(client);
            pw.println(reserved);

            boolean free = ser.nextBoolean();
            if(free) { //tu server wyśle czy ten klient rzeczywiście ma rezerwacje na tą godzinę
                System.out.println("You reservation for " + reserved + " o'clock was declined.");
                break;
            } else
                System.out.println("You didn't have reservation for this hour. Try again.");


            try {
                recieve();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static void service(String what) throws Exception {
        if(what.equals("r") || what.equals("R")){
            pw.println(true);
            reserve();
            //jeśli r lub R to zrobić rezerwacje
        }
        else if(what.equals("d") || what.equals("D")){
            pw.println(false);
            decline();
            //jeśli jest d lub D to usunąć rezerwacje
        }
        else if(what.equals("e") || what.equals("E"))
            check = true; //zakończyć program
        else
            System.out.println("Option you choose didn't exist. Try again.");
    }

    public static void main(String args[]) {
        try{
            Socket socket = null;

            try{
                socket = new Socket(InetAddress.getLocalHost(), 3002);

                ser = new Scanner(socket.getInputStream());
                pw = new PrintWriter(socket.getOutputStream(), true);
                scan = new Scanner(System.in);

                System.out.println("You are connected by " + socket.getLocalPort() + ".");

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("IO Exception");
                System.exit(1);
            }

            System.out.print("Give your name you want to use: ");
            client = scan.nextLine();
            pw.println(client);


            try{
                Recieve recieve = new Recieve(socket, list);
                recieve.recieve(); //pobranie listy wolnych i zajętych miejsc, ZAWSZE!
                recieve.print();
                recieve.start();
                System.out.println("Reservations:\n");
                print(); //wypisanie wolych miejsc w konsoli

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Socket read error");
                System.exit(1);
            }

            //rezerwowanie miejsca
            try{
                while(true){
                    System.out.print("You want to reserve(r) or decline(d) a service, or exit(e) a program: ");//to w pętli
                    String what = scan.nextLine();
                    service(what);
                    scan.nextLine();//czyści buffer
                    if(check){
                        System.out.println("Ending program.");
                        break;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Socket write error");
                System.exit(1);
            }

        } finally {
            try {
                pw.close();
                ser.close();
                scan.close();
                socket.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}*/