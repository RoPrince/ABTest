package alphabeta.service;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com. alphabeta.constants.MetricNameConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

/***
 * Captures timer, meter and histogram for method execution
 */
@Service
public class ABMetricService {

    private static final Logger logger = LoggerFactory.getLogger(ABMetricService.class);

    @Autowired
    private MetricRegistry metricRegistry;

    /***
     * This method starts capturing metric data for method calls
     * @param alphaBeta
     * @param featureKey
     * @return
     */
    public MetricWrapper startCapture(String alphaBeta, String featureKey) {
        Timer timerMethodExecution = metricRegistry.timer(MetricRegistry.name(getTimeMetricRegisterName(alphaBeta, featureKey)));
        Timer.Context context = timerMethodExecution.time();
        return new MetricWrapper(alphaBeta, featureKey, timerMethodExecution, context);
    }


    /***
     * This method updates metric with captured data
     * @param wrapper
     */
    public void finalize(MetricWrapper wrapper) {
        finalize(wrapper, null);
    }

    /***
     * This method updates metric with captured data
     * @param wrapper
     * @param exception
     */
    public void finalize(MetricWrapper wrapper, Exception exception) {
        if (!ObjectUtils.isEmpty(exception) && !ObjectUtils.isEmpty(exception.getClass())) {
            logger.info("Counter metric for '{}'", wrapper.getFeatureKey());
            Counter failureCounter = metricRegistry.counter(MetricRegistry.name(getResponseCounterMetricRegisterName(wrapper.getAlphaBeta(), wrapper.getFeatureKey(), exception.getClass().getName())));
            failureCounter.inc();
        }

        String status = (exception == null) ? "success" : "fail";

        Counter responseCounter = metricRegistry.counter(MetricRegistry.name(getResponseCounterMetricRegisterName(wrapper.getAlphaBeta(), wrapper.getFeatureKey(), status)));
        responseCounter.inc();

        long elapsed = wrapper.getContext().stop();
        TimeUnit timerPrecision = TimeUnit.NANOSECONDS;
        wrapper.getTimerMethodExecution().update(elapsed, timerPrecision);
    }

    /**
     * doraaste.timer.<alpha/beta>.<feature>
     *
     * @param alphaOrBeta
     * @param featureKey
     * @return
     */
    private String getTimeMetricRegisterName(String alphaOrBeta, String featureKey) {
        return MetricNameConstants.METRIC_LAYER.concat(".").concat(alphaOrBeta.toLowerCase()).concat(".").concat(featureKey);
    }

    /**
     * doraaste.counter.<exception class/success/fail>.<alpha/beta>.<feature>
     *
     * @param alphaBeta
     * @param featureKey
     * @param exceptionClassName
     * @return
     */
    private String getResponseCounterMetricRegisterName(String alphaBeta, String featureKey, String exceptionClassName) {
        return MetricNameConstants.COUNTER_RESPONSE_METRIC_LAYER.
                concat(".").concat(MetricNameConstants.STATUS_RESPONSE_METRIC_LAYER).
                concat(".").concat(exceptionClassName.toLowerCase()).concat(".").
                concat(alphaBeta.toLowerCase()).concat(".").concat(featureKey.toLowerCase());
    }

    public class MetricWrapper {
        private String alphaBeta;
        private String featureKey;
        private Timer timerMethodExecution;
        private Timer.Context context;

        public MetricWrapper(String alphaBeta, String featureKey, Timer timerMethodExecution, Timer.Context context) {
            this.alphaBeta = alphaBeta;
            this.featureKey = featureKey;
            this.timerMethodExecution = timerMethodExecution;
            this.context = context;
        }

        public String getAlphaBeta() {
            return alphaBeta;
        }

        public String getFeatureKey() {
            return featureKey;
        }

        public Timer getTimerMethodExecution() {
            return timerMethodExecution;
        }

        public Timer.Context getContext() {
            return context;
        }
    }

}
