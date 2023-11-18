package graphql4smr.lib.deadlockdetection;

import ch.petikoch.libs.jtwfg.GraphBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockWithDeadlockDetection extends ReentrantLock {

    public final GraphBuilder<String> builder;

    public  String currentthread = null;
    private ThreadLocal<String> temp_currentthread = new ThreadLocal<>();

    public ReentrantLockWithDeadlockDetection(GraphBuilder<String> builder) {
        this.builder = builder;
        temp_currentthread.set( null);
    }

    @Override
    public void lock() {

        String thread_name =  Thread.currentThread().getName();
        temp_currentthread.set( currentthread);

        if(temp_currentthread.get() == null){
        }else{
            builder.addTaskWaitsFor(thread_name, temp_currentthread.get());
        }

        try {
            super.lock();
            currentthread = thread_name;
            if(temp_currentthread.get() == null){
            }else{
                builder.removeTaskWaitForDependency(Thread.currentThread().getName(),temp_currentthread.get());
            }
        } finally {

        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        String thread_name =  Thread.currentThread().getName();
        temp_currentthread.set( currentthread);

        if(temp_currentthread.get() == null){
        }else{
            builder.addTaskWaitsFor(thread_name, temp_currentthread.get());
        }

        try {
            super.lockInterruptibly();
            currentthread = thread_name;
        } finally {

        }
    }



    @Override
    public boolean tryLock() {
        new RuntimeException();
        return false;
    }

    @Override
    public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
        new RuntimeException();
        return false;
    }

    @Override
    public void unlock() {
        try {

            currentthread = null;
            super.unlock();
        } finally {

        }
    }
}
