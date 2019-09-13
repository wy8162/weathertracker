package y.w.weathertracker.bdd;

import y.w.weathertracker.measurements.model.Measurement;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * These are the test definitions for the following features
 * - 02-get-measurement.feature
 */
@Slf4j
public class GetMeasurementStepDefs
{
    @Autowired
    private StepDefsHelper helper;

    @When("I get a measurement for {string}")
    public void i_get_a_measurement_for(String dateTimeStamp) throws URISyntaxException
    {
        int responseCode;
        try
        {
            ResponseEntity<Measurement> responseEntity = helper.getRestTemplate().getForEntity(
                    new URI(helper.getBaseMeasurementUrl() + "/" + StepDefsHelper.stripLeadingTrailingQuote(dateTimeStamp)),
                    Measurement.class);
            Measurement measurement = responseEntity.getBody();
            responseCode = responseEntity.getStatusCodeValue();

            helper.setResponseEntity(responseEntity);
            helper.setMeasurement(measurement);
        }
        catch (HttpClientErrorException e)
        {
            responseCode = e.getStatusCode().value();
        }
        helper.setResultSet(responseCode, null);
    }

    @Then("the response body is:")
    public void the_response_body_is(DataTable dataTable) {
        List<String> data = dataTable.asLists().get(1);
        ZonedDateTime zd = Instant.parse(StepDefsHelper.stripLeadingTrailingQuote(data.get(0))).atZone( ZoneId.of("UTC"));
        Double temperature = Double.parseDouble(data.get(1));
        Double dewPoint    = Double.parseDouble(data.get(2));
        Double precip      = Double.parseDouble(data.get(3));

        assertThat(helper.getMeasurement().toString()).isEqualTo(new Measurement(zd, temperature, dewPoint, precip).toString());
    }
}