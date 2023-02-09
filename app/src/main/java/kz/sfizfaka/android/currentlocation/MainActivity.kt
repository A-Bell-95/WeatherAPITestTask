package kz.sfizfaka.android.currentlocation

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kz.sfizfaka.android.currentlocation.databinding.ActivityMainBinding
import org.json.JSONObject



const val API_KEY_1 = "6d65b753875f483dac4115003230702"
const val API_KEY_2 = "BPVQKAYC45GRK39X6RMU3UHL8"
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var latitude: TextView
    private lateinit var longitude: TextView
    private lateinit var temperature: TextView
    private lateinit var city: TextView
    private lateinit var updata: TextView
    private var lat = 0.0
    private var lon = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        latitude = findViewById(R.id.textView2)
        longitude = findViewById(R.id.textView3)
        temperature = findViewById(R.id.temperature)
        city = findViewById(R.id.city)
        updata = findViewById(R.id.upDate)

        getLocation()

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> getResult1(lat,lon)
                    1 -> getResult2(lat,lon)
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

    }
    private fun getLocation(){

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),100)
                    return
        }
        val location = fusedLocationProviderClient.lastLocation
        location.addOnSuccessListener {
            if (it != null){
            val textLatitude = it.latitude.toString()
            val textLongtitude = it.longitude.toString()
            lat = it.latitude
            lon = it.longitude
            latitude.text = textLatitude
            longitude.text = textLongtitude

            }
        }
    }
    private fun getResult1(latitude: Double,longitude: Double){
        val url = "https://api.weatherapi.com/v1/current.json?" +
                "key=$API_KEY_1&q=$latitude,$longitude&aqi=no"
        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(Request.Method.GET,
            url,
            {
                response ->
                val obj = JSONObject(response)
                val temp = obj.getJSONObject("current").getString("temp_c")
                val cit = obj.getJSONObject("location").getString("name")
                val upd = obj.getJSONObject("current").getString("last_updated")
                when(obj.getJSONObject("current").getJSONObject("condition").getString("text")) {
                    "Sunny" -> binding.imageView.setImageResource(R.drawable.icons8_sun_100)
                    "Rain" -> binding.imageView.setImageResource(R.drawable.icons8_rain_100)
                    "Snow" -> binding.imageView.setImageResource(R.drawable.icons8_snow_100)
                }
                temperature.text = "$temp °C"
                city.text = cit
                updata.text = "Last updated:$upd"
            },
            {
                Toast.makeText(applicationContext, "ERROR with API", Toast.LENGTH_LONG).show()
            }
            )
        queue.add(stringRequest)
    }
    private fun getResult2(latitude: Double,longitude: Double){
        val url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" +
                "$latitude,$longitude?unitGroup=metric&key=$API_KEY_2&contentType=json"
        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(Request.Method.GET,
            url,
            {
                    response ->
                val obj = JSONObject(response)
                val temp = obj.getJSONObject("currentConditions").getString("temp")
                val cit = obj.getString("address")
                val upd = obj.getJSONObject("currentConditions").getString("datetime")

                temperature.text = "$temp °C"
                city.text = cit
                updata.text = "Last updated:$upd"
            },
            {
                Toast.makeText(applicationContext, "ERROR with API", Toast.LENGTH_LONG).show()
            }
        )
        queue.add(stringRequest)
    }

}