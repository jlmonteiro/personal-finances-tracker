package com.jorgemonteiro.apps.finance.configuration

import com.jorgemonteiro.apps.finance.common.ScenarioContext
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.When
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

/**
 * Cucumber step definitions for configuration API.
 */
class ConfigurationSteps {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var dsl: DSLContext

    @Autowired
    private lateinit var context: ScenarioContext

    @Before
    fun cleanup() {
        dsl.execute("TRUNCATE app_configuration CASCADE")
    }

    @Given("no configuration exists")
    fun noConfigurationExists() {
        // Already truncated in @Before
    }

    @Given("configuration exists with currency {string} and month start day {int}")
    fun configurationExists(currency: String, monthStartDay: Int) {
        mockMvc.perform(
            post("/api/v1/configuration")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"currency":"$currency","monthStartDay":$monthStartDay}""")
        )
    }

    @When("I request the configuration")
    fun requestConfiguration() {
        context.result = mockMvc.perform(get("/api/v1/configuration"))
    }

    @When("I create configuration with empty body")
    fun createConfigurationEmptyBody() {
        context.result = mockMvc.perform(
            post("/api/v1/configuration")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        )
    }

    @When("I create configuration with currency {string} and month start day {int}")
    fun createConfiguration(currency: String, monthStartDay: Int) {
        context.result = mockMvc.perform(
            post("/api/v1/configuration")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"currency":"$currency","monthStartDay":$monthStartDay}""")
        )
    }

    @When("I update configuration with currency {string}")
    fun updateConfigurationCurrency(currency: String) {
        context.result = mockMvc.perform(
            patch("/api/v1/configuration")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"currency":"$currency"}""")
        )
    }

    @When("I update configuration with month start day {int}")
    fun updateConfigurationMonthStartDay(monthStartDay: Int) {
        context.result = mockMvc.perform(
            patch("/api/v1/configuration")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"monthStartDay":$monthStartDay}""")
        )
    }

    @And("the response contains currency {string}")
    fun responseContainsCurrency(currency: String) {
        context.result.andExpect(jsonPath("$.currency").value(currency))
    }

    @And("the response contains month start day {int}")
    fun responseContainsMonthStartDay(monthStartDay: Int) {
        context.result.andExpect(jsonPath("$.monthStartDay").value(monthStartDay))
    }

    @And("the response contains an id")
    fun responseContainsId() {
        context.result.andExpect(jsonPath("$.id").exists())
    }

    @And("the response contains a createdAt timestamp")
    fun responseContainsCreatedAt() {
        context.result.andExpect(jsonPath("$.createdAt").exists())
    }
}
