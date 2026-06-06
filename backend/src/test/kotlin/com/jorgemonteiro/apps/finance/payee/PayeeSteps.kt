package com.jorgemonteiro.apps.finance.payee

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
 * Cucumber step definitions for payees API.
 */
class PayeeSteps {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var dsl: DSLContext

    @Autowired
    private lateinit var context: ScenarioContext

    private var lastPayeeId: String? = null

    @Before
    fun cleanup() {
        dsl.execute("TRUNCATE payee_categories CASCADE")
        dsl.execute("TRUNCATE payees CASCADE")
        dsl.execute("TRUNCATE categories CASCADE")
    }

    @Given("a payee {string} with category {string} exists")
    fun payeeWithCategoryExists(payeeName: String, categoryName: String) {
        // Look up category by name
        val catResult = mockMvc.perform(get("/api/v1/categories")).andReturn()
        val cats: List<String> = JsonPath.read(catResult.response.contentAsString, "$[?(@.name=='$categoryName')].id")
        val catId = cats.first()
        val response = mockMvc.perform(
            post("/api/v1/payees")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "$payeeName", "categoryIds": ["$catId"]}""")
        ).andReturn()
        lastPayeeId = JsonPath.read(response.response.contentAsString, "$.id")
    }

    @When("I create a payee {string} with all existing categories")
    fun createPayeeWithAllCategories(name: String) {
        val catResult = mockMvc.perform(get("/api/v1/categories")).andReturn()
        val ids: List<String> = JsonPath.read(catResult.response.contentAsString, "$[*].id")
        val idsJson = ids.joinToString(",") { "\"$it\"" }
        context.result = mockMvc.perform(
            post("/api/v1/payees")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "$name", "categoryIds": [$idsJson]}""")
        )
    }

    @When("I create a payee {string} with a random category ID")
    fun createPayeeWithRandomCategory(name: String) {
        context.result = mockMvc.perform(
            post("/api/v1/payees")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "$name", "categoryIds": ["${UUID.randomUUID()}"]}""")
        )
    }

    @When("I create a payee {string} with empty categories")
    fun createPayeeWithEmptyCategories(name: String) {
        context.result = mockMvc.perform(
            post("/api/v1/payees")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "$name", "categoryIds": []}""")
        )
    }

    @When("I list payees")
    fun listPayees() {
        context.result = mockMvc.perform(get("/api/v1/payees"))
    }

    @When("I update the payee name to {string}")
    fun updatePayeeName(name: String) {
        context.result = mockMvc.perform(
            patch("/api/v1/payees/$lastPayeeId")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "$name"}""")
        )
    }

    @When("I update the payee categories to all existing categories")
    fun updatePayeeCategories() {
        val catResult = mockMvc.perform(get("/api/v1/categories")).andReturn()
        val ids: List<String> = JsonPath.read(catResult.response.contentAsString, "$[*].id")
        val idsJson = ids.joinToString(",") { "\"$it\"" }
        context.result = mockMvc.perform(
            patch("/api/v1/payees/$lastPayeeId")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"categoryIds": [$idsJson]}""")
        )
    }

    @When("I update payee with random ID name to {string}")
    fun updatePayeeWithRandomId(name: String) {
        context.result = mockMvc.perform(
            patch("/api/v1/payees/${UUID.randomUUID()}")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "$name"}""")
        )
    }

    @When("I delete the payee")
    fun deletePayee() {
        context.result = mockMvc.perform(delete("/api/v1/payees/$lastPayeeId"))
    }

    @When("I delete payee with random ID")
    fun deletePayeeWithRandomId() {
        context.result = mockMvc.perform(delete("/api/v1/payees/${UUID.randomUUID()}"))
    }

    @And("the response contains payee name {string}")
    fun responseContainsPayeeName(name: String) {
        context.result.andExpect(jsonPath("$.name").value(name))
    }

    @And("the response contains {int} categories")
    fun responseContainsCategories(count: Int) {
        context.result.andExpect(jsonPath("$.categories.length()").value(count))
    }

    @When("I update the payee categories to a random category ID")
    fun updatePayeeCategoriesToRandom() {
        context.result = mockMvc.perform(
            patch("/api/v1/payees/$lastPayeeId")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"categoryIds": ["${UUID.randomUUID()}"]}""")
        )
    }

    @And("the first payee category has name {string} and icon {string}")
    fun firstPayeeCategoryHasDetails(name: String, icon: String) {
        context.result.andExpect(jsonPath("$[0].categories[0].name").value(name))
        context.result.andExpect(jsonPath("$[0].categories[0].icon").value(icon))
    }
}
