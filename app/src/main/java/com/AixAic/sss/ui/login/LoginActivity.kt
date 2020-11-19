package com.AixAic.sss.ui.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.edit
import com.AixAic.sss.MainActivity
import com.AixAic.sss.R
import com.AixAic.sss.SSSApplication
import com.AixAic.sss.SSSApplication.Companion.context
import com.AixAic.sss.logic.Repository
import com.AixAic.sss.logic.model.LoginData
import com.AixAic.sss.logic.model.UserResponse
import com.AixAic.sss.logic.network.ServiceCreator
import com.AixAic.sss.logic.network.UserService
import com.AixAic.sss.ui.BottomActivity
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //记住密码功能
        val prefs = getPreferences(Context.MODE_PRIVATE)
        val isRemember = prefs.getBoolean("remember_password", false)
        if (isRemember) {
            //将账号密码填入输入框内
            val userName = prefs.getString("userName", "")
            val userPassword = prefs.getString("userPassword", "")
            userNameEdit.setText(userName)
            userPasswordEdit.setText(userPassword)
            rememberPass.isChecked = true
        }
        loginBtn.setOnClickListener {
            val userName = userNameEdit.text.toString()
            val userPassword = userPasswordEdit.text.toString()
            val loginData = LoginData(userName, userPassword)

            val userService = ServiceCreator.create<UserService>()
            userService.login(loginData).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    val userResponse = response.body()

                    if (userResponse != null && userResponse.status.equals("ok")){
                        val user = userResponse.user
                        Log.d("Login", userResponse.status)
                        Log.d("Login", user.name)
                        if (rememberPass.isChecked) { //是否选中记住密码
                            prefs.edit() {
                                putInt("id", user.id)
                                putString("name", user.name)
                                putString("sno", user.sno)
                                putString("password", user.password)
                                putString("school", user.school)
                                putString("phone", user.phone)
                                putBoolean("remember_password", true)
                                putString("userName", userName)
                                putString("userPassword", userPassword)
                            }
                        }
                        val intent = Intent(context, BottomActivity::class.java)
                        startActivity(intent)
                    }else
                        Toast.makeText(SSSApplication.context, "账号或密码错误", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }

}