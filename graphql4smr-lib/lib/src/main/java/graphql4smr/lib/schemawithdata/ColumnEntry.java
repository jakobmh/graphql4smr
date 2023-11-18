package graphql4smr.lib.schemawithdata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ColumnEntry implements Map<String,String> {

    private Map<String,String> data = new HashMap();

    transient
    private Lock entryLock = new ReentrantLock();

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return data.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return data.containsValue(value);
    }

    @Override
    public String get(Object key) {
        return data.get(key);
    }

    @Override
    public String put(String key, String value) {
        return data.put(key,value);
    }

    @Override
    public String remove(Object key) {
        return data.remove(key);
    }

    @Override
    public void putAll(Map m) {
        data.putAll(m);
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public Set keySet() {
        return data.keySet();
    }

    @Override
    public Collection values() {
        return data.values();
    }

    @Override
    public Set<Entry<String,String>> entrySet() {
        return data.entrySet();
    }
}
