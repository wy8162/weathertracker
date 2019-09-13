package y.w.weathertracker.advice;

import y.w.weathertracker.measurements.controller.MeasurementsResourceController;
import y.w.weathertracker.statistics.Statistic;
import y.w.weathertracker.statistics.controller.StatsResourceController;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.text.Format;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

/**
 *
 * Copied from https://github.com/Noviato/spring-rxjava-example/blob/master/src/main/java/com/vn/noviato/rest/ControllerAdviceInitBinder.java
 *
 * ControllerAdviceBinder
 *
 * @author ywang
 * @date 8/10/2019
 */
@ControllerAdvice(assignableTypes = { MeasurementsResourceController.class, StatsResourceController.class })
public class ControllerAdviceInitBinder
{
    private static class Editor<T> extends PropertyEditorSupport
    {
        private final Function<String, T> parser;
        private final Format              format;

        public Editor(Function<String, T> parser, Format format) {

            this.parser = parser;
            this.format = format;
        }

        public void setAsText(String text) {

            setValue(this.parser.apply(text));
        }

        @SuppressWarnings("unchecked")
        public String getAsText() {

            return format.format((T) getValue());
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {

        webDataBinder.registerCustomEditor(
                Instant.class,
                new Editor<>(
                        Instant::parse,
                        DateTimeFormatter.ISO_INSTANT.toFormat()));

        webDataBinder.registerCustomEditor(
                LocalDate.class,
                new Editor<>(
                        text -> LocalDate.parse(text, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy").toFormat()));

        webDataBinder.registerCustomEditor(
                LocalDateTime.class,
                new Editor<>(
                        text -> LocalDateTime.parse(text, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").toFormat()));

        webDataBinder.registerCustomEditor(
                LocalTime.class,
                new Editor<>(
                        text -> LocalTime.parse(text, DateTimeFormatter.ISO_LOCAL_TIME),
                        DateTimeFormatter.ISO_LOCAL_TIME.toFormat()));

        webDataBinder.registerCustomEditor(
                OffsetDateTime.class,
                new Editor<>(
                        text -> OffsetDateTime.parse(text, DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                        DateTimeFormatter.ISO_OFFSET_DATE_TIME.toFormat()));

        webDataBinder.registerCustomEditor(
                OffsetTime.class,
                new Editor<>(
                        text -> OffsetTime.parse(text, DateTimeFormatter.ISO_OFFSET_TIME),
                        DateTimeFormatter.ISO_OFFSET_TIME.toFormat()));

        /**
         * The @InitBinder will enable controller handlers to have parameters like below:
         *
         *  ResponseEntity<Measurement> getMeasurement(@PathVariable ZonedDateTime timestamp)
         *
         *  or
         *
         *      @GetMapping
         *     public List<AggregateResult> getStats(
         *             @RequestParam("metric") List<String> metrics,
         *             @RequestParam("stat") List<Statistic> stats,
         *             @RequestParam("fromDateTime") ZonedDateTime from,
         *             @RequestParam("toDateTime") ZonedDateTime to
         *     )
         *
         * Will parse ISO 8061 string in UTC like "2015-09-01T16:00:00.000Z" to ZonedDateTime like
         * 2015-09-01T16:00Z[UTC]
         */
        webDataBinder.registerCustomEditor(
                ZonedDateTime.class,
                new Editor<>(
                        text -> ZonedDateTime.parse(text, DateTimeFormatter.ISO_ZONED_DATE_TIME).withZoneSameInstant(ZoneId.of("UTC")),
                                DateTimeFormatter.ISO_ZONED_DATE_TIME.toFormat()));

        webDataBinder.registerCustomEditor(
                Statistic.class,
                new StatisticsConverter());
    }

    public static class StatisticsConverter extends PropertyEditorSupport
    {
        @Override
        public void setAsText(String text) throws IllegalArgumentException
        {
            switch (text.toUpperCase())
            {
            case "MIN":
                setValue(Statistic.MIN);
                break;
            case "MAX":
                setValue(Statistic.MAX);
                break;
            case "AVERAGE":
                setValue(Statistic.AVERAGE);
                break;
            }
        }
    }
}
