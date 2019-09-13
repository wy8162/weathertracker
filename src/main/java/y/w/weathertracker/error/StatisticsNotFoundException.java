package y.w.weathertracker.error;

/**
 * MeasurementNotFoundException
 *
 * @author ywang
 * @date 8/13/2019
 */
public class StatisticsNotFoundException extends WeatherServiceException
{
    public StatisticsNotFoundException(String message)
    {
        super(message);
    }
}
