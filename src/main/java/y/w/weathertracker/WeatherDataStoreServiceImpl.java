package y.w.weathertracker;

import y.w.weathertracker.measurements.model.Measurement;
import y.w.weathertracker.measurements.service.MeasurementQueryService;
import y.w.weathertracker.measurements.service.MeasurementStore;
import y.w.weathertracker.statistics.Statistic;
import y.w.weathertracker.statistics.model.AggregateResult;
import y.w.weathertracker.statistics.service.MeasurementAggregator;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static y.w.weathertracker.measurements.model.Measurement.METRIC_DEWPOINT;
import static y.w.weathertracker.measurements.model.Measurement.METRIC_PRECIPITATION;
import static y.w.weathertracker.measurements.model.Measurement.METRIC_TEMPERATURE;

@Service
public class WeatherDataStoreServiceImpl implements MeasurementQueryService, MeasurementStore, MeasurementAggregator
{
    // Use sorted TreeMap so that the measurements are sorted in ZonedDateTime natural order.
    private final Map<ZonedDateTime, Measurement> measurementStore = new TreeMap<>();

    /**
     * We will not save a measurement if it doesn't have any metrics.
     *
     * @param measurement it must has a timestamp.
     * @return
     */
    @Override
    public boolean add(Measurement measurement) {
        if ((measurement.getDewPoint() == null &&
            measurement.getPrecipitation() == null &&
            measurement.getTemperature() == null) || measurement.getTimestamp() == null)
            return false;

        measurementStore.put(measurement.getTimestamp().withNano(0), measurement);
        return true;
    }

    @Override
    public Optional<Measurement> fetch(ZonedDateTime timestamp) {
        return Optional.ofNullable(measurementStore.get(timestamp.withNano(0)));
    }

    @Override
    public List<Measurement> queryDateRange(ZonedDateTime from, ZonedDateTime to) {
        return measurementStore.entrySet().stream()
                .filter( e -> {
                    ZonedDateTime t = e.getKey();
                    return (t.isAfter(from) || t.isEqual(from)) && t.isBefore(to);
                    })
                .map(m -> m.getValue())
                .collect(Collectors.toList());
    }

    @Override
    public List<AggregateResult> analyze(List<Measurement> measurements, List<String> metrics, List<Statistic> stats) {
        List<AggregateResult> results = new ArrayList<>();

        // Returns an empty list of AggregateResult if there is nothing to do.
        if (measurements == null || metrics == null || stats == null ||
            measurements.size() == 0 || metrics.size() == 0 || stats .size() == 0)
            return results;

        for ( String m : metrics){
            DoubleSummaryStatistics summary;

            // We calculate max, min, and average now with Stream functions.
            switch (m)
            {
                case METRIC_TEMPERATURE:
                    summary = measurements.stream()
                            .filter( e-> e.getTemperature() != null )
                            .collect(Collectors.summarizingDouble(Measurement::getTemperature));
                    break;
                case METRIC_DEWPOINT:
                    summary = measurements.stream()
                            .filter( e-> e.getDewPoint() != null )
                            .collect(Collectors.summarizingDouble(Measurement::getDewPoint));
                    break;
                case METRIC_PRECIPITATION:
                    summary = measurements.stream()
                            .filter( e-> e.getPrecipitation() != null )
                            .collect(Collectors.summarizingDouble(Measurement::getPrecipitation));
                    break;
                default:
                    // This should not happen. If it does, return empty list of statistics.
                    return results;
            }

            if (summary.getCount() > 0)
            {
                for (Statistic s : stats)
                {
                    AggregateResult a = new AggregateResult(m, s, 0);
                    switch (s)
                    {
                    case MIN:
                        a.setValue(summary.getMin());
                        break;
                    case MAX:
                        a.setValue(summary.getMax());
                        break;
                    case AVERAGE:
                        a.setValue(summary.getAverage());
                        break;
                    }
                    results.add(a);
                }
            }
        }
        return results;
    }

    @Override public int count()
    {
        return measurementStore.size();
    }

    @Override public List<Measurement> fetchAll()
    {
        return measurementStore.values().stream().collect(Collectors.toList());
    }
}
