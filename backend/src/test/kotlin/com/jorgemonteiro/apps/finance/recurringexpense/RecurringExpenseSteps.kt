package com.jorgemonteiro.apps.finance.recurringexpense

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

class RecurringExpenseSteps {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var dsl: DSLContext

    @Autowired
    private lateinit var context: ScenarioContext

    private var lastRecurringId: String? = null

    @Before
    fun cleanup() {
        dsl.execute("TRUNCATE recurring_expenses CASCADE")
    }

    private fun getPayeeId(): String {
        val r = mockMvc.perform(get("/api/v1/payees")).andReturn()
        return JsonPath.read(r.response.contentAsString, "$[0].id")
    }

    private fun getCategoryId(): String {
        val r = mockMvc.perform(get("/api/v1/categories")).andReturn()
        return JsonPath.read(r.response.contentAsString, "$[0].id")
    }

    @Given("a recurring expense {string} with value {double} exists")
    fun recurringExpenseExists(title: String, value: Double) {
        val payeeId = getPayeeId()
        val categoryId = getCategoryId()
        val r = mockMvc.perform(
            post("/api/v1/recurring-expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"payeeId":"$payeeId","categoryId":"$categoryId","title":"$title","expectedValue":$value,"frequency":"MONTHLY","startDate":"2026-01-01"}""")
        ).andReturn()
        lastRecurringId = JsonPath.read(r.response.contentAsString, "$.id")
    }

    @When("I create a recurring expense {string} with value {double} frequency {string}")
    fun createRecurringExpense(title: String, value: Double, frequency: String) {
        val payeeId = getPayeeId()
        val categoryId = getCategoryId()
        context.result = mockMvc.perform(
            post("/api/v1/recurring-expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"payeeId":"$payeeId","categoryId":"$categoryId","title":"$title","expectedValue":$value,"frequency":"$frequency","startDate":"2026-01-01"}""")
        )
    }

    @When("I list recurring expenses")
    fun listRecurringExpenses() {
        context.result = mockMvc.perform(get("/api/v1/recurring-expenses"))
    }

    @When("I update the recurring expense value to {double}")
    fun updateRecurringExpenseValue(value: Double) {
        context.result = mockMvc.perform(
            patch("/api/v1/recurring-expenses/$lastRecurringId")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"expectedValue":$value}""")
        )
    }

    @When("I deactivate the recurring expense")
    fun deactivateRecurringExpense() {
        context.result = mockMvc.perform(
            patch("/api/v1/recurring-expenses/$lastRecurringId")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"isActive":false}""")
        )
    }

    @When("I delete the recurring expense")
    fun deleteRecurringExpense() {
        context.result = mockMvc.perform(delete("/api/v1/recurring-expenses/$lastRecurringId"))
    }

    @When("I delete recurring expense with random ID")
    fun deleteRecurringExpenseRandom() {
        context.result = mockMvc.perform(delete("/api/v1/recurring-expenses/${UUID.randomUUID()}"))
    }

    @And("the recurring expense title is {string}")
    fun recurringExpenseTitle(title: String) {
        context.result.andExpect(jsonPath("$.title").value(title))
    }

    @And("the recurring expense is active")
    fun recurringExpenseIsActive() {
        context.result.andExpect(jsonPath("$.isActive").value(true))
    }

    @And("the recurring expense is not active")
    fun recurringExpenseIsNotActive() {
        context.result.andExpect(jsonPath("$.isActive").value(false))
    }

    @And("the recurring expense value is {double}")
    fun recurringExpenseValue(value: Double) {
        context.result.andExpect(jsonPath("$.expectedValue").value(value))
    }
}
