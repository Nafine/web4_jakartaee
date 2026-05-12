package se.ifmo.mbean;

public class MissRatio implements MissRatioMBean {

    private final DotCounterService counterService;

    public MissRatio(DotCounterService counterService) {
        this.counterService = counterService;
    }

    @Override
    public double getMissRatio() {
        long total = counterService.getTotal();
        if (total == 0) {
            return 0.0;
        }
        return (double) counterService.getMissed() / total * 100.0;
    }
}
