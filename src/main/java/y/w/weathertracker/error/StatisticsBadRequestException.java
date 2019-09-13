package y.w.weathertracker.error;

/**
 * MeasurementNotFoundException
 *
 * @author ywang
 * @date 8/13/2019
 */
public class StatisticsBadRequestException extends WeatherServiceException
{
    public StatisticsBadRequestException(String message)
    {
        super(message);
    }
}
