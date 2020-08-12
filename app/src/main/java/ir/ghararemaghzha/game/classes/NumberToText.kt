package ir.ghararemaghzha.game.classes

public fun toEnglishFormat(input:Int): String {
    var number = input.toString()

    number = when (number.length % 3) {
        1 -> "00$number"
        2 -> "0$number"
        else -> number
    }

    val digits = getDigits()
    val result = StringBuilder()
    var res: StringBuilder
    var len = number.length
    var count = 0
    var sum: Int
    while (len > 0) {
        sum = 0
        res = StringBuilder()
        val three = number.substring(len - 3, len)

        for (i in 0..2) {
            val num = three[i].toInt() - '0'.toInt()
            sum += num
            if (num == 0) continue
            if (i == 1) {
                val nextNum = three[i + 1].toInt() - '0'.toInt()
                if (num == 1 && nextNum != 0) {
                    sum += nextNum
                    res.append(digits[nextNum - 1].fourth)
                    break
                }
            }
            res.append(digits[num - 1].get(i))
        }
        if (sum != 0) {
            when (count) {
                1 -> res.append("هزار و ")
                2 -> res.append("میلیون و ")
                3 -> res.append("میلیارد و ")
                4 -> res.append("تریلیون و ")
                5 -> res.append("کوادتریلیون و ")
                6 -> res.append("کوینترلیون و ")
                7 -> res.append("سیکستریلیون و ")
            }
        }
        result.insert(0, res.toString())
        count++
        len -= 3


    }
    return if (result.toString().endsWith(" و ")) result.toString().substring(0, result.toString().length - 3) else result.toString()

}

data class Types(val first: String, val second: String, val third: String, val fourth: String) {
    fun get(i: Int) = when (i) {
        0 -> third
        1 -> second
        2 -> first
        else -> "صفر"
    }
}

fun getDigits(): List<Types> {
    val digits = mutableListOf<Types>()
    digits.add(Types("یک ", "ده ", "صد ", "یازده "))
    digits.add(Types("دو ", "بیست ", "دویست ", "دوازده "))
    digits.add(Types("سه  ", "سی ", "یسصد ", "سیزده "))
    digits.add(Types("چهار  ", "چهل ", "چهارصد ", "چهارده "))
    digits.add(Types("پنج  ", "پنجاه ", "پانصد ", "پانزده "))
    digits.add(Types("شش ", "شصت ", "ششصد ", "شانزده "))
    digits.add(Types("هفت  ", "هفتاد ", "هفتصد ", "هفده "))
    digits.add(Types("هشت ", "هشتاد ", "هشتصد ", "هجده "))
    digits.add(Types("نه ", "نود ", "نهصد ", "نوزده "))
    return digits


}