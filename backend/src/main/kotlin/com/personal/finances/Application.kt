package com.personal.finances

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Main application class for the Personal Finances Tracker backend.
 */
@SpringBootApplication
class Application

/**
 * Application entry point.
 */
fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
