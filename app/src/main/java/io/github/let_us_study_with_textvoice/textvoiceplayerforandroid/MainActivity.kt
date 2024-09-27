package io.github.let_us_study_with_textvoice.textvoiceplayerforandroid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Objects

class MainActivity : AppCompatActivity() {
    lateinit var textSTS: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        textSTS = findViewById<TextView>(R.id.textSTS)

        openSTS()
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
                //openSTS()
            }

            R.id.menuListOptionOpenVoiceSTS -> {
                Log.d("openMenu", "OpenVoiceSTS")
                openVoiceSTS()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun openVoice() {}

    fun openSTS2() {
        val i: Intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        i.setType("text/plane")
        i.putExtra(Intent.EXTRA_TITLE, "memo.txt")
        startActivityForResult(i, 2)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        val title = Objects.requireNonNull(
//            data!!.data
//        )
//
//        var str = StringBuffer()
//        try {
//            val a = title?.let { getContentResolver().openInputStream(it) }
//            val b = InputStreamReader(a)
//            val reader = BufferedReader(b)
//            var line: String
//            while ((line = reader.readLine()) != null) {
//                str.append(line)
//                str.append(System.getProperty("line.separator"))
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//
//        // StringBuilderの内容をテキストエリアに反映
//        Log.d("logstr", str.toString())
//        textSTS.setText(str.toString())
//    }

    // StrorageAccessFramewokeを使って、ファイルを選択する
    fun openSTS() {
        Log.d("openMenu", "OpenSTS2")

        // 選択画面を起動
        registerForActivityResult(
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
                            str.append(System.getProperty("line.separator"))
                            Log.d("logstr", str.toString())
                        }
                    // StringBuilderの内容をテキストエリアに反映
                    Log.d("logstr", str.toString())
                    textSTS.setText(str.toString())
                }
            }
        }.launch(
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TITLE, "textSTS.txt")
                // Android端末の\sdscar\Downloadディレクトリに何かテキストファイルを入れておく。例えば、textSTS.txt
                // testSTS.txtは、本プロジェクトフォルダの直下にある。
            }
        )

    }

    fun openVoiceSTS() {}
}