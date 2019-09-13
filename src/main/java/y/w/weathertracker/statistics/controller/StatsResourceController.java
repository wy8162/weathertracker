package y.w.weathertracker.statistics.controller;

import y.w.weathertracker.measurements.model.Measurement;
import y.w.weathertracker.measurements.service.MeasurementQueryService;
import y.w.weathertracker.statistics.Statistic;
import y.w.weathertracker.statistics.model.AggregateResult;
import y.w.weathertracker.statistics.service.MeasurementAggregator;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/stats")
public class StatsResourceController
{
    private final MeasurementQueryService queryService;
    private final MeasurementAggregator   aggregator;

    public StatsResourceController(MeasurementQueryService queryService, MeasurementAggregator aggregator) {
        this.queryService = queryService;
        this.aggregator = aggregator;
    }

    /**
     * Changed from and to to bind String instead of ZonedDateTime. It won't work.
     *
     * TODO: resolve why ZonedDateTime not working.
     *
     * @param metrics
     * @param stats
     * @param from
     * @param to
     * @return
     */
    @ApiOperation(value = "Get statistics details for metrics")
    @GetMapping
    @ResponseStatus(HttpStatus.OK) // always ok. Returns empty if no stats
    public List<AggregateResult> getStats(
            @RequestParam("metric") List<String> metrics,
            @RequestParam("stat") List<Statistic> stats,
            @RequestParam("fromDateTime") ZonedDateTime from,
            @RequestParam("toDateTime") ZonedDateTime to
    )
    {
        List<Measurement> measurements = queryService.queryDateRange(from, to);
        return aggregator.analyze(measurements, metrics, stats);
    }
}
