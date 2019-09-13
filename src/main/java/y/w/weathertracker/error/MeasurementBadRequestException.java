package y.w.weathertracker.error;

/**
 * MeasurementNotFoundException
 *
 * @author ywang
 * @date 8/13/2019
 */
public class MeasurementBadRequestException extends WeatherServiceException
{
    public MeasurementBadRequestException(String message)
    {
        super(message);
    }
}
