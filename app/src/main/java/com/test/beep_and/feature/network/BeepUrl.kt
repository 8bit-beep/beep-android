package com.test.beep_and.feature.network


object BeepUrl {
    const val BASE_URL = "https://beepapi.com"
    const val DODAM_URL = "https://dauth.b1nd.com/api"

    const val AUTH = "$BASE_URL/users"
    const val SHIFT = "$BASE_URL/shifts"
    const val EMAIL = "$BASE_URL/email"
    const val ATTENDS = "$BASE_URL/attends"
    const val STUDENTS = "$BASE_URL/students"
    const val DAUTH = "$BASE_URL/dauth"
    const val BASIC_AUTH = "$BASE_URL/auth"

    object Auth {
        const val CARD = "$AUTH/card"
        const val GET_INFO = "$AUTH/me"
        const val DEL_INFO = "$AUTH/me"
        const val ROOM = "$AUTH/me/room"
        const val PAT_PASSWORD = "$AUTH/password/{uuid}"
    }

    object Shift {
        const val DEL = "$SHIFT/{shiftId}"
        const val GET = SHIFT
        const val GET_MY = "$SHIFT/me"
        const val PAT = SHIFT
        const val PAT_STAT = "$SHIFT/{shiftId}/status"
        const val POST = SHIFT
    }

    object Email {
        const val VERIFY_EMAIL = "$EMAIL/verify"
        const val SEND_EMAIL = "$EMAIL/send"
    }

    object DAuth {
        const val LOGIN = "$DAUTH/login"
        const val REFRESH = "$BASIC_AUTH/refresh"
        const val D_LOGIN = "$DODAM_URL/auth/login"
    }

    object Attends {
        const val POST_ATTENDS = ATTENDS
        const val CARD_ATTENDS = "$ATTENDS/card"
        const val CANCEL_ATTENDS = "$ATTENDS/cancel"
    }

    object Students {
        const val GET_ROOM = "$STUDENTS/room"
        private const val NOT_ATTEND = "$STUDENTS/not-attend"
        const val NOT_ATTEND_ROOM = "$NOT_ATTEND/room"
        const val NOT_ATTEND_CLASS = "$NOT_ATTEND/class"
        const val PRE_ATTEND = "$STUDENTS/pre-attend"
        const val ATTEND_STAT = "$STUDENTS/attend-status"
    }


}