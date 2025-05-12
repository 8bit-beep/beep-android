package com.test.beep_and.feature.network.user.model

data class RoomModel(val id: Int, val name: String, val club: String)

class Room {

    val roomList = listOf(
        RoomModel(id = 1, name = "PROJECT3", club = "CHATTY"),
        RoomModel(id = 2, name = "PROJECT4", club = "DUCAMI"),
        RoomModel(id = 3, name = "PROJECT5", club = "BIND"),
        RoomModel(id = 4, name = "PROJECT6", club = "SAMDI"),
        RoomModel(id = 5, name = "LAB15_16", club = "MODI"),
        RoomModel(id = 6, name = "LAB17_18", club = "CNS"),
        RoomModel(id = 7, name = "LAB19_20", club = "LOUTER"),
        RoomModel(id = 8, name = "LAB21_22", club = "ALT"),
    )

    val parseRoomName: (String) -> String = { name ->
        val club = roomList.find { it.name == name }?.club ?: ""
        when {
            name.startsWith("PROJECT") -> {
                val number = name.removePrefix("PROJECT")
                "프로젝트 $number ($club)"
            }
            name.startsWith("LAB") -> {
                val numbers = name.removePrefix("LAB").split("_")
                if (numbers.size == 2) {
                    "랩 ${numbers[0]}, ${numbers[1]} ($club)"
                } else {
                    name
                }
            }
            else -> name
        }
    }

    val parseOnlyRoom: (String) -> String = { name ->
        when {
            name.startsWith("PROJECT") -> {
                val number = name.removePrefix("PROJECT")
                "프로젝트 $number"
            }
            name.startsWith("LAB") -> {
                val numbers = name.removePrefix("LAB").split("_")
                if (numbers.size == 2) {
                    "랩 ${numbers[0]}, ${numbers[1]}"
                } else {
                    name
                }
            }
            else -> name
        }
    }
}
