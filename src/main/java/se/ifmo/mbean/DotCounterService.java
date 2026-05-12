package se.ifmo.mbean;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongConsumer;

@ApplicationScoped
public class DotCounterService {

    private final AtomicLong total = new AtomicLong(0);
    private final AtomicLong missed = new AtomicLong(0);
    private final List<LongConsumer> multipleOfTenListeners = new CopyOnWriteArrayList<>();

    public void recordPoint(boolean hit) {
        long count = total.incrementAndGet();
        if (!hit) {
            missed.incrementAndGet();
        }
        if (count % 10 == 0) {
            multipleOfTenListeners.forEach(l -> l.accept(count));
        }
    }

    public long getTotal() {
        return total.get();
    }

    public long getMissed() {
        return missed.get();
    }

    public void addMultipleOfTenListener(LongConsumer listener) {
        multipleOfTenListeners.add(listener);
    }
}
