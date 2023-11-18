package graphql4smr.lib.util;

import java.util.concurrent.locks.Lock;

public interface LockBuilder {
    public Lock build();
}
