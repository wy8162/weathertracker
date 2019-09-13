package y.w.weathertracker.measurements.service;

        import y.w.weathertracker.measurements.model.Measurement;

        import java.util.List;
        import java.time.ZonedDateTime;

public interface MeasurementQueryService {
    /**
     * @param from starting timestamp inclusive.
     * @param to ending timestamp exclusive
     * @return list of measurements between from and to.
     */
    List<Measurement> queryDateRange(ZonedDateTime from, ZonedDateTime to);
}
