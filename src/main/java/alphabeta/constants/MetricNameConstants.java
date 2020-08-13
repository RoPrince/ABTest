package alphabeta.constants;

/**
 * Constants for naming metrics.
 */
public final class MetricNameConstants {

    /**
     * Make sure instances can't be created.
     */
    private MetricNameConstants() {
    }

    /**
     * do-raaste metric name.
     */
    public static final String METRIC_LAYER = "doraaste.timer";

    /**
     * HTTP counter response error code metric layer name.
     */
    public static final String COUNTER_RESPONSE_METRIC_LAYER = "doraaste.counter";


    /**
     * HTTP counter response status metric layer name.
     */
    public static final String STATUS_RESPONSE_METRIC_LAYER = "status";

    public static final String ALPHA = "alpha";
    public static final String BETA = "beta";

}
