package graphql4smr.lib;

import graphql4smr.lib.util.LockBuilder;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Stringwrapper implements Serializable {
    private String value;
    private Lock lock = new ReentrantLock();

    public Stringwrapper(String i) {
        this.value = i;
    }

    public Stringwrapper(String i, Class lockclass) {
        this.value = i;
        try {
            lock = (Lock) lockclass.newInstance();
        }
        catch (Exception ex) {
            new RuntimeException();
        }
    }

    public Stringwrapper(String i, LockBuilder lockBuilder) {
        this.value = i;
        this.lock = lockBuilder.build();
    }



    public String getValue(){
        return value;
    }

    public void setValue(StringChanger stringChanger) {
        lock.lock();
        this.value = stringChanger.change(this);
        lock.unlock();
    }

    public interface  StringChanger {
        public  String change(Stringwrapper stringwrapper);
    }
}
