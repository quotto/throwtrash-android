package com.example.mythrowtrash.usecase

class Validator {
    companion object {
        const val RESULT_VALID:Int = 0
        const val RESULT_EMPTY:Int = 1
        const val RESULT_OVER_CHAR:Int = 2
        const val RESULT_INVALID_CHAR:Int = 3
        fun validateOtherText(text:String):Int {
            var resultCode: Int = -1
            if(text.isEmpty()) {
                return RESULT_EMPTY
            }
            else if(text.length > 10) {
                return RESULT_OVER_CHAR
            } else if(Regex("^[A-z0-9Ａ-ｚ０-９ぁ-んァ-ヶー一-龠\\s]+$").find(text)?.value == null) {
                return RESULT_INVALID_CHAR
            }
            return RESULT_VALID
        }
    }
}