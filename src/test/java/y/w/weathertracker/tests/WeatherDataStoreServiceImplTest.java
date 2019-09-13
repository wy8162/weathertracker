package y.w.weathertracker.tests;

import y.w.weathertracker.WeatherDataStoreServiceImpl;
import y.w.weathertracker.measurements.model.Measurement;
import y.w.weathertracker.statistics.Statistic;
import y.w.weathertracker.statistics.model.AggregateResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.json.JacksonTester;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test WeatherDataStoreService. No dependency on Spring
 */
@Slf4j
public class WeatherDataStoreServiceImplTest
{
    // Format: 2015-09-01T16:00:00.000Z
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    private WeatherDataStoreServiceImpl service;
    private List<Measurement>           measurementsCreated;
    private List<ZonedDateTime>         weatherSamplingTimeSlots;
    private List<Double>                valuesPredefined;

    private JacksonTester<AggregateResult> json;

    @Before
    public void setup()
    {
        JacksonTester.initFields(this, new ObjectMapper());

        service = new WeatherDataStoreServiceImpl();

        weatherSamplingTimeSlots = Arrays.asList(
                ZonedDateTime.parse("2015-09-01T16:00:00.000Z", dateTimeFormatter),
                ZonedDateTime.parse("2015-09-01T16:01:00.000Z", dateTimeFormatter),
                ZonedDateTime.parse("2015-09-01T16:02:00.000Z", dateTimeFormatter),
                ZonedDateTime.parse("2015-09-01T16:03:00.000Z", dateTimeFormatter),
                ZonedDateTime.parse("2015-09-01T16:04:00.000Z", dateTimeFormatter),
                ZonedDateTime.parse("2015-09-01T16:05:00.000Z", dateTimeFormatter),
                ZonedDateTime.parse("2015-09-01T16:06:00.000Z", dateTimeFormatter)
        );

        valuesPredefined = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0);

        measurementsCreated = new ArrayList<>();

        for (int i=0; i<weatherSamplingTimeSlots.size(); i++)
        {
            Measurement m = Measurement.builder()
                    .timestamp(weatherSamplingTimeSlots.get(i))
                    .temperature(valuesPredefined.get(i))
                    .dewPoint(valuesPredefined.get(i))
                    .precipitation(valuesPredefined.get(i))
                    .build();
            service.add(m);
            measurementsCreated.add(m);
        }
    }

    @Test
    public void addAndFetchTest()
    {
        ZonedDateTime t = ZonedDateTime.now().withNano(0);

        Measurement measurement = Measurement.builder()
                .timestamp(t)
                .temperature(35.0)
                .dewPoint(1.0)
                .precipitation(2.0)
                .build();

        int count = service.count();

        // Given the service is called
        service.add(measurement);

        assertThat(service.count()).isEqualTo(count + 1);
        assertThat(service.fetch(t)).containsSame(measurement);
    }

    @Test
    public void invalidMeasurementNotAddedTest()
    {
        service.add(Measurement.builder().build());
        int count = service.count();

        // count not changed
        assertThat(service.count()).isEqualTo(count);
    }

    @Test
    public void queryDateRangeTest()
    {
        // We got 7 Measurements
        assertThat(service.count()).isEqualTo(7);

        List<Measurement> results = service.queryDateRange(weatherSamplingTimeSlots.get(1), weatherSamplingTimeSlots.get(4));

        assertThat(results.size()).isEqualTo(3);
        assertThat(results).isSubsetOf(measurementsCreated);
    }

    @Test
    public void analyzeEmptyDataResultsInEmptyResultTest()
    {
        List<AggregateResult> l = service.analyze(null, null, null);

        assertThat(l).isEmpty();

        l = service.analyze(measurementsCreated, null, null);

        assertThat(l).isEmpty();
    }

    @Test
    public void analyzeOneMeasurementTest()
    {
        List<AggregateResult> l = service.analyze(
                Arrays.asList(measurementsCreated.get(1)),
                Arrays.asList(Measurement.METRIC_DEWPOINT, Measurement.METRIC_TEMPERATURE),
                Arrays.asList(Statistic.MIN, Statistic.MAX)
                );

        // There must be 4 results
        assertThat(l).hasSize(4);

        l = service.analyze(
                Arrays.asList(measurementsCreated.get(1)),
                Arrays.asList(Measurement.METRIC_DEWPOINT),
                Arrays.asList(Statistic.MIN)
        );

        assertThat(l).hasSize(1);
        assertThat(l.get(0)).isEqualToComparingFieldByField(new AggregateResult(Measurement.METRIC_DEWPOINT, Statistic.MIN, 2.0));

        l = service.analyze(
                Arrays.asList(measurementsCreated.get(1)),
                Arrays.asList(Measurement.METRIC_DEWPOINT),
                Arrays.asList(Statistic.MAX)
        );

        assertThat(l).hasSize(1);
        assertThat(l.get(0)).isEqualToComparingFieldByField(new AggregateResult(Measurement.METRIC_DEWPOINT, Statistic.MAX, 2.0));
    }

    @Test
    public void analyzeMultipleMeasurementTest()
    {
        List<AggregateResult> l = service.analyze(
                measurementsCreated,
                Arrays.asList(Measurement.METRIC_TEMPERATURE),
                Arrays.asList(Statistic.MIN, Statistic.MAX, Statistic.AVERAGE)
        );

        // There must be 3 results
        assertThat(l).hasSize(3);

        List<String> ls = l.stream().map(AggregateResult::toString).collect(Collectors.toList());
        assertThat(ls).contains(
                new AggregateResult(Measurement.METRIC_TEMPERATURE, Statistic.MIN, 1.0).toString(),
                new AggregateResult(Measurement.METRIC_TEMPERATURE, Statistic.MAX, 7.0).toString(),
                new AggregateResult(Measurement.METRIC_TEMPERATURE, Statistic.AVERAGE, 4.0).toString()
        );
    }
}
