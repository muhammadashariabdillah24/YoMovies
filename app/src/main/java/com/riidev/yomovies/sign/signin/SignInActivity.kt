package com.riidev.yomovies.sign.signin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.*
import com.riidev.yomovies.R
import kotlinx.android.synthetic.main.activity_sign_in.*

import com.riidev.yomovies.HomeActivity
import com.riidev.yomovies.sign.SignUpActivity
import com.riidev.yomovies.utils.Preferences


class SignInActivity : AppCompatActivity() {

    lateinit var iUsername:String
    lateinit var iPassword:String

    lateinit var mDatabase : DatabaseReference
    lateinit var preference : Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mDatabase = FirebaseDatabase.getInstance().getReference("User")
        preference = Preferences(this)

        preference.setValues("onboarding", "1")
        if (preference.getValues("status").equals("1")) {
            finishAffinity()

            val intent = Intent(this@SignInActivity, HomeActivity::class.java)
            startActivity(intent)
        }

        btn_masuk_akun.setOnClickListener {
            iUsername = edt_username.text.toString()
            iPassword = edt_password.text.toString()

            if (iUsername.equals("")) {
                edt_username.error = "Silahkan masukan username anda!"
                edt_username.requestFocus()
            } else if (iPassword.equals("")) {
                edt_password.error = "Silahkan masukan password anda!"
                edt_password.requestFocus()
            } else {
                pushLogin(iUsername, iPassword)
            }
        }

        btn_daftar_akun.setOnClickListener {
            val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun pushLogin(iUsername: String, iPassword: String) {
        mDatabase.child(iUsername).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val user = dataSnapshot.getValue(User::class.java)
                if (user == null) {
                    Toast.makeText(this@SignInActivity, "User  tidak ditemukan", Toast.LENGTH_LONG).show()
                } else {

                    if (user.password.equals(iPassword)){

                        preference.setValues("nama", user.nama.toString())
                        preference.setValues("user", user.username.toString())
                        preference.setValues("email", user.email.toString())
                        preference.setValues("url", user.url.toString())
                        preference.setValues("saldo", user.saldo.toString())
                        preference.setValues("status", "1")

                        val intent = Intent(this@SignInActivity, HomeActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@SignInActivity, "Password anda salah!", Toast.LENGTH_LONG).show()
                    }
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@SignInActivity, databaseError.message, Toast.LENGTH_LONG).show()
            }
        })
    }
}