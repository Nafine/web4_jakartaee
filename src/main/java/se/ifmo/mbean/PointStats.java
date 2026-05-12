package se.ifmo.mbean;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import java.util.concurrent.atomic.AtomicLong;

public class PointStats extends NotificationBroadcasterSupport implements PointStatsMBean {

    private static final String NOTIFICATION_TYPE = "se.ifmo.point.count.multiple.of.ten";

    private final DotCounterService counterService;
    private final AtomicLong seqNumber = new AtomicLong(0);

    public PointStats(DotCounterService counterService) {
        this.counterService = counterService;
        counterService.addMultipleOfTenListener(this::onMultipleOfTen);
    }

    private void onMultipleOfTen(long count) {
        sendNotification(new Notification(
                NOTIFICATION_TYPE,
                this,
                seqNumber.incrementAndGet(),
                System.currentTimeMillis(),
                "Total points reached " + count
        ));
    }

    @Override
    public long getTotalPoints() {
        return counterService.getTotal();
    }

    @Override
    public long getMissedPoints() {
        return counterService.getMissed();
    }
}
