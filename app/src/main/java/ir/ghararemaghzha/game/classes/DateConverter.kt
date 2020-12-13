package ir.ghararemaghzha.game.classes

class DateConverter {
    var day = 0
    var month: Int = 0
    var year: Int = 0

    private var jYear = 0
    private var jMonth: Int = 0
    private var jDay: Int = 0
    private var gYear = 0
    private var gMonth: Int = 0
    private var gDay: Int = 0
    private var leap = 0
    private var march: Int = 0


    override fun toString(): String = String.format("%04d/%02d/%02d", year, month, day)

    fun gregorianToPersian(year: Int, month: Int, day: Int) {
        val jd: Int = jG2JD(year, month, day)
        jD2Jal(jd)
        this.year = jYear
        this.month = jMonth
        this.day = jDay
    }

    private fun jG2JD(year: Int, month: Int, day: Int, j1G0: Int=0): Int {
        var jd = (1461 * (year + 4800 + (month - 14) / 12) / 4
                + 367 * (month - 2 - 12 * ((month - 14) / 12)) / 12
                - 3 * ((year + 4900 + (month - 14) / 12) / 100) / 4 + day
                - 32075)
        if (j1G0 == 0) {
            jd = jd - (year + 100100 + (month - 8) / 6) / 100 * 3 / 4 + 752
        }
        return jd
    }

    private fun jD2JG(JD: Int, j1G0: Int=0) {
        val i: Int
        var j: Int
        j = 4 * JD + 139361631
        if (j1G0 == 0) {
            j = j + (4 * JD + 183187720) / 146097 * 3 / 4 * 4 - 3908
        }
        i = j % 1461 / 4 * 5 + 308
        gDay = i % 153 / 5 + 1
        gMonth = i / 153 % 12 + 1
        gYear = j / 1461 - 100100 + (8 - gMonth) / 6
    }

    private fun jD2Jal(JDN: Int) {
        jD2JG(JDN)
        jYear = gYear - 621
        jalCal(jYear)
        val jDN1F = jG2JD(gYear, 3, march, 0)
        var k = JDN - jDN1F
        if (k >= 0) {
            if (k <= 185) {
                jMonth = 1 + k / 31
                jDay = k % 31 + 1
                return
            } else {
                k -= 186
            }
        } else {
            jYear -= 1
            k += 179
            if (leap == 1) {
                k += 1
            }
        }
        jMonth = 7 + k / 30
        jDay = k % 30 + 1
    }

    private fun jalCal(jY: Int) {
        march = 0
        leap = 0
        val breaks = intArrayOf(-61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181, 1210,
                1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178)
        gYear = jY + 621
        var leapJ = -14
        var jp = breaks[0]
        var jump: Int
        for (j in 1..19) {
            val jm = breaks[j]
            jump = jm - jp
            if (jY < jm) {
                var n = jY - jp
                leapJ += n / 33 * 8 + (n % 33 + 3) / 4
                if (jump % 33 == 4 && jump - n == 4) {
                    leapJ += 1
                }
                val leapG = gYear / 4 - (gYear / 100 + 1) * 3 / 4 - 150
                march = 20 + leapJ - leapG
                if (jump - n < 6) {
                    n = n - jump + (jump + 4) / 33 * 33
                }
                leap = ((n + 1) % 33 - 1) % 4
                if (leap == -1) {
                    leap = 4
                }
                break
            }
            leapJ += jump / 33 * 8 + jump % 33 / 4
            jp = jm
        }
    }

}