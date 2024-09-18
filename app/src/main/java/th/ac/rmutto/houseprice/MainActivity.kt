package th.ac.rmutto.houseprice

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.os.StrictMode
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    @SuppressLint("DefaultLocale")

    lateinit var gender : Spinner
    lateinit var hyper : Spinner
    lateinit var heart : Spinner
    lateinit var smoke : Spinner
    lateinit var bmi : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //To run network operations on a main thread or as an synchronous task.
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        gender = findViewById(R.id.spinGen)
        hyper = findViewById(R.id.spinhyper)
        heart = findViewById(R.id.spinheart)
        smoke = findViewById(R.id.spinsmoke)
        bmi = findViewById(R.id.editTextText)
        val btnPredict = findViewById<Button>(R.id.btnPredict)

        val adapGender = ArrayAdapter.createFromResource(
            this,
            R.array.gender,  // ใส่ชื่อของ array ที่ต้องการใช้
            android.R.layout.simple_spinner_item)
        adapGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapGender);

        val adapHyper = ArrayAdapter.createFromResource(
            this,
            R.array.hypertension,  // ใส่ชื่อของ array ที่ต้องการใช้
            android.R.layout.simple_spinner_item)
        adapHyper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hyper.setAdapter(adapHyper);

        val adapHeart = ArrayAdapter.createFromResource(
            this,
            R.array.heartdisease,  // ใส่ชื่อของ array ที่ต้องการใช้
            android.R.layout.simple_spinner_item)
        adapHyper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        heart.setAdapter(adapHeart);

        val adapSmoke = ArrayAdapter.createFromResource(
            this,
            R.array.smoke,  // ใส่ชื่อของ array ที่ต้องการใช้
            android.R.layout.simple_spinner_item)
        adapSmoke.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        smoke.setAdapter(adapSmoke);


        btnPredict.setOnClickListener {5
            if(bmi.text.isEmpty()){
                Toast.makeText(applicationContext, "กรุณากรอกค่า BMI", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val url: String = getString(R.string.root_url)

            val okHttpClient = OkHttpClient()
            val formBody: RequestBody = FormBody.Builder()
                .add("Gender", gender.selectedItemId.toString())
                .add("hypertension", hyper.selectedItemId.toString())
                .add("heartdisease", heart.selectedItemId.toString())
                .add("smoke", smoke.selectedItemId.toString())
                .add("bmi", bmi.text.toString())
                .build()
            val request: Request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val data = JSONObject(response.body!!.string())
                if (data.length() > 0) {
                    val stroke = data.getString("stroke")
                    val message = "ผลของคุณคือ $stroke"
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("ผลทำนาย")
                    builder.setMessage(message)
                    builder.setNeutralButton("OK", clearText())
                    val alert = builder.create()
                    alert.show()

                }
            } else {
                Toast.makeText(applicationContext, "ไม่สามารถเชื่อต่อกับเซิร์ฟเวอร์ได้", Toast.LENGTH_LONG).show()
            }
        }//button predict
    }//onCreate function

    private fun clearText(): DialogInterface.OnClickListener? {
        return DialogInterface.OnClickListener { dialog, which ->
            gender.setSelection(0)
            hyper.setSelection(0)
            heart.setSelection(0)
            smoke.setSelection(0)
            bmi.text.clear()
        }
    }

}//main class