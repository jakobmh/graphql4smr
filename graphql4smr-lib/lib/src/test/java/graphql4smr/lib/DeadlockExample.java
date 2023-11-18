package graphql4smr.lib;

import ch.petikoch.libs.jtwfg.*;
import com.google.common.util.concurrent.CycleDetectingLockFactory;
import graphql4smr.lib.deadlockdetection.ReentrantLockWithDeadlockDetection;

import java.util.concurrent.locks.ReentrantLock;

public class DeadlockExample {

    public static void main(String[] args) {
        CycleDetectingLockFactory factory = CycleDetectingLockFactory.newInstance(CycleDetectingLockFactory.Policies.THROW);
        //ReentrantLock lock1 = factory.newReentrantLock("lock1");


        //ReentrantLock lock2 = factory.newReentrantLock("lock2");
        GraphBuilder<String> builder = new GraphBuilder<>();


        ReentrantLock lock1 = new ReentrantLockWithDeadlockDetection(builder);
        ReentrantLock lock2 = new ReentrantLockWithDeadlockDetection(builder);

        /*
        builder.addTaskWaitsFor("thread 1", "thread 2");
        builder.addTaskWaitsFor("thread 2", "thread 1");
        //builder.removeTaskWaitForDependency("task 1", "task 2");
        builder.addTask("thread 1");
        builder.addTask("thread 2");

        builder.removeTask("thread 1");
         */




        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {

                System.out.println(Thread.currentThread());
                String thread_name  = "thread 1 ";
                ReentrantLock lock_outer = lock1;
                ReentrantLock lock_inner = lock2;

                try {
                    lock_outer.lockInterruptibly();
                    System.out.println(thread_name + "lock_outer.lock()");
                    SleepUtil.delaystatic();
                    try {
                        lock_inner.lockInterruptibly();
                        System.out.println(thread_name + "lock_inner.lock()");
                        try {
                            System.out.println(thread_name + "success");
                        } finally {
                            lock_inner.unlock();
                        }
                    } catch (CycleDetectingLockFactory.PotentialDeadlockException e) {
                        System.out.println(thread_name + "PotentialDeadlockException inner");
                    } finally {
                        lock_outer.unlock();
                    }
                } catch (CycleDetectingLockFactory.PotentialDeadlockException e) {
                    System.out.println(thread_name + "PotentialDeadlockException outer");
                } catch (InterruptedException e) {
                    System.out.println(thread_name + "InterruptedException outer");
                }
            }
        });




        thread1.start();


        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {

                System.out.println(Thread.currentThread());
                String thread_name  = "thread 2 ";
                ReentrantLock lock_outer = lock2;
                ReentrantLock lock_inner = lock1;
                SleepUtil.delayrandom();
                try {
                    lock_outer.lockInterruptibly();
                    System.out.println(thread_name + "lock_outer.lock()");
                    SleepUtil.delaystatic();
                    try {
                        lock_inner.lockInterruptibly();
                        System.out.println(thread_name + "lock_inner.lock()");
                        try {
                            System.out.println(thread_name + "success");
                        } finally {
                            lock_inner.unlock();
                        }
                    } catch (CycleDetectingLockFactory.PotentialDeadlockException e) {
                        System.out.println(thread_name + "PotentialDeadlockException inner");
                    }  finally {
                        lock_outer.unlock();
                    }

                } catch (CycleDetectingLockFactory.PotentialDeadlockException e) {
                    System.out.println(thread_name + "PotentialDeadlockException outer");
                } catch (InterruptedException e) {
                    System.out.println(thread_name + "InterruptedException outer");
                }
            }
        });

        thread2.start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    Graph<String> graph = builder.build();
                    DeadlockDetector<String> deadlockDetector = new DeadlockDetector<>();
                    DeadlockAnalysisResult<String> analyzisResult = deadlockDetector.analyze(graph);
                    if(analyzisResult.hasDeadlock()){
                        // do something in your domain like throwing an exception or killing a task or ...
                        //

                    for (DeadlockCycle<String> deadlockCycle : analyzisResult.getDeadlockCycles()) {
                        System.out.println(deadlockCycle);
                        //System.out.println();

                        String stopthread = (String)deadlockCycle.getAllDeadlockedTasks().toArray()[0];

                        if(thread1.getName().equals(stopthread)){
                            thread1.interrupt();
                        }
                        if(thread2.getName().equals(stopthread)){
                            thread2.interrupt();
                        }
                    }

                        //System.out.println(analyzisResult);

                        //thread1.interrupt();

                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if(!(thread1.isAlive() && thread2.isAlive())){
                        break;
                    }
                }

            }
        }).start();


    }
}
