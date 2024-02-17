package ru.sample.duckapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.sample.duckapp.domain.Duck
import ru.sample.duckapp.infra.Api
import java.io.ByteArrayInputStream

class MainActivity : AppCompatActivity() {

    private lateinit var DuckButton: Button
    private lateinit var DuckimageView: ImageView
    private lateinit var DuckEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Находим элементы макета по их идентификаторам
        DuckButton = findViewById(R.id.DuckButton)
        DuckimageView = findViewById(R.id.DuckimageView)
        DuckEditText = findViewById(R.id.DuckEditText)

        // Устанавливаем слушатель кликов на кнопку
        DuckButton.setOnClickListener {
            loadDuck()
        }
    }

    private fun loadDuck() {
        val codeText = DuckEditText.text.toString().trim()

        if (codeText.isEmpty()) {
            // Если EditText пустой, загружаем случайную утку
            getDuck()
        } else {
            // Если EditText содержит текст, пытаемся его преобразовать в число
            val code = codeText.toIntOrNull()

            if (code != null) {
                // Если удалось преобразовать в число, загружаем утку по HTTP коду
                getCodeDuck(code.toString())
            } else {
                // Если не удалось преобразовать в число, показываем пользователю сообщение об ошибке
                Toast.makeText(this, "Неверный формат числа", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getDuck() {
        Api.ducksApi.getRandomDuck().enqueue(object : Callback<Duck> {
            override fun onResponse(call: Call<Duck>, response: Response<Duck>) {
                if (response.isSuccessful) {
                    val duck = response.body()
                    duck?.let {
                        Picasso.get().load(it.url).fit().centerInside().into(DuckimageView)
                    }
                } else {
                    showErrorToast()
                }
            }

            override fun onFailure(call: Call<Duck>, t: Throwable) {
                showErrorToast()
            }
        })
    }

    private fun getCodeDuck(duckCode: String) {
        Api.ducksApi.getCodeDuck(duckCode).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val duckByteArray = response.body()?.bytes()
                    duckByteArray?.let {
                        val inputStream = ByteArrayInputStream(it)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        DuckimageView.setImageBitmap(bitmap)
                    }
                } else {
                    showErrorToast()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showErrorToast()
            }
        })
    }

    private fun showErrorToast() {
        Toast.makeText(applicationContext, "Такая картинка отсутствует", Toast.LENGTH_LONG).show()
    }
}
