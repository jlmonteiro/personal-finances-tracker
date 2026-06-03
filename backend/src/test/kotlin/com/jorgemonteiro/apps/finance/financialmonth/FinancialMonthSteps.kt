package com.jorgemonteiro.apps.finance.financialmonth

import com.jayway.jsonpath.JsonPath
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
import java.util.UUID

/**
 * Cucumber step definitions for financial months API.
 */
class FinancialMonthSteps {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var dsl: DSLContext

    @Autowired
    private lateinit var context: ScenarioContext

    private var lastMonthId: String? = null

    @Before
    fun cleanup() {
        dsl.execute("TRUNCATE quarters CASCADE")
        dsl.execute("TRUNCATE financial_months CASCADE")
        dsl.execute("TRUNCATE app_configuration CASCADE")
    }

    @Given("the application is configured with month start day {int}")
    fun configuredWithMonthStartDay(day: Int) {
        // Try POST first, if 409 then PATCH
        val result = mockMvc.perform(
            post("/api/v1/configuration")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"currency": "EUR", "monthStartDay": $day}""")
        ).andReturn()
        if (result.response.status == 409) {
            mockMvc.perform(
                patch("/api/v1/configuration")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"monthStartDay": $day}""")
            )
        }
    }

    @Given("a financial month exists for year {int} month {int}")
    fun financialMonthExists(year: Int, month: Int) {
        val response = mockMvc.perform(
            post("/api/v1/financial-months")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"year": $year, "month": $month}""")
        ).andReturn()
        lastMonthId = JsonPath.read(response.response.contentAsString, "$.id")
    }

    @When("I create a financial month for year {int} month {int}")
    fun createFinancialMonth(year: Int, month: Int) {
        context.result = mockMvc.perform(
            post("/api/v1/financial-months")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"year": $year, "month": $month}""")
        )
        try {
            val body = context.result.andReturn().response.contentAsString
            lastMonthId = JsonPath.read(body, "$.id")
        } catch (_: Exception) {}
    }

    @When("I list financial months")
    fun listFinancialMonths() {
        context.result = mockMvc.perform(get("/api/v1/financial-months"))
    }

    @When("I get the financial month by ID")
    fun getFinancialMonthById() {
        context.result = mockMvc.perform(get("/api/v1/financial-months/$lastMonthId"))
    }

    @When("I get financial month with random ID")
    fun getFinancialMonthWithRandomId() {
        context.result = mockMvc.perform(get("/api/v1/financial-months/${UUID.randomUUID()}"))
    }

    @When("I get the quarters for the created month")
    fun getQuartersForCreatedMonth() {
        context.result = mockMvc.perform(get("/api/v1/financial-months/$lastMonthId/quarters"))
    }

    @When("I get quarters for financial month with random ID")
    fun getQuartersForRandomId() {
        context.result = mockMvc.perform(get("/api/v1/financial-months/${UUID.randomUUID()}/quarters"))
    }

    @And("the financial month starts on {string}")
    fun financialMonthStartsOn(date: String) {
        context.result.andExpect(jsonPath("$.startDate").value(date))
    }

    @And("the financial month ends on {string}")
    fun financialMonthEndsOn(date: String) {
        context.result.andExpect(jsonPath("$.endDate").value(date))
    }

    @And("there are {int} quarters")
    fun thereAreQuarters(count: Int) {
        context.result.andExpect(jsonPath("$.length()").value(count))
    }

    @And("quarter {int} starts on {string}")
    fun quarterStartsOn(number: Int, date: String) {
        context.result.andExpect(jsonPath("$[${number - 1}].startDate").value(date))
    }

    @And("quarter {int} ends on {string}")
    fun quarterEndsOn(number: Int, date: String) {
        context.result.andExpect(jsonPath("$[${number - 1}].endDate").value(date))
    }
}
