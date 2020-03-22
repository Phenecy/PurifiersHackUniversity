package dev.bonch.herehackpurify.model.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import dev.bonch.herehackpurify.activities.RegistrationActivity
import dev.bonch.herehackpurify.activities.SplashActivity
import dev.bonch.herehackpurify.model.network.NetworkService
import dev.bonch.herehackpurify.model.pojo.Client
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object TestRep {
    fun getClientByPhone(activity: SplashActivity, phone: String) {
        NetworkService
            .TABLE_API
            .getClientByPhone(phone)
            .enqueue(object : Callback<Client> {
                override fun onResponse(
                    call: Call<Client>,
                    resp: Response<Client>
                ){
                    Log.d("Test", "Good")
                    activity.isClientReg(resp.body()?: Client())

                }

                override fun onFailure(call: Call<Client>, t: Throwable) {
                    Log.d("Test", t.localizedMessage ?: "Error!")
                    activity.isClientReg(Client())

                }
            })
    }

    fun createClient(activity: RegistrationActivity, client: Client) {
        NetworkService
            .TABLE_API
            .createClient(client)
            .enqueue(object : Callback<Client> {
                override fun onResponse(
                    call: Call<Client>,
                    resp: Response<Client>
                ){
                    Log.d("Test", "Good")
                    activity.regSuccess()
                }

                override fun onFailure(call: Call<Client>, t: Throwable) {
                    Log.d("Test", t.localizedMessage ?: "Error!")
                }
            })
    }


}
