package y.w.weathertracker.measurements.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

/**
 * Implementation of the Measurement. A measurement can have the following properties
 *
 * Metric Name	    Type	    Example	                    Notes
 * timestamp	    DateTime	"2015-09-01T16:00:00.000Z"	Always sent as an ISO-8061 string in UTC
 * temperature	    float	    22.4	                    in ° C
 * dewPoint	        float	    18.6	                    in ° C
 * precipitation	float	    142.2	                    in mm
 */
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor // needed for JSON
@Setter
@Getter
@ToString
public class Measurement {
    public final static String METRIC_TEMPERATURE   = "temperature";
    public final static String METRIC_DEWPOINT      = "dewPoint";
    public final static String METRIC_PRECIPITATION = "precipitation";

    @ApiModelProperty(notes = "Timestamp in ISO-8061 format. Specifies the time when the metric is sampled")
    @JsonProperty(value="timestamp")
    private ZonedDateTime timestamp;

    @ApiModelProperty(notes = "Temperature value")
    @JsonProperty(value="temperature")
    private Double temperature;

    @ApiModelProperty(notes = "Dew point value")
    @JsonProperty(value="dewPoint")
    private Double dewPoint;

    @ApiModelProperty(notes = "Precipitation value")
    @JsonProperty(value="precipitation")
    private Double precipitation;
}
