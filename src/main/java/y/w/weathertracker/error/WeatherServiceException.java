package y.w.weathertracker.error;

/**
 * WeatherServiceException
 *
 * @author ywang
 * @date 8/13/2019
 */
public class WeatherServiceException extends RuntimeException
{
    public WeatherServiceException(String message)
    {
        super(message);
    }

    /**
     * This is to prevent exception stacktace to be printed on console.
     * @return
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
