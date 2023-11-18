package graphql4smr.lib.schemawithdata;

import graphql4smr.lib.Stringwrapper;

import java.util.concurrent.locks.Lock;

public interface WriteAccess<T>{

    public T getValue();

    public void setValue(T t);

    public Lock getLock();


    default void setValue(WriteAccessInterface<T> writeAccessInterface) {
        getLock().lock();
        try {
            T temp = writeAccessInterface.readAndWrite(this.getValue());
            if(temp == null){
                throw new RuntimeException();
            }
            this.setValue(temp);
            /*
            try {
                //Thread.sleep(0,1);
                //Thread.sleep(0);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
             */
        }finally {
            getLock().unlock();
        }
    }

    public interface WriteAccessInterface<T> {
        public  T readAndWrite(T t);
    }
}
