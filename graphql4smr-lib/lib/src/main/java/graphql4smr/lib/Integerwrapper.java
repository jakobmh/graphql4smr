package graphql4smr.lib;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Integerwrapper implements Serializable {
    private Integer value;
    private Lock lock = new ReentrantLock();

    public Integerwrapper(int i) {
        this.value = i;
    }

    public Integerwrapper(int i, Class lockclass) {
        this.value = i;
        try {
            lock = (Lock) lockclass.newInstance();
        }
        catch (Exception ex) {
            new RuntimeException();
        }
    }

    public Integer getValue(){
        return value;
    }

    public void setValue(IntegerChanger integerChanger) {
        lock.lock();
        this.value = integerChanger.change(this);
        lock.unlock();
    }

    public interface  IntegerChanger{
        public  Integer change(Integerwrapper integerwrapper);
    }
}
