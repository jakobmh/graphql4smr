package graphql4smr.lib.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class FakeReentrantLock implements Lock {
    @Override
    public void lock() {

    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        throw new RuntimeException();
    }

    @Override
    public boolean tryLock() {
        throw new RuntimeException();
    }

    @Override
    public boolean tryLock(long l, TimeUnit timeUnit) throws InterruptedException {
        throw new RuntimeException();
    }

    @Override
    public void unlock() {

    }

    @Override
    public Condition newCondition() {
        throw new RuntimeException();
    }
}
