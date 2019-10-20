class Deadlock extends Thread {
    String resource1;
    String resource2;
    String thread;

    public Deadlock(String resource1, String resource2, String thread){
        this.resource1=resource1;
        this.resource2=resource2;
        this.thread=thread;
    }

    public void run() {
        while (true) {
            try {
                synchronized(resource1) {
                    System.out.println(thread + ": locked resource " + resource1);
                    Thread.sleep(100);
                    synchronized(resource2) {
                        System.out.println(thread + ": locked resource " + resource2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


public class Simulate {


    public static void main(String[] args) {
        //tu if ags jest odpowiednie to odpowiedni "lock" uruchamia

        long start = System.currentTimeMillis();
        long end = start + 60*1000; // 60 seconds * 1000 ms/sec
        //odpale
        while (System.currentTimeMillis() < end)
        {
            // run
            // usunę wątek
        }

    }

}