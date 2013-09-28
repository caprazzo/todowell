package net.caprazzi.giddone;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import static com.codahale.metrics.MetricRegistry.*;

public class Meters {

    private final static MetricRegistry metrics = new MetricRegistry();

    public static final RepoMetrics repositories = new RepoMetrics();
    public static final SystemMetrics system = new SystemMetrics();

    static {
        final JmxReporter reporter = JmxReporter.forRegistry(metrics).build();
        reporter.start();
    }

    public static class SystemMetrics {
        public static final Timer workers = metrics.timer(name(Meters.class, "workers"));
        public static final Timer cloning = metrics.timer(name(Meters.class, "cloning"));
        public static final Timer scanning = metrics.timer(name(Meters.class, "scanning"));
        public static final Timer deploying = metrics.timer(name(Meters.class, "deploying"));
        public static final Timer commentParsing = metrics.timer(name(Meters.class, "parsing", "comments"));
        public static final Timer todoParsing = metrics.timer(name(Meters.class, "parsing", "todo"));
    }

    public static class RepoMetrics {
        public static final Histogram files = metrics.histogram(name(Meters.class, "repo", "files"));
        public static final Histogram comments = metrics.histogram(name(Meters.class, "repo", "comments"));
        public static final Histogram todos = metrics.histogram(name(Meters.class, "repo", "todos"));
    }
}
