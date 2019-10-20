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
class Livelock extends Thread{
    String name1;
    String name2;

    public Livelock(String name1, String name2){
        this.name1=name1;
        this.name2=name2;
    }

    static class Fork {
        private Dinner owner;
        public Fork(Dinner dinner) {
            owner = dinner;
        }
        public Dinner getOwner() {
            return owner;
        }
        public synchronized void setOwner(Dinner dinner) {
            owner = dinner;
        }
        public synchronized void use() {
            System.out.println(owner + " has eaten.");
        }
    }

    static class Dinner {
        private String name;
        private boolean isHungry;

        public Dinner(String name) {
            this.name=name;
            this.isHungry=true;
        }
        public String getName() {
            return name;
        }

        public boolean isHungry() {
            return isHungry;
        }

        public void eatWith(Fork Fork, Dinner philosopher) throws InterruptedException {
            while (isHungry) {
                // Don't have the Fork, so wait patiently for other philosopher.
                if (Fork.owner != this) {
                    Thread.sleep(1);
                    continue;
                }
                // If other philosopher is hungry, insist upon passing the Fork.
                if (philosopher.isHungry()) {
                    System.out.println(name + ": You eat first my friend " + philosopher.getName());
                    Fork.setOwner(philosopher);
                    continue;
                }
                // philosopher wasn't hungry, so finally eat
                Fork.use();
                isHungry = false;
                System.out.println(name +  ": I am stuffed, my friend " + philosopher.getName());
                Fork.setOwner(philosopher);
            }
        }
    }

    public void run(){
        final Dinner philosopher1 = new Dinner(name1);
        final Dinner philosopher2 = new Dinner(name2);
        final Fork fork = new Fork(philosopher1);

        try {
            philosopher1.eatWith(fork, philosopher2);
        } catch (InterruptedException e) { }
    }
}
class Starvation {
    String name = Thread.currentThread().getName();;

    /*public Starvation(String name){
        //this.name=name;
    }*/

    public synchronized void starve(){
        long end = System.currentTimeMillis() + 2 * 1000;
        while (true) {
            System.out.println("Thread " + name + " is starving");
            if(System.currentTimeMillis() > end){
                System.out.println("Time passed. Ending simulation...");

                /*thread1.stop();
                thread2.stop();
                thread3.stop();
                thread4.stop();
                thread5.stop();*/

                System.exit(0);
            }
        }
    }
}

public class Simulate {
    long end = System.currentTimeMillis() + 10 * 1000; // 10 seconds * 1000 ms/sec
    //long end_s = System.currentTimeMillis() + 5 * 1000; // 5 seconds * 1000 ms/sec

    public void deadlock() {
        String resource1 = "1";
        String resource2 = "2";

        Deadlock thread1 = new Deadlock(resource1, resource2,"Thread 1");
        Deadlock thread2 = new Deadlock(resource2, resource1, "Thread 2");

        thread1.start();
        thread2.start();

        while(true){
            if(System.currentTimeMillis() > end){
                System.out.println("Time passed. Ending simulation...");

                thread1.stop();
                thread2.stop();

                System.exit(0);
            }
        }
    }
    public void livelock() {
        String philosopher1 = "Socrates";
        String philosopher2 = "Plato";

       Livelock thread1 = new Livelock(philosopher1, philosopher2);
       Livelock thread2 = new Livelock(philosopher2, philosopher1);

        thread1.start();
        thread2.start();

        while(true){
            if(System.currentTimeMillis() > end){
                System.out.println("Time passed. Ending simulation...");

                thread1.stop();
                thread2.stop();

                System.exit(0);
            }
        }
    }
    public void starvation() {
        /*Starvation thread1 = new Starvation("Sheldon");
        Starvation thread2 = new Starvation("Leonard");
        Starvation thread3 = new Starvation("Raj");
        Starvation thread4 = new Starvation("Howard");
        Starvation thread5 = new Starvation("Penny");

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();*/

        Starvation hungry = new Starvation();

        for(int i=0; i<5; i++){
            new Thread(() -> hungry.starve()).start();
        }
    }


    public static void main(String[] args) {
        //tu if ags jest odpowiednie to odpowiedni "lock" się uruchamia
        if (args.length != 1) {
            System.out.println("You must give some parameter.");
            System.exit(1);
        }
        try {
            Simulate simulate = new Simulate();
            if (args[0].equals("deadlock"))
                simulate.deadlock();
            else if (args[0].equals("livelock"))
                simulate.livelock();
            else if (args[0].equals("starvation"))
                simulate.starvation();
            else {
                System.out.println("There isn't this type of simulation.");
                System.exit(1);
            }
        } catch (Exception e) {}
    }
}