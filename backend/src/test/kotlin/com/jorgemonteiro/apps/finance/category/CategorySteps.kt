package com.jorgemonteiro.apps.finance.category

import com.jayway.jsonpath.JsonPath
import com.jorgemonteiro.apps.finance.common.ScenarioContext
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import java.util.UUID

/**
 * Cucumber step definitions for categories API.
 */
class CategorySteps {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var dsl: DSLContext

    @Autowired
    private lateinit var context: ScenarioContext

    private var lastCategoryId: String? = null

    @Before
    fun cleanup() {
        dsl.execute("TRUNCATE payee_categories CASCADE")
        dsl.execute("TRUNCATE categories CASCADE")
    }

    @Given("a category {string} with icon {string} exists")
    fun categoryExists(name: String, icon: String) {
        val response = mockMvc.perform(
            post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "$name", "icon": "$icon"}""")
        ).andReturn()

        val body = response.response.contentAsString
        lastCategoryId = JsonPath.read(body, "$.id")
        context.lastCreatedId = lastCategoryId
    }

    @When("I create a category with name {string} and icon {string}")
    fun createCategory(name: String, icon: String) {
        context.result = mockMvc.perform(
            post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "$name", "icon": "$icon"}""")
        )
    }

    @When("I list categories")
    fun listCategories() {
        context.result = mockMvc.perform(get("/api/v1/categories"))
    }

    @When("I update the category name to {string}")
    fun updateCategoryName(name: String) {
        context.result = mockMvc.perform(
            patch("/api/v1/categories/$lastCategoryId")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "$name"}""")
        )
    }

    @When("I update the category icon to {string}")
    fun updateCategoryIcon(icon: String) {
        context.result = mockMvc.perform(
            patch("/api/v1/categories/$lastCategoryId")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"icon": "$icon"}""")
        )
    }

    @When("I update the last category name to {string}")
    fun updateLastCategoryName(name: String) {
        context.result = mockMvc.perform(
            patch("/api/v1/categories/$lastCategoryId")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "$name"}""")
        )
    }

    @When("I delete the category")
    fun deleteCategory() {
        context.result = mockMvc.perform(delete("/api/v1/categories/$lastCategoryId"))
    }

    @When("I delete category with random ID")
    fun deleteCategoryWithRandomId() {
        context.result = mockMvc.perform(delete("/api/v1/categories/${UUID.randomUUID()}"))
    }

    @When("I update category with random ID name to {string}")
    fun updateCategoryWithRandomId(name: String) {
        context.result = mockMvc.perform(
            patch("/api/v1/categories/${UUID.randomUUID()}")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "$name"}""")
        )
    }

    @When("I create a category with a name of 101 characters and icon {string}")
    fun createCategoryWithLongName(icon: String) {
        val longName = "a".repeat(101)
        context.result = mockMvc.perform(
            post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "$longName", "icon": "$icon"}""")
        )
    }

    @When("I update the category with empty body")
    fun updateCategoryWithEmptyBody() {
        context.result = mockMvc.perform(
            patch("/api/v1/categories/$lastCategoryId")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{}""")
        )
    }

    @And("the first category name is {string}")
    fun firstCategoryNameIs(name: String) {
        context.result.andExpect(jsonPath("$[0].name").value(name))
    }

    @And("the second category name is {string}")
    fun secondCategoryNameIs(name: String) {
        context.result.andExpect(jsonPath("$[1].name").value(name))
    }

    @And("the response contains name {string}")
    fun responseContainsName(name: String) {
        context.result.andExpect(jsonPath("$.name").value(name))
    }

    @And("the response contains icon {string}")
    fun responseContainsIcon(icon: String) {
        context.result.andExpect(jsonPath("$.icon").value(icon))
    }

    @And("the response is an empty list")
    fun responseIsEmptyList() {
        context.result.andExpect(jsonPath("$").isArray)
        context.result.andExpect(jsonPath("$.length()").value(0))
    }

    @And("the response list has {int} items")
    fun responseListHasItems(count: Int) {
        context.result.andExpect(jsonPath("$.length()").value(count))
    }

    @And("the category no longer exists")
    fun categoryNoLongerExists() {
        mockMvc.perform(get("/api/v1/categories"))
            .andExpect(jsonPath("$.length()").value(0))
    }
}
