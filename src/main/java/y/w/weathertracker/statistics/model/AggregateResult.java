package y.w.weathertracker.statistics.model;

import y.w.weathertracker.statistics.Statistic;
import com.fasterxml.jackson.annotation.JsonGetter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Each instance of this class represents one statistics information. I.e., based on the specification of the project.
 *       | stat         | min                      |
 *       | stat         | max                      |
 *       | stat         | average                  |
 *       | metric       | temperature              |
 *       | metric       | dewPoint                 |
 *       | metric       | precipitation            |
 */
@Builder(toBuilder = true)
@NoArgsConstructor // needed for JSON
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class AggregateResult {
    public static String PARAM_TYPE_METRIC       = "metric";
    public static String PARAM_TYPE_STAT         = "stat";
    public static String PARAM_TYPE_FROMDATETIME = "fromDateTime";
    public static String PARAM_TYPE_TODATETIME   = "toDateTime";

    private String    metric;
    private Statistic statistic;
    private double    value;

    public AggregateResult(String metric, Statistic statistic, double value) {
        this.metric = metric;
        this.statistic = statistic;
        this.value = value;
    }

    @ApiModelProperty(notes = "Metric which can be temperature, dewPoint and precipitation")
    @JsonGetter("metric")
    public String getMetric() {
        return this.metric;
    }

    @ApiModelProperty(notes = "Statistics include MIN, MAX and AVERAGE")
    @JsonGetter("stat")
    public Statistic getStatistic() {
        return this.statistic;
    }

    @ApiModelProperty(notes = "The value of the metric")
    @JsonGetter("value")
    public double getValue() {
        return this.value;
    }
}
