package y.w.weathertracker;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = "pretty",
        features = { "classpath:features"},
        glue={"classpath:y.w.weathertracker.bdd", // Cucumber to find steps
              "classpath:y.w.weathertracker.typeregistry"})  // Cucumber to find TypeRegistry
public class WeathertrackerCucumberTests
{
}
