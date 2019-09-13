package y.w.weathertracker.measurements.controller;

import y.w.weathertracker.error.MeasurementBadRequestException;
import y.w.weathertracker.error.MeasurementNotFoundException;
import y.w.weathertracker.measurements.model.Measurement;
import y.w.weathertracker.measurements.service.MeasurementStore;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/measurements")
public class MeasurementsResourceController
{
    private final MeasurementStore  store;
    private final DateTimeFormatter dateTimeFormatter;

    @Autowired
    public MeasurementsResourceController(MeasurementStore store, DateTimeFormatter dateTimeFormatter) {
        this.store = store;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @ApiOperation(value="Create a measurement", response = boolean.class)
    @ApiResponses(value={
            @ApiResponse(code = 200, message = "Successfully created measurement"),
            @ApiResponse(code = 400, message = "Bad request")
    })
    @PostMapping
    public ResponseEntity<Measurement> createMeasurement( // ResponseEntity to return metadata along with data
            @Valid @RequestBody Measurement measurement, UriComponentsBuilder ucb) {
        boolean result = store.add(measurement);

        log.info(measurement.toString());

        if (result)
        {
            // Use HttpHeaders location to return the URI of the newly created resources.
            HttpHeaders headers = new HttpHeaders();
            URI locationUri = ucb
                    .path("/measurements/")
                    .path(dateTimeFormatter.format(measurement.getTimestamp()))
                    .build()
                    .toUri();
            headers.setLocation(locationUri);

            // HttpStatus.CREATED, implicit by method created()
            return new ResponseEntity<>(measurement, headers, HttpStatus.CREATED);
        } else
            // BAD_REQUEST
            throw new MeasurementBadRequestException("Measurement not created because of invalid data: " + measurement.toString());
    }

    /**
     * Pathmatcher needs to make sure the ISO 8061 time like 2015-09-01T16:00:00.000Z
     * in URI path variable is passed correctly.
     *
     * The regexp basically matches ".000Z" part as well, instead of being treated as
     * extension.
     *
     * @param timestamp
     * @return
     */
    @ApiOperation(value = "Retrieve a measurement based on a timestamp")
    @ResponseStatus(HttpStatus.OK) // No need to use ResponseEntity anymore. Just handle OK case.
    @GetMapping("/{timestamp:.+}")
    public Measurement getMeasurement(@PathVariable ZonedDateTime timestamp) {
        Optional<Measurement> measurement = store.fetch(timestamp);

        if (measurement.isPresent())
            log.info("Fetched one measurement : " + measurement.get().toString());
        else
            log.info("There is no measurement available for timestampt : " + timestamp);

        if (measurement.isPresent()) {
            return measurement.get();
        } else {
            // Exception handler handles NOT_FOUND case.
            throw new MeasurementNotFoundException("Measurement not found for timestamp " + timestamp);
        }
    }
}
