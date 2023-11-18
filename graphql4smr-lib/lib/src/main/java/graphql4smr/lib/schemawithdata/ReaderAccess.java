package graphql4smr.lib.schemawithdata;

import java.util.concurrent.locks.Lock;

public interface ReaderAccess<T>{
    public T getValue();

    public Lock getLock();
    default void setValue(ReaderAccess.ReaderAccessInterface<T> readerAccessInterface) {
        getLock().lock();
        readerAccessInterface.readOnly(this.getValue());
        getLock().unlock();
    }

    public interface ReaderAccessInterface<T> {
        public  void readOnly(T t);
    }
}
