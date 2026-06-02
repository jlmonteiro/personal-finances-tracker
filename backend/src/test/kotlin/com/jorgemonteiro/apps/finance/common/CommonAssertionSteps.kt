package com.jorgemonteiro.apps.finance.common

import io.cucumber.java.en.Then
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Shared assertion steps reusable across all features.
 */
class CommonAssertionSteps {

    @Autowired
    private lateinit var context: ScenarioContext

    @Then("the response status is {int}")
    fun responseStatusIs(statusCode: Int) {
        context.result.andExpect(status().`is`(statusCode))
    }
}
