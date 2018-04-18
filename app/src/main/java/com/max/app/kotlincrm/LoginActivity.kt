package com.max.app.kotlincrm

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import com.max.app.kotlincrm.api.JZBConstants
import com.max.app.kotlincrm.api.JZBController
import com.max.app.kotlincrm.api.JzbResponseHandler
import com.max.app.kotlincrm.utils.L
import com.max.app.kotlincrm.utils.MyToast
import com.max.app.kotlincrm.utils.Sp
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private var mContext: Activity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Sp.init_SP_Instance(this, JZBConstants.SP_USERINFO)
        val isLogin = Sp.get_Boolean(JZBConstants.TAG_ISLOGIN, false)
        if (isLogin) {
//            MainManagerActivity.actionMainManagerActivity(mContext)
            this.finish()
        }
        mContext = this
        initView()
    }

    private fun initView() {
        btn_login.setOnTouchListener(mOnTouchListener)
        btn_login.setOnClickListener(this)
        iv_password_eye.setOnClickListener(this)
    }
    private var passwordFlag = false
    override fun onClick(v: View?) {
        when (v) {
            btn_login -> {
                val userName = et_login_username.text.toString()
                val password = et_login_password.text.toString()
                L.md("userName=$userName  password=$password")
                login(userName, password)
            }
            iv_password_eye -> {
                if (passwordFlag) {
                    passwordFlag = false
                    et_login_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    et_login_password.setSelection(et_login_password.text.toString().length)
                    iv_password_eye.setImageResource(R.drawable.eye_open)
                } else {
                    passwordFlag = true
                    et_login_password.transformationMethod = PasswordTransformationMethod.getInstance()
                    et_login_password.setSelection(et_login_password.text.toString().length)
                    iv_password_eye.setImageResource(R.drawable.eye_close)
                }
            }
        }
    }

    fun login(userName: String, password: String){
        JZBController.getInstance(this).postLogin(userName, password, object : JzbResponseHandler() {
            override fun onHttpSuccessStatusOk(jsonObject: JSONObject?) {
                MyToast.makeText(mContext, "登录成功")
                try {
                    val data = jsonObject!!.getJSONObject("data")
                    val trueName = data.optString(JZBConstants.TRUENAME)
                    val roleName = data.optString(JZBConstants.ROLENAME)
                    val roleNumber = data.optString(JZBConstants.ROLENUMBER)
                    val avatar = data.optString(JZBConstants.AVATAR)
                    L.md("trueName=$trueName  roleName=$roleName  roleNumber=$roleNumber")
                    Sp.init_SP_Instance(mContext, JZBConstants.SP_USERINFO)
                    Sp.put_String(JZBConstants.TRUENAME, trueName)
                    Sp.put_String(JZBConstants.ROLENAME, roleName)
                    Sp.put_String(JZBConstants.AVATAR, avatar)
                    Sp.put_String(JZBConstants.ROLENUMBER, roleNumber)
                    //记住用户名密码
//                    Sp.init_SP_Instance(mContext, JZBConstants.SP_AUTHINFO)
//                        MySharedPreferences.put_String(JZBConstants.AUTH_USERNAME, username)
//                        val pswd = if (mRememberPasswordIcon.isChecked()) AESUtil.encryptAES(password, JZBConstants.AKey) else ""
//                        MySharedPreferences.put_String(JZBConstants.AUTH_PASSWORD, pswd)
//                        mHandler.sendEmptyMessage(LoginSuccess)
//                    } else {
//                        val msg = json.optString("msg")
//                        mHandler.sendEmptyMessage(LoginFailer)
//                        val message = mHandler.obtainMessage()
//                        message.what = LoginFailer
//                        message.obj = msg
//                        message.sendToTarget()
                    toMain()
                } catch (e: Exception) {
                    MyToast.makeText(mContext, "登录失败，请稍后重试")
                }
            }
        })
    }

    private val mOnTouchListener: View.OnTouchListener = View.OnTouchListener { v, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            v.alpha = 0.5f
        } else if (event.action == MotionEvent.ACTION_UP) {
            v.alpha = 1f
        }
        false
    }

    fun toMain() {
        val intent = Intent(mContext, MainManagerActivity::class.java)
        startActivity(intent)
        finish()
    }
}
