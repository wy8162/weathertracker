package y.w.weathertracker.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * MeasurementNotFoundException
 *
 * @ResponseStatus makes this exception available for use in a short way to create
 * @ControllerAdvice.
 *
 * @author ywang
 * @date 8/13/2019
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ServiceServerErrorException extends WeatherServiceException
{
    public ServiceServerErrorException(String message)
    {
        super(message);
    }
}
