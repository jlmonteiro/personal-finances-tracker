package com.jorgemonteiro.apps.finance.incomesource

import com.jorgemonteiro.apps.finance.common.ScenarioContext
import io.cucumber.datatable.DataTable
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
 * Cucumber step definitions for income sources API.
 */
class IncomeSourceSteps {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var dsl: DSLContext

    @Autowired
    private lateinit var context: ScenarioContext

    @Before
    fun cleanup() {
        dsl.execute("TRUNCATE income_sources CASCADE")
    }

    @Given("no income sources exist")
    fun noIncomeSourcesExist() {
        // Already truncated in @Before
    }

    @Given("an income source exists with name {string} and amount {string} currency {string}")
    fun incomeSourceExists(name: String, amount: String, currency: String) {
        val response = mockMvc.perform(
            post("/api/v1/income-sources")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "$name",
                        "amount": {"value": "$amount", "currency": "$currency"},
                        "frequency": "MONTHLY",
                        "paymentDateType": "FIXED",
                        "paymentDateRule": "25",
                        "startDate": "2026-01-01"
                    }
                """.trimIndent())
        ).andReturn()

        context.lastCreatedId = com.jayway.jsonpath.JsonPath.read<String>(
            response.response.contentAsString, "$.id"
        )
    }

    @When("I list income sources")
    fun listIncomeSources() {
        context.result = mockMvc.perform(get("/api/v1/income-sources"))
    }

    @When("I list income sources with page {int} and size {int}")
    fun listIncomeSourcesPaginated(page: Int, size: Int) {
        context.result = mockMvc.perform(get("/api/v1/income-sources?page=$page&size=$size"))
    }

    @When("I create an income source with name {string} and amount {string} currency {string}")
    fun createIncomeSource(name: String, amount: String, currency: String) {
        context.result = mockMvc.perform(
            post("/api/v1/income-sources")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "$name",
                        "amount": {"value": "$amount", "currency": "$currency"},
                        "frequency": "MONTHLY",
                        "paymentDateType": "FIXED",
                        "paymentDateRule": "25",
                        "startDate": "2026-01-01"
                    }
                """.trimIndent())
        )

        try {
            context.lastCreatedId = com.jayway.jsonpath.JsonPath.read<String>(
                context.result.andReturn().response.contentAsString, "$.id"
            )
        } catch (_: Exception) {}
    }

    @When("I create an income source:")
    fun createIncomeSourceFromTable(table: DataTable) {
        val data = table.asMap(String::class.java, String::class.java)
        context.result = mockMvc.perform(
            post("/api/v1/income-sources")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "${data["name"]}",
                        "description": "${data["description"] ?: ""}",
                        "amount": {"value": "${data["amount"]}", "currency": "${data["currency"]}"},
                        "frequency": "${data["frequency"] ?: "MONTHLY"}",
                        "paymentDateType": "${data["paymentDateType"] ?: "FIXED"}",
                        "paymentDateRule": "${data["paymentDateRule"] ?: "1"}",
                        "startDate": "${data["startDate"] ?: "2026-01-01"}"
                    }
                """.trimIndent())
        )
    }

    @When("I create an income source with empty body")
    fun createIncomeSourceEmptyBody() {
        context.result = mockMvc.perform(
            post("/api/v1/income-sources")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        )
    }

    @When("I create an income source with name {string} and no amount")
    fun createIncomeSourceNoAmount(name: String) {
        context.result = mockMvc.perform(
            post("/api/v1/income-sources")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "$name",
                        "frequency": "MONTHLY",
                        "paymentDateType": "FIXED",
                        "paymentDateRule": "25",
                        "startDate": "2026-01-01"
                    }
                """.trimIndent())
        )
    }

    @When("I create an income source with invalid frequency {string}")
    fun createIncomeSourceInvalidFrequency(frequency: String) {
        context.result = mockMvc.perform(
            post("/api/v1/income-sources")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "Test",
                        "amount": {"value": "100.00", "currency": "EUR"},
                        "frequency": "$frequency",
                        "paymentDateType": "FIXED",
                        "paymentDateRule": "1",
                        "startDate": "2026-01-01"
                    }
                """.trimIndent())
        )
    }

    @When("I create an income source with invalid payment date type {string}")
    fun createIncomeSourceInvalidDateType(dateType: String) {
        context.result = mockMvc.perform(
            post("/api/v1/income-sources")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "Test",
                        "amount": {"value": "100.00", "currency": "EUR"},
                        "frequency": "MONTHLY",
                        "paymentDateType": "$dateType",
                        "paymentDateRule": "1",
                        "startDate": "2026-01-01"
                    }
                """.trimIndent())
        )
    }

    @When("I update the income source with amount {string} currency {string}")
    fun updateIncomeSourceAmount(amount: String, currency: String) {
        context.result = mockMvc.perform(
            patch("/api/v1/income-sources/${context.lastCreatedId}")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"amount": {"value": "$amount", "currency": "$currency"}}""")
        )
    }

    @When("I update the income source with name {string}")
    fun updateIncomeSourceName(name: String) {
        context.result = mockMvc.perform(
            patch("/api/v1/income-sources/${context.lastCreatedId}")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "$name"}""")
        )
    }

    @When("I update income source {string} with name {string}")
    fun updateNonExistentIncomeSource(id: String, name: String) {
        context.result = mockMvc.perform(
            patch("/api/v1/income-sources/$id")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "$name"}""")
        )
    }

    @When("I deactivate the income source")
    fun deactivateIncomeSource() {
        context.result = mockMvc.perform(
            patch("/api/v1/income-sources/${context.lastCreatedId}")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"isActive": false}""")
        )
    }

    @When("I delete the income source")
    fun deleteIncomeSource() {
        context.result = mockMvc.perform(delete("/api/v1/income-sources/${context.lastCreatedId}"))
    }

    @When("I delete income source {string}")
    fun deleteNonExistentIncomeSource(id: String) {
        context.result = mockMvc.perform(delete("/api/v1/income-sources/$id"))
    }

    @And("the response contains {int} income sources")
    fun responseContainsCount(count: Int) {
        context.result.andExpect(jsonPath("$.data.length()").value(count))
    }

    @And("the pagination shows {int} total elements")
    fun paginationShowsTotal(total: Int) {
        context.result.andExpect(jsonPath("$.pagination.totalElements").value(total))
    }

    @And("the pagination shows {int} total pages")
    fun paginationShowsTotalPages(totalPages: Int) {
        context.result.andExpect(jsonPath("$.pagination.totalPages").value(totalPages))
    }

    @And("the response contains income source name {string}")
    fun responseContainsName(name: String) {
        context.result.andExpect(jsonPath("$.name").value(name))
    }

    @And("the response contains income source description {string}")
    fun responseContainsDescription(description: String) {
        context.result.andExpect(jsonPath("$.description").value(description))
    }

    @And("the response contains income source amount {string} currency {string}")
    fun responseContainsAmount(amount: String, currency: String) {
        context.result.andExpect(jsonPath("$.amount.value").value(amount))
        context.result.andExpect(jsonPath("$.amount.currency").value(currency))
    }

    @And("the response contains an income source id")
    fun responseContainsIncomeSourceId() {
        context.result.andExpect(jsonPath("$.id").exists())
    }

    @And("the response contains an income source createdAt")
    fun responseContainsIncomeSourceCreatedAt() {
        context.result.andExpect(jsonPath("$.createdAt").exists())
    }

    @And("the response contains income source isActive false")
    fun responseContainsIsActiveFalse() {
        context.result.andExpect(jsonPath("$.isActive").value(false))
    }

    @And("the income source no longer exists")
    fun incomeSourceNoLongerExists() {
        mockMvc.perform(get("/api/v1/income-sources"))
            .andExpect(jsonPath("$.data.length()").value(0))
    }
}
