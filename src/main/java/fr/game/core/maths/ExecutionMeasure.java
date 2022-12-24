package fr.game.core.maths;

public class ExecutionMeasure {

    private final long startTime;
    private long endTime;

    public ExecutionMeasure() {
        this.startTime = System.currentTimeMillis();
    }

    public void stop() {
        this.endTime = System.currentTimeMillis();
    }

    public long logTime(String label) {
        long time = endTime - startTime;
        System.out.println("Time execution for " + label + " : " + time + "ms");
        return time;
    }
}
