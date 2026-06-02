package com.jorgemonteiro.apps.finance.exception

/**
 * Base exception for all application exceptions.
 */
abstract class BaseException(message: String) : RuntimeException(message)

/**
 * Base for business logic exceptions (invalid operations, rule violations).
 */
abstract class BusinessException(message: String) : BaseException(message)

/**
 * Base for entity not found exceptions. Maps to HTTP 404.
 */
abstract class EntityNotFoundException(message: String) : BusinessException(message)

/**
 * Base for entity conflict exceptions (duplicates, constraint violations). Maps to HTTP 409.
 */
abstract class EntityConflictException(message: String) : BusinessException(message)

/**
 * Base for validation exceptions (invalid business rules). Maps to HTTP 422.
 */
abstract class ValidationException(message: String, val errors: List<String>) : BusinessException(message)
