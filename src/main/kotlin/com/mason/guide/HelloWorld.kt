package com.mason.guide

/**
 * Created by mwu on 2020/4/17
 */
data class KingDonateProgress(
    var stage: Int,
    var progress: Long
)

val stageProgressMap = hashMapOf(
    1 to 1000L,
    2 to 2000L,
    3 to 3000L,
    4 to 4000L,
    5 to 5000L
)

fun main() {
//    val donateProgress = KingDonateProgress(5, 4000)
//    println(donateProgress)
//    testKingDonateProgress(donateProgress, 10000)
//    println(donateProgress)
    println(hashMapOf<Int, Int>().values.find { it > 0 })
}

fun testKingDonateProgress(donateProgress: KingDonateProgress, addProgress: Long) {
    donateProgress.progress += addProgress
    while (true) {
        val oldStage = donateProgress.stage
        val oldStageTotalProgress = stageProgressMap[oldStage] ?: 0L
        if (donateProgress.progress < oldStageTotalProgress) {
            break
        }
        if (donateProgress.stage > 5) {
            donateProgress.stage = 5
            donateProgress.progress = stageProgressMap[5] ?: 0L
            break
        } else {
            println("${oldStage} --> $oldStageTotalProgress")
            donateProgress.stage += 1
            donateProgress.progress -= oldStageTotalProgress
        }
    }
}