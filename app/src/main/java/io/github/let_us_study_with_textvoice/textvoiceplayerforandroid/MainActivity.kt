package io.github.let_us_study_with_textvoice.textvoiceplayerforandroid

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.HandlerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.IOException
import java.util.Timer
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {
    lateinit var getContentSTS: ActivityResultLauncher<Intent>
    lateinit var getContentSound: ActivityResultLauncher<Intent>
    val mediaPlayer = MediaPlayer()
    lateinit var btnPlay: Button
    lateinit var btnStop: Button
    lateinit var tvCurPos: TextView
    lateinit var textSTS: TextView
    lateinit var timeStamps: MutableList<TimeStamp>
    lateinit var timerCurPos: Timer

//    // 音声ファイルをファイルピッカーで開く
//    val getContentSound =
//        registerForActivityResult(StartActivityForResult()) {
//            if (it.resultCode == RESULT_OK) {
//                val resultIntent = it.data
//                val uri: Uri? = resultIntent?.data
//
//                mediaPlayer.apply {
//                    stop()
//                    reset()
//                }
//
//                if (uri != null) {
//                    mediaPlayer.apply {
//                        setDataSource(this@MainActivity, uri)   // 音源を設定
//                        //メディアソースの再生準備が整ったときに呼び出されるコールバックの登録する
//                        setOnPreparedListener {
//                            // 各ボタンをタップ可能に設定
//                            btnPlay.setEnabled(true)
//                            btnPlay.text = getString(R.string.play)
//                            btnStop.setEnabled(true)
//                        }
//
//                        // 再生中にメディアソースの終端に到達したときに呼び出されるコールバックを登録
//                        setOnCompletionListener {
//                            // ループ設定がされていなければ
//                            if (!isLooping()) {
//                                // 再生ボタンのラベルを「再生」に設定
//                                btnPlay.text = getString(R.string.play)
//                            }
//                        }
//
//                        // 非同期でメディア再生を準備
//                        prepareAsync()
//                    }
//                }
//            }
//        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Log.d("mediaPlayer2_2_1", mediaPlayer.duration.toString())


        btnPlay = findViewById<Button>(R.id.btnPlay)
        btnPlay.setEnabled(false)
        btnPlay.text = getString(R.string.play)

        btnStop = findViewById<Button>(R.id.btnStop)
        btnStop.text = getString(R.string.stop)
        btnStop.setEnabled(false)

        tvCurPos = findViewById<TextView>(R.id.tvCurPos)


        textSTS = findViewById<TextView>(R.id.tvTextSTS)
        // Text Selection をenableにし、カーソルが有効になるようにする。(https://akira-watson.com/android/text-selection.html)
        textSTS.setTextIsSelectable(true)
        Log.d("mediaPlayer2_2_2", mediaPlayer.duration.toString())

        // 音声ファイルをファイルピッカーで開く
        getContentSound =
            registerForActivityResult(StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val resultIntent = it.data
                    val uri: Uri? = resultIntent?.data

                    mediaPlayer.apply {
                        stop()
                        reset()
                    }

                    if (uri != null) {
                        mediaPlayer.apply {
                            setDataSource(this@MainActivity, uri)   // 音源を設定
                            //メディアソースの再生準備が整ったときに呼び出されるコールバックの登録する
                            setOnPreparedListener {
                                // 各ボタンをタップ可能に設定
                                btnPlay.setEnabled(true)
                                btnPlay.text = getString(R.string.play)
                                btnStop.setEnabled(true)
                            }

                            // 再生中にメディアソースの終端に到達したときに呼び出されるコールバックを登録
                            setOnCompletionListener {
                                // ループ設定がされていなければ
                                if (!isLooping()) {
                                    // 再生ボタンのラベルを「再生」に設定
                                    btnPlay.text = getString(R.string.play)
                                }
                            }

                            // 非同期でメディア再生を準備
                            prepareAsync()
                        }
                    }
                }
            }

        // STSファイルを選択する選択画面(filePicker)を起動
        getContentSTS = registerForActivityResult(
            StartActivityForResult()
        ) { result ->
            Log.d("openMenu", "OpenSTS3")
            Log.d("mediaPlayer2_2", mediaPlayer.duration.toString())
            // 選択時の処理
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let {
                    val str = StringBuilder()
                    // ファイルから行単位に読み込み、その内容をStringBuilderに保存
                    contentResolver.openInputStream(it)
                        ?.bufferedReader()
                        ?.forEachLine {
                            str.append(it)
                            Log.d("logstr", str.toString())
                            str.append(System.lineSeparator())
                            Log.d("logstr", str.toString())
                            Log.d("mediaPlayer2_3", mediaPlayer.duration.toString())

                        }
                    // StringBuilderの内容をテキストエリアに反映
                    Log.d("logstr", str.toString())
                    timeStamps =
                        TimeStampsAndLyricsConv.separateTimeStamp(str.toString())
                    Log.d("mediaPlayer2_4", mediaPlayer.duration.toString())

                    var senText = ""
                    for (i in 1..timeStamps.size - 1) {
                        senText += timeStamps[i].sentence
                        timeStamps[i].startTimeMiliSec
                        timeStamps[i].pauseTimeMiliSec
                    }
                    textSTS.setText(senText)
                    Log.d("mediaPlayer2_5", mediaPlayer.duration.toString())

                    // アクションバーのタイトルに本文のタイトルを表示(https://qiita.com/cozyk100/items/8aa1c622f3437e73e46b)
                    // 表題からスペースを削除（https://www.choge-blog.com/programming/kotlinstringremovewhitspace/）
                    supportActionBar?.setTitle(timeStamps[0].sentence.filterNot { it.isWhitespace() })
                    Log.d("mediaPlayer2_6", mediaPlayer.duration.toString())

                }
            }
        }

        // textSTSをタップしたときの処理
        textSTS.setOnClickListener {
            val k = textSTS.selectionStart
            val n = textSTS.selectionEnd
            Log.d("Select", "Start:$k   End:$n  True:${k == n}")
        }
