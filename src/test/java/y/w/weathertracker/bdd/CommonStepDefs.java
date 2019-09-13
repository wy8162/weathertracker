package y.w.weathertracker.bdd;

import y.w.weathertracker.typeregistry.JsonDTO;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * These are the common test definitions for the following features
 * - 01-add-measurement.feature
 * - 01-get-stats.feature
 * - 02-get-measurement.feature
 */
@Slf4j
public class CommonStepDefs extends AbstractSpringConfigurationTest
{
    @Autowired
    private StepDefsHelper helper;

    @Given("I have submitted new measurements as follows:")
    public void i_have_submitted_new_measurements_as_follows(JsonDTO matrix) throws URISyntaxException
    {
        List<String> jsons = matrix.getListOfJson();
        for (String s : jsons)
        {
            log.info("Measureament to be submitted: " + s);

            StepDefsHelper.ResultSet resultSet = helper.postMeasurement(helper.getRestTemplate(), new URI(helper.getBaseMeasurementUrl()), s);
            helper.setResultSet(resultSet);
        }
    }

    @When("I submit a new measurement as follows:")
    public void i_submit_a_new_measurement_as_follows(JsonDTO matrix)
            throws URISyntaxException
    {
        List<String> jsons = matrix.getListOfJson();

        log.info("Measureament to be submitted: " + jsons.get(0));

        StepDefsHelper.ResultSet resultSet = helper.postMeasurement(helper.getRestTemplate(), new URI(helper.getBaseMeasurementUrl()), jsons.get(0));

        helper.setResultSet(resultSet);
    }

    @Then("the response has a status code of {int}")
    public void the_response_has_a_status_code_of(Integer statusCode)
    {
        assertThat(helper.getResultSet().responseCode).isEqualTo(statusCode);
    }
}