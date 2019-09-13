package y.w.weathertracker.error;

/**
 * MeasurementNotFoundException
 *
 * @author ywang
 * @date 8/13/2019
 */
public class MeasurementNotFoundException extends WeatherServiceException
{
    public MeasurementNotFoundException(String message)
    {
        super(message);
    }
}
