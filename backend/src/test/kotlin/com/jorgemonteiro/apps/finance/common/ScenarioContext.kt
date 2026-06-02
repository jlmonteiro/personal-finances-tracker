package com.jorgemonteiro.apps.finance.common

import io.cucumber.spring.ScenarioScope
import org.springframework.stereotype.Component
import org.springframework.test.web.servlet.ResultActions

/**
 * Scenario-scoped context shared between step definition classes.
 * A new instance is created for each Cucumber scenario.
 */
@Component
@ScenarioScope
class ScenarioContext {

    /** The latest HTTP response from a When step. */
    lateinit var result: ResultActions

    /** The ID of the last created resource (for subsequent operations). */
    var lastCreatedId: String? = null
}
