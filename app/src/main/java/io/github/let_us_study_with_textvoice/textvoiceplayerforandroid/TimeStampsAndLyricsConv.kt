package io.github.let_us_study_with_textvoice.textvoiceplayerforandroid

import android.util.Log

object TimeStampsAndLyricsConv {
    fun separateTimeStamp(text: String): MutableList<TimeStamp> {
        var stsText = text
//    stsText = "　　　　京都大学申请iPS细胞临床试验[00:01.03]　　日本京都大学研究人员的研究表明，[/00:03.74][00:04.10]把利用iPS细胞形成的血液成分[/00:07.42][00:07.68]注入再生不良性贫血的患者体内后[/00:11.00]\n"

        // タイムスタンプの書式（正規表現様）
        val regexStart = Regex("""\[\d{2}:\d{2}\.\d{2}]""")
        val regexPause = Regex("""\[/\d{2}:\d{2}\.\d{2}]""")

        // STSテキストの中に含まれるタイムスタンプをすべて抽出する
        val timeStampsStart = regexStart.findAll(stsText)
        println("timeStampsStart:${timeStampsStart}")
        timeStampsStart.forEach { println(it.toString()) }
        val timeStampsPause = regexPause.findAll(stsText)

        // STSテキストの中に含まれているPauseのタイムスタンプをすべて除去する
        timeStampsPause.forEach { stsText = stsText.replace(it.value, "") }

        // startTimesのミュータブルリストを作る
        val startTimes: MutableList<Int> = mutableListOf()
        timeStampsStart.forEach { startTimes.add(timeStampToMilisec(it)) }
        startTimes.add(0, 0)

        // pauseTimesのミュータブルリストを作る
        val pauseTimes: MutableList<Int> = mutableListOf()
        timeStampsPause.forEach { pauseTimes.add(timeStampToMilisec(it)) }
        pauseTimes.add(0, 0)


        var sentences: MutableList<String> = mutableListOf()
        timeStampsStart.forEach {
            sentences = stsText.split(regexStart) as MutableList<String>
        }

        var timeStamps: MutableList<TimeStamp> = mutableListOf()
        var begin: Int  = 0
        var end: Int = 0
        for (i in 0..sentences.size - 1){
            if(i > 0){
                begin = end
                end = begin + sentences[i].length - 1
            }
            timeStamps.add(TimeStamp(startTimes[i], pauseTimes[i], sentences[i],begin,end))
            Log.d("timeStamp","i = $i,  ${timeStamps[i].sentence},  ${timeStamps[i].numberOfBegin},  ${timeStamps[i].numberOfEnd}")
            end++
        }

        return timeStamps
    }

    // [00:01.03]または[/00:03.74]形式のタイムスタンプをミリ秒単位の値に変換する
    private fun timeStampToMilisec(timeStamp: MatchResult): Int {
        val splitedTimeStamp: List<String>  // 分と秒に分割し、それを入れるリスト、要素[0]：分を入れる、要素[1]：秒(00.00)を入れる
        val stringTimeStamp = timeStamp.value
        splitedTimeStamp = stringTimeStamp
            .replace("[", "")
            .replace("/", "")
            .replace("]", "")
            .split(":")

        return splitedTimeStamp[0].toInt() * 60000 + (splitedTimeStamp[1].toDouble() * 1000).toInt()
    }
}
