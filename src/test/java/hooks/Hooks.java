package hooks;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import utils.BaseTest;

public class Hooks {

    @Before
    public void setup() {

        BaseTest.initBrowser();
    }

    @After
    public void tearDown() {

        BaseTest.quitBrowser();
    }
}