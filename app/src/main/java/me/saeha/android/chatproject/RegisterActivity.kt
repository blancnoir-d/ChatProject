package me.saeha.android.chatproject

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import me.saeha.android.chatproject.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    //DB
    val databaseReference = Firebase.database("https://chatapplication-2b8c6-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegisterSignUp.setOnClickListener {
            val signUpName = binding.etRegisterName.text.toString()
            val signUpPosition = binding.etRegisterPosition.text.toString()
            val signUpEmail = binding.etRegisterEmail.text.toString()
            val signUpId = binding.etRegisterId.text.toString()
            val signUpPass = binding.etRegisterPass.text.toString()
            if(signUpName.isEmpty() || signUpPosition.isEmpty() || signUpEmail.isEmpty() ||signUpPass.isEmpty()){
                Toast.makeText(this,"빠짐 없이 입력해주세요",Toast.LENGTH_SHORT).show()

            }else{
                databaseReference.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.child("users").hasChild(signUpId)){
                            Toast.makeText(this@RegisterActivity, "이미 등록된 아이디 입니다.",Toast.LENGTH_SHORT).show()
                        }else{
                            databaseReference.child("users").child(signUpId).child("name").setValue(signUpName)
                            databaseReference.child("users").child(signUpId).child("position").setValue(signUpPosition)
                            databaseReference.child("users").child(signUpId).child("email").setValue(signUpEmail)
                            databaseReference.child("users").child(signUpId).child("pass").setValue(signUpPass)

                            saveUserName(this@RegisterActivity, signUpName)
                            saveUserPosition(this@RegisterActivity, signUpPosition)
                            saveUserEmail(this@RegisterActivity, signUpEmail)
                            saveUserId(this@RegisterActivity, signUpId)
                            //saveUserPass(this@RegisterActivity, signUpPass)


                            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }

        }
    }

}