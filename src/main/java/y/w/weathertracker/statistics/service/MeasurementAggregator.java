package y.w.weathertracker.statistics.service;

import java.util.List;

import y.w.weathertracker.measurements.model.Measurement;
import y.w.weathertracker.statistics.Statistic;
import y.w.weathertracker.statistics.model.AggregateResult;

public interface MeasurementAggregator {
    /**
     * Calculate statistics based on a list of {@link Measurement}.
     *
     * @param measurements list of Measurement to be acted upon.
     * @param metrics the metrics required. This can include temperature, dewPoint, precipitation.
     * @param stats the statics required. Refer to {@link Statistic} for details.
     * @return empty list or a list of {@link AggregateResult}
     */
    List<AggregateResult> analyze(List<Measurement> measurements, List<String> metrics, List<Statistic> stats);
}
