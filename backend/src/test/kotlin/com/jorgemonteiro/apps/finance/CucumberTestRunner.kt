package com.jorgemonteiro.apps.finance

import org.junit.platform.suite.api.ConfigurationParameter
import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectPackages
import org.junit.platform.suite.api.Suite

/**
 * JUnit Platform suite that runs all Cucumber features.
 */
@Suite
@IncludeEngines("cucumber")
@SelectPackages("com.jorgemonteiro.apps.finance")
@ConfigurationParameter(key = "cucumber.glue", value = "com.jorgemonteiro.apps.finance")
@ConfigurationParameter(key = "cucumber.features", value = "classpath:features")
@ConfigurationParameter(key = "cucumber.plugin", value = "pretty, json:build/reports/cucumber/cucumber.json, html:build/reports/cucumber/cucumber.html")
class CucumberTestRunner
