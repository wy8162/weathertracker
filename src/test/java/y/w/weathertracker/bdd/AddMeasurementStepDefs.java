package y.w.weathertracker.bdd;

import io.cucumber.java.en.And;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * These are the test definitions for the following features
 * - 01-add-measurement.feature
 */
@Slf4j
public class AddMeasurementStepDefs
{
    @Autowired
    private StepDefsHelper helper;

    //////////////////////////////////////////////////////////
    // Step Definitions for 01-add-measurement.feature
    //////////////////////////////////////////////////////////
    @And("the Location header has the path {string}")
    public void the_Location_header_has_the_path(String location)
    {
        assertThat(helper.getResultSet().re.getHeaders().getLocation().getPath()).isEqualTo(location);
    }
}