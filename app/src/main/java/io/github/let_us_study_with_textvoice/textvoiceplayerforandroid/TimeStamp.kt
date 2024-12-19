package io.github.let_us_study_with_textvoice.textvoiceplayerforandroid

data class TimeStamp(
    val startTimeMiliSec: Int,
    val pauseTimeMiliSec: Int,
    val sentence: String,
    val numberOfBegin: Int,     // センテンスの始まりの文字を表す、本文の先頭からの文字数
    val numberOfEnd: Int        // センテンスの終わりの文字を表す、本文の先頭からの文字数
)