//
//        btnPlay = findViewById<Button>(R.id.btnPlay)
//        btnStop = findViewById<Button>(R.id.btnStop)
//        tvCurPos = findViewById<TextView>(R.id.tvCurPos)

        // Play・Pauseボタンを押した時
        btnPlay.setOnClickListener {
            if (!mediaPlayer.isPlaying) {   // Pause又はStop状態の時
                try {
                    Log.d("mediaPlayer2", mediaPlayer.toString())
                    Log.d("mediaPlayer2_1", mediaPlayer.duration.toString())
                    mediaPlayer.start()
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                } finally {
                    if (mediaPlayer.isPlaying) btnPlay.text = getString(R.string.pause)
                }
            } else {    // Play状態の時
                try {
                    // 再生を一時停止
                    mediaPlayer.pause()
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                } finally {
                    if (!mediaPlayer.isPlaying) btnPlay.text = getString(R.string.play)
                }
            }
        }

        // Stopボタンを押した時
        btnStop.setOnClickListener {
            try {
                mediaPlayer.stop()
                mediaPlayer.prepare()   // mediaPlayerを初期状態に戻す（再生位置は0に戻る）
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (!mediaPlayer.isPlaying) btnPlay.text = getString(R.string.play)
            }
        }

        // 再生経過時間を表示する
        timerCurPos = timer(name = "curPos", period = 50L) {
            HandlerCompat.createAsync(mainLooper).post {
                tvCurPos.text =
                    "再生経過時間:" + convertMillisTo60(mediaPlayer.currentPosition)  // 一時停止した時にcurrentPosition(ミリ秒)を60進数に変換し表示する
                if (mediaPlayer.isPlaying) {
                    var senText = ""
                    for (i in 1..timeStamps.size - 1) {
                        if (mediaPlayer.currentPosition >= timeStamps[i].startTimeMiliSec && mediaPlayer.currentPosition < timeStamps[i + 1].startTimeMiliSec) {

                        } else {

                            senText += timeStamps[i].sentence
                        }
                    }
                    textSTS.setText(senText)
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("itemtitle", item.title.toString())

        when (item.itemId) {
            R.id.menuListOptionOpenVoice -> {
                Log.d("openMenu", "OpenVoice")
                openVoice()
            }

            R.id.menuListOptionOpenSTS -> {
                Log.d("openMenu", "OpenSTS1")
                openSTS_a()
                Log.d("openMenu", "OpenSTS2")

            }

            R.id.menuListOptionOpenVoiceSTS -> {
                Log.d("openMenu", "OpenVoiceSTS")
                openVoiceSTS()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    // アプリを一時的に隠した時の処理
    override fun onPause() {
        super.onPause()
//        try {
//            mediaPlayer.pause()
//        } catch (e: IllegalStateException) {
//            e.printStackTrace()
//        }
    //      ver_0.004 音声ファイルを開いたのち、本文ファイルを開くと音声ファイルの設定が消える原因は、
    //      本文のファイルピッカーがTextVoicePlayerアプリの上に重なり、fun onPause()が呼ばれる。
    //      fun onPause()の中で
    //      try {mediaPlayer.pause()} catch (e:IllegalStateException) {e.printStackTrace()}を
    //      実行していたて、mediaPlayer.pause()を実行した時に、IllegalStateExceptionが発生。
    //      fun onPause()の中から、
    //      try {mediaPlayer.pause()} catch (e:IllegalStateException) {e.printStackTrace()}を
    //      削除して解消。
    }


    override fun onDestroy() {
        Log.d("TVPonDestroy", "before_cancel")
        timerCurPos.cancel()      // timer(name = "curPos")をcancelする事により、
                        // mediaPlayer.currentPositionを呼ばなくなったため
                        // mediaPlayer.currentPositionで発生していた
                        //      IllegalStateExceptionエラーは
                        //          at android.media.MediaPlayer.getCurrentPosition(Native Method)
                        //          at io.github.let_us_study_with_textvoice.textvoiceplayerforandroid.MainActivity$onCreate$7$1.run(MainActivity.kt:240)
                        //                    tvCurPos.text = "再生経過時間:" + convertMillisTo60(mediaPlayer.currentPosition)
                        // 発生しなくなった。
        Log.d("TVPonDestroy", "after_cancel")
        mediaPlayer?.let{
            if(it.isPlaying){
                it.stop()
            }
            it.release()
        }
        // ボタンを初期化する
        btnPlay.setEnabled(false)
        btnPlay.text = getString(R.string.pause)
        btnStop.setEnabled(false)


        super.onDestroy()
    }


    fun openVoice() {
        // 暗黙インテントにより既成のファイルピッカーを呼び出す
        val iSound = Intent(Intent.ACTION_OPEN_DOCUMENT)
        iSound.type = "audio/mpeg"
        iSound.putExtra(Intent.EXTRA_TITLE, "memo.mp3")
        getContentSound.launch(iSound)
    }


    fun openVoiceSTS() {

    }

    // StrorageAccessFramewokeを使って、ファイルを選択する
    fun openSTS_a() {
        getContentSTS.launch(
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TITLE, "textSTS.txt")
                // Android端末の\sdscar\Downloadディレクトリに何かテキストファイルを入れておく。例えば、textSTS.txt
                // testSTS.txtは、本プロジェクトフォルダの直下にある。
            }
        )
    }


    // ミリ秒ー＞60進数変換
    // https://pisuke-code.com/android-ways-to-format-millis/
    fun convertMillisTo60(millis: Int): String? {
        val l = millis % 1000
        val s = millis / 1000 % 60
        val m = millis / 1000 / 60 % 60
        var h = millis / 1000 / (60 * 60) % 24
        h += millis / 1000 / (60 * 60 * 24) * 24
        return String.format("%d:%02d:%02d.%02d", h, m, s, l)
    }


}