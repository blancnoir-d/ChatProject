package me.saeha.android.chatproject

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import me.saeha.android.chatproject.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    //DB
    val databaseReference = Firebase.database("https://chatapplication-2b8c6-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //로그인
        binding.btnLoginSignIn.setOnClickListener {
            databaseReference.addListenerForSingleValueEvent(object:ValueEventListener{
                val id = binding.tetLoginId.text.toString()
                val password =binding.tetLoginPass.text.toString()

                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.child("users").hasChild(id)){// 파이어베이스 DB에 아이디가 있으면
                       val getPass = snapshot.child("users").child(id).child("pass").getValue(String::class.java).toString()
                        if(password != getPass){ //비밀번호가 틀리면
                            Toast.makeText(this@LoginActivity,"아이디가 없거나 비밀번호가 일치하지 않습니다",Toast.LENGTH_SHORT).show()

                        }else{//비밀번호가 맞으면 로그인
                            val getName = snapshot.child("users").child(id).child("name").getValue(String::class.java).toString()
                            val getPosition = snapshot.child("users").child(id).child("position").getValue(String::class.java).toString()
                            val getEmail = snapshot.child("users").child(id).child("email").getValue(String::class.java).toString()
                            Log.d("Login 이름", getName)
                            saveUserName(this@LoginActivity, getName)
                            saveUserPosition(this@LoginActivity, getPosition)
                            saveUserEmail(this@LoginActivity, getEmail)
                            saveUserId(this@LoginActivity, id)
                            //saveUserPass(this@LoginActivity, getPass)

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                    }else{// 파이어베이스 DB에 아이디가 없으면
                        Toast.makeText(this@LoginActivity,"아이디가 없거나 비밀번호가 일치하지 않습니다",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        }

        //회원가입
        binding.btnLoginSignUp.setOnClickListener {
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        if(checkLoginState(this)){
         val intent = Intent(this,MainActivity::class.java)
         startActivity(intent)
         finish()
        }
    }

    override fun onBackPressed() { //앱 종료
        finishAffinity();
        System.runFinalization();
        System.exit(0);
    }
}