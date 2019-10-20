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
    static class fork {
        private Dinner owner;
        public fork(Dinner dinner) {
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

        public void eatWith(fork fork, Dinner philosopher) throws InterruptedException {
            while (isHungry) {
                // Don't have the fork, so wait patiently for other philosopher.
                if (fork.owner != this) {
                    Thread.sleep(1);
                    continue;
                }
                // If other philosopher is hungry, insist upon passing the fork.
                if (philosopher.isHungry()) {
                    System.out.println(name + ": You eat first my friend " + philosopher.getName());
                    fork.setOwner(philosopher);
                    continue;
                }
                // philosopher wasn't hungry, so finally eat
                fork.use();
                isHungry = false;
                System.out.println(name +  ": I am stuffed, my friend " + philosopher.getName());
                fork.setOwner(philosopher);
            }
        }
        //socrates
        //plato
    }
}

class Starvation extends Thread {

    class Worker {
        String name;

        public Worker(String name){
            this.name=name;
        }

        public synchronized void work() {
            while (true) {
                System.out.println(name + " is working");
            }
        }
    }

    public void run() {
        Worker worker1 = new Worker("Sheldon");
        Worker worker2 = new Worker("Leonard");
        Worker worker3 = new Worker("Raj");
        Worker worker4 = new Worker("Howard");
        Worker worker5 = new Worker("Penny");

        worker1.work();
        worker2.work();
        worker3.work();
        worker4.work();
        worker5.work();

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