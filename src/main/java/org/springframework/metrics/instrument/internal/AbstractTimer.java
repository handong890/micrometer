package org.springframework.metrics.instrument.internal;

import org.springframework.metrics.instrument.Clock;
import org.springframework.metrics.instrument.Timer;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public abstract class AbstractTimer implements Timer {
    private Clock clock;

    protected AbstractTimer(Clock clock) {
        this.clock = clock;
    }

    @Override
    public <T> T record(Callable<T> f) throws Exception {
        final long s = clock.monotonicTime();
        try {
            return f.call();
        } finally {
            final long e = clock.monotonicTime();
            record(e - s, TimeUnit.NANOSECONDS);
        }
    }

    @Override
    public void record(Runnable f) {
        final long s = clock.monotonicTime();
        try {
            f.run();
        } finally {
            final long e = clock.monotonicTime();
            record(e - s, TimeUnit.NANOSECONDS);
        }
    }

    /**
     * @return Convert a nanos to seconds without truncation.
     * @see TimeUnit#convert(long, TimeUnit)
     */
    protected static double nanosToUnit(double nanos, TimeUnit destinationUnit) {
        switch (destinationUnit) {
            case MICROSECONDS:
                return nanos / (C1 / C0);
            case MILLISECONDS:
                return nanos / (C2 / C0);
            case SECONDS:
                return nanos / (C3 / C0);
            case MINUTES:
                return nanos / (C4 / C0);
            case HOURS:
                return nanos / (C5 / C0);
            case DAYS:
                return nanos / (C6 / C0);

            case NANOSECONDS:
            default:
                return nanos;
        }
    }

    protected static double secondsToUnit(double seconds, TimeUnit destinationUnit) {
        switch (destinationUnit) {
            case NANOSECONDS:
                return seconds * (C3 / C0);
            case MICROSECONDS:
                return seconds * (C3 / C1);
            case MILLISECONDS:
                return seconds * (C3 / C2);
            case MINUTES:
                return seconds / (C4 / C3);
            case HOURS:
                return seconds / (C5 / C3);
            case DAYS:
                return seconds / (C6 / C3);

            case SECONDS:
            default:
                return seconds;
        }
    }

    private static final long C0 = 1L;
    private static final long C1 = C0 * 1000L;
    private static final long C2 = C1 * 1000L;
    private static final long C3 = C2 * 1000L;
    private static final long C4 = C3 * 60L;
    private static final long C5 = C4 * 60L;
    private static final long C6 = C5 * 24L;

    private static final long MAX = Long.MAX_VALUE;
}
