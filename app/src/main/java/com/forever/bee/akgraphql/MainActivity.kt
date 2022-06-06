package com.forever.bee.akgraphql

import android.app.SearchManager.QUERY
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.forever.bee.akgraphql.databinding.ActivityMainBinding

import com.forever.bee.akgraphql.trips.TripsFragment
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

/**
 * Serves as this app's launcher activity, simply commits a FragmentTransaction to display a
 * [TripsFragment] fragment.
 *
 * */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val DOCUMENT = "{ allTrips { id title startTime priority duration creationTime } }"
        const val ENDPOINT = BuildConfig.GRAPHQL_API_KEY
        const val MEDIA_TYPE_JSON  = "application/json; charset=utf-8"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * Executes our GraphQL request.
     *
     * @return The raw JSON as a String
     * */
    fun query(): String? {
        var response: String? = null

        try {
            val payload = HashMap<String, String>()

            payload[QUERY] = DOCUMENT

            val body = Gson().toJson(payload)
            val request = Request.Builder()
                .url(ENDPOINT)
                .post(body.toRequestBody(MEDIA_TYPE_JSON.toMediaTypeOrNull()))
                .build()

            response = OkHttpClient().newCall(request).execute().body.toString()


        } catch (e: IOException) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
        }
        return response
    }
}