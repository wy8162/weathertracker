package y.w.weathertracker.advice;

import y.w.weathertracker.error.MeasurementBadRequestException;
import y.w.weathertracker.error.MeasurementNotFoundException;
import y.w.weathertracker.error.ServiceServerErrorException;
import y.w.weathertracker.error.StatisticsBadRequestException;
import y.w.weathertracker.error.StatisticsNotFoundException;
import y.w.weathertracker.error.WeatherServiceException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;

/**
 * ServiceErrorAdvice
 *
 * @author ywang
 * @date 8/13/2019
 */

@Slf4j
@ControllerAdvice
public class WeatherServiceErrorAdvice
{
    @ExceptionHandler({RuntimeException.class, ServiceServerErrorException.class, SQLException.class, NullPointerException.class})
    public ResponseEntity<Error> handleRunTimeException(RuntimeException e)
    {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    @ExceptionHandler({ MeasurementNotFoundException.class, StatisticsNotFoundException.class })
    public ResponseEntity<Error> handleNotFoundException(WeatherServiceException e)
    {
        return error(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler({ MeasurementBadRequestException.class, StatisticsBadRequestException.class})
    public ResponseEntity<Error> handlesServiceException(WeatherServiceException e)
    {
        return error(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class })
    public ResponseEntity<Error> handlesServiceException(HttpMessageNotReadableException e)
    {
        return error(HttpStatus.BAD_REQUEST, e);
    }

    private ResponseEntity<Error> error(HttpStatus status, Exception e)
    {
        log.error("Exception : ", e);
        return ResponseEntity.status(status).body(new Error(status, e.getMessage()));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Error
    {
        private HttpStatus status;
        private String message;
    }
}
