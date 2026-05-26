package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"stepdefinitions"},

        // Reports configuration
        plugin = {
                "pretty",
                "html:target/cucumber-reports.html",
                "json:target/cucumber-reports/cucumber.json",
                "summary"
        },

        // Tag execution control
        tags = "@smoke or @regression",

        monochrome = true,

        // Better strictness (recommended)
        dryRun = false
)
public class TestRunner extends AbstractTestNGCucumberTests {
}