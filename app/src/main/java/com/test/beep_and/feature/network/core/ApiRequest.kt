package com.test.beep_and.feature.network.core


import com.test.beep_and.feature.network.core.exception.BadRequestException
import com.test.beep_and.feature.network.core.exception.ConflictException
import com.test.beep_and.feature.network.core.exception.DataNotFoundException
import com.test.beep_and.feature.network.core.exception.ForbiddenException
import com.test.beep_and.feature.network.core.exception.InternalServerException
import com.test.beep_and.feature.network.core.exception.LockedException
import com.test.beep_and.feature.network.core.exception.NotFoundException
import com.test.beep_and.feature.network.core.exception.TooManyRequestsException
import com.test.beep_and.feature.network.core.exception.UnauthorizedException
import com.test.beep_and.feature.network.core.model.DefaultResponse
import com.test.beep_and.feature.network.core.model.Response

//suspend inline fun <T> safeRequest(crossinline request: suspend () -> Response<T>): T {
//    val response = request()
//    return when (response.status) {
//        200, 201, 204 -> response.data ?: throw DataNotFoundException(response.message)
//        400 -> throw BadRequestException(response.message)
//        401 -> throw UnauthorizedException(response.message)
//        403 -> throw ForbiddenException(response.message)
//        404 -> throw NotFoundException(response.message)
//        409 -> throw ConflictException(response.message)
//        423 -> throw LockedException(response.message)
//        429 -> throw TooManyRequestsException(response.message)
//        500 -> throw InternalServerException(response.message, response.status)
//        else -> throw Exception(response.message)
//    }
//}
//
//suspend inline fun defaultSafeRequest(crossinline request: suspend () -> DefaultResponse<T>): T {
//    val response = request()
//    return when (response.status) {
//        200, 201, 204 -> Unit
//        400 -> throw BadRequestException(response.message)
//        401 -> throw UnauthorizedException(response.message)
//        403 -> throw ForbiddenException(response.message)
//        404 -> throw NotFoundException(response.message)
//        409 -> throw ConflictException(response.message)
//        423 -> throw LockedException(response.message)
//        429 -> throw TooManyRequestsException(response.message)
//        500 -> throw InternalServerException(response.message, response.status)
//        else -> throw Exception(response.message)
//    }
//}