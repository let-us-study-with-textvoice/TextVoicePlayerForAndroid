package io.github.let_us_study_with_textvoice.textvoiceplayerforandroid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    lateinit var startForResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val textSTS = findViewById<TextView>(R.id.tvTextSTS)
        // Text Selection をenableにし、カーソルが有効になるようにする。(https://akira-watson.com/android/text-selection.html)
        textSTS.setTextIsSelectable(true)

        // 選択画面を起動
        startForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            Log.d("openMenu", "OpenSTS3")

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
                        }
                    // StringBuilderの内容をテキストエリアに反映
                    Log.d("logstr", str.toString())
                    val timeStampsAndLirycsConverter =
                        TimeStampsAndLirycsConverter.separateTimeStamp(str.toString())
                    var senText = ""
                    for (i in 1..timeStampsAndLirycsConverter.sentences.size - 1) {
                        senText += timeStampsAndLirycsConverter.sentences[i]
                    }
                    textSTS.setText(senText)
                }
            }
        }

        // textSTSをタップしたときの処理
        textSTS.setOnClickListener {
            val k = textSTS.selectionStart
            val n = textSTS.selectionEnd
            Log.d("Select", "Start:$k   End:$n  True:${k==n}")
        }
    }

    // StrorageAccessFramewokeを使って、ファイルを選択する
    fun openSTS_a() {
        startForResult.launch(
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TITLE, "textSTS.txt")
                // Android端末の\sdscar\Downloadディレクトリに何かテキストファイルを入れておく。例えば、textSTS.txt
                // testSTS.txtは、本プロジェクトフォルダの直下にある。
            }
        )
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

    fun openVoice() {

    }


    fun openVoiceSTS() {}
}