package com.jorgemonteiro.apps.finance.expense

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
 * Cucumber step definitions for expenses API.
 */
class ExpenseSteps {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var dsl: DSLContext

    @Autowired
    private lateinit var context: ScenarioContext

    private var lastExpenseId: String? = null

    @Before
    fun cleanup() {
        dsl.execute("TRUNCATE expenses CASCADE")
    }

    private fun getMonthId(): String {
        val r = mockMvc.perform(get("/api/v1/financial-months")).andReturn()
        return JsonPath.read(r.response.contentAsString, "$[0].id")
    }

    private fun getPayeeId(): String {
        val r = mockMvc.perform(get("/api/v1/payees")).andReturn()
        return JsonPath.read(r.response.contentAsString, "$[0].id")
    }

    private fun getCategoryId(): String {
        val r = mockMvc.perform(get("/api/v1/categories")).andReturn()
        return JsonPath.read(r.response.contentAsString, "$[0].id")
    }

    @Given("an expense {string} with expected value {double} due on {string} exists")
    fun expenseExists(title: String, value: Double, dueDate: String) {
        val monthId = getMonthId()
        val payeeId = getPayeeId()
        val categoryId = getCategoryId()
        val r = mockMvc.perform(
            post("/api/v1/financial-months/$monthId/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"payeeId":"$payeeId","categoryId":"$categoryId","title":"$title","expectedValue":$value,"dueDate":"$dueDate"}""")
        ).andReturn()
        lastExpenseId = JsonPath.read(r.response.contentAsString, "$.id")
    }

    @When("I create an expense {string} with expected value {double} due on {string}")
    fun createExpense(title: String, value: Double, dueDate: String) {
        val monthId = getMonthId()
        val payeeId = getPayeeId()
        val categoryId = getCategoryId()
        context.result = mockMvc.perform(
            post("/api/v1/financial-months/$monthId/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"payeeId":"$payeeId","categoryId":"$categoryId","title":"$title","expectedValue":$value,"dueDate":"$dueDate"}""")
        )
        try {
            lastExpenseId = JsonPath.read(context.result.andReturn().response.contentAsString, "$.id")
        } catch (_: Exception) {}
    }

    @When("I create an expense with random payee due on {string}")
    fun createExpenseWithRandomPayee(dueDate: String) {
        val monthId = getMonthId()
        val categoryId = getCategoryId()
        context.result = mockMvc.perform(
            post("/api/v1/financial-months/$monthId/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"payeeId":"${UUID.randomUUID()}","categoryId":"$categoryId","title":"Test","expectedValue":10,"dueDate":"$dueDate"}""")
        )
    }

    @When("I list expenses for the financial month")
    fun listExpenses() {
        val monthId = getMonthId()
        context.result = mockMvc.perform(get("/api/v1/financial-months/$monthId/expenses"))
    }

    @When("I list expenses for random month ID")
    fun listExpensesForRandomMonth() {
        context.result = mockMvc.perform(get("/api/v1/financial-months/${UUID.randomUUID()}/expenses"))
    }

    @When("I update the expense with actual value {double} and payment date {string}")
    fun updateExpensePayment(value: Double, date: String) {
        context.result = mockMvc.perform(
            patch("/api/v1/expenses/$lastExpenseId")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"actualValue":$value,"paymentDate":"$date"}""")
        )
    }

    @When("I delete the expense")
    fun deleteExpense() {
        context.result = mockMvc.perform(delete("/api/v1/expenses/$lastExpenseId"))
    }

    @When("I delete expense with random ID")
    fun deleteExpenseRandom() {
        context.result = mockMvc.perform(delete("/api/v1/expenses/${UUID.randomUUID()}"))
    }

    @And("the expense title is {string}")
    fun expenseTitle(title: String) {
        context.result.andExpect(jsonPath("$.title").value(title))
    }

    @And("the expense status is {string}")
    fun expenseStatus(status: String) {
        context.result.andExpect(jsonPath("$.status").value(status))
    }

    @And("the expense quarter number is {int}")
    fun expenseQuarterNumber(number: Int) {
        context.result.andExpect(jsonPath("$.quarterNumber").value(number))
    }
}
