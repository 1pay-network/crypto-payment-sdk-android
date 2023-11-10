package network.onepay.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn = findViewById<Button>(R.id.btn_start)
        btn.setOnClickListener {
            PaymentDialog().show(supportFragmentManager, "payment_dialog")
        }
    }
}