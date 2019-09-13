package y.w.weathertracker.measurements.service;

import y.w.weathertracker.measurements.model.Measurement;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface MeasurementStore
{
    /**
     * Add a measurement to the store.
     *
     * @param measurement it must has a timestamp. We will remove fractional secod value if any.
     * @return true if successful, false if failed to add.
     */
    boolean add(Measurement measurement);

    /**
     * @param timestamp a target timestamp. We will remove the fractional second value.
     * @return a measurement sampled at timestamp
     */
    Optional<Measurement> fetch(ZonedDateTime timestamp);

    List<Measurement> fetchAll();

    /**
     * Returns the number of measurements in the store.
     *
     * @return
     */
    int count();
}
