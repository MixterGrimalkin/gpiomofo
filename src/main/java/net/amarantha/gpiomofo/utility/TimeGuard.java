package net.amarantha.gpiomofo.utility;

import com.google.inject.Inject;
import net.amarantha.utils.time.Now;

import java.util.HashMap;
import java.util.Map;

public class TimeGuard {

    @Inject private Now now;

    private Map<Object, Long> lastCalled = new HashMap<>();

    public void reset() {
        lastCalled.clear();
    }

    public boolean every(long interval, Object key, TimeGuardCallback callback) {
        Long last = lastCalled.get(key);
        if ( last==null || now.epochMilli()-last >= interval ) {
            callback.call();
            lastCalled.put(key, now.epochMilli());
            return true;
        }
        return false;
    }

    public interface TimeGuardCallback {
        void call();
    }
}
