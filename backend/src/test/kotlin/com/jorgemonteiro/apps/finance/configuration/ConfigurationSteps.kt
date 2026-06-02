package com.jorgemonteiro.apps.finance.configuration

import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

/**
 * Cucumber step definitions for configuration API tests.
 */
class ConfigurationSteps {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var dsl: DSLContext

    private lateinit var result: ResultActions

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
        result = mockMvc.perform(get("/api/v1/configuration"))
    }

    @When("I create configuration with empty body")
    fun createConfigurationEmptyBody() {
        result = mockMvc.perform(
            post("/api/v1/configuration")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        )
    }

    @When("I create configuration with currency {string} and month start day {int}")
    fun createConfiguration(currency: String, monthStartDay: Int) {
        result = mockMvc.perform(
            post("/api/v1/configuration")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"currency":"$currency","monthStartDay":$monthStartDay}""")
        )
    }

    @When("I update configuration with currency {string}")
    fun updateConfigurationCurrency(currency: String) {
        result = mockMvc.perform(
            patch("/api/v1/configuration")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"currency":"$currency"}""")
        )
    }

    @When("I update configuration with month start day {int}")
    fun updateConfigurationMonthStartDay(monthStartDay: Int) {
        result = mockMvc.perform(
            patch("/api/v1/configuration")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"monthStartDay":$monthStartDay}""")
        )
    }

    @Then("the response status is {int}")
    fun responseStatusIs(status: Int) {
        result.andExpect(status().`is`(status))
    }

    @And("the response contains currency {string}")
    fun responseContainsCurrency(currency: String) {
        result.andExpect(jsonPath("$.currency").value(currency))
    }

    @And("the response contains month start day {int}")
    fun responseContainsMonthStartDay(monthStartDay: Int) {
        result.andExpect(jsonPath("$.monthStartDay").value(monthStartDay))
    }

    @And("the response contains an id")
    fun responseContainsId() {
        result.andExpect(jsonPath("$.id").exists())
    }

    @And("the response contains a createdAt timestamp")
    fun responseContainsCreatedAt() {
        result.andExpect(jsonPath("$.createdAt").exists())
    }
}
