package com.example.swinclubeventmanagementapplication

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.swinclubeventmanagementapplication.JSONResponse.*
import com.example.swinclubeventmanagementapplication.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginSignUpActivity :
    AppCompatActivity(),
    View.OnClickListener,
    FirebaseDBHelper.itemOnClickListener{

    // -- UI elements -- //
    private lateinit var binding: ActivityLoginBinding
    private lateinit var actionBar: ActionBar
    private lateinit var progressDialog: ProgressDialog

    private var email = ""
    private var password = ""
    private lateinit var user: String
    private lateinit var login_signUp: String
    private lateinit var firebaseDbHelper: FirebaseDBHelper
    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    // -- const val -- //
    private val COMMITTEE_MEMBER = "CommitteeMember"
    private val STUDENT = "Student"
    private val LOGIN = "login"
    private val SIGN_UP = "signUp"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = getIntent()
        user = intent?.getStringExtra("user").toString()
        login_signUp = intent?.getStringExtra("login_signUp").toString()

        initUI()
        checkUser()
    }

    @SuppressLint("SetTextI18n")
    private fun initUI() {
        firebaseDbHelper = FirebaseDBHelper(this,null,null,this)

        var msg = ""
        var aBarTitle = ""
        if(login_signUp.equals(LOGIN)){
            msg = "Logging In..."
            aBarTitle = "Login as $user"
            binding.textViewNoAcc.text = "Doest Not have an account? Sign up here"
            binding.textViewNoAcc.setOnClickListener{updateActivity(user,SIGN_UP)}
            binding.textViewLoginSignUpTitle.text = "Login"
            binding.buttonLoginSignUp.text = "Login"
        }else if(login_signUp.equals(SIGN_UP)){
            msg = "Creating Account In..."
            aBarTitle = "Sign Up"
            binding.textViewNoAcc.text = "Already have an account? Login here"
            binding.textViewNoAcc.setOnClickListener{updateActivity(user,LOGIN)}
            binding.textViewLoginSignUpTitle.text = "Sign Up"
            binding.buttonLoginSignUp.text = "Sign Up"
        }

        database = FirebaseDatabase.getInstance().getReference("Students")
        firebaseAuth = FirebaseAuth.getInstance()

        actionBar = supportActionBar!!
        actionBar.title = aBarTitle
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage(msg)
        progressDialog.setCanceledOnTouchOutside(false)

        // -- UI elements click listener -- //
        binding.buttonLoginSignUp.setOnClickListener(this)
    }

    private fun updateActivity(user: String,login_signUp: String){
        val intent: Intent = Intent(this,LoginSignUpActivity::class.java)
        intent.putExtra("user", user)
        intent.putExtra("login_signUp", login_signUp)
        finish()
        startActivity(intent)
    }


    private fun login(){
        var currStdID =
            Regex("^([^@]+)").find(binding.emailEt.text.toString().trim())?.value.toString()
        firebaseDbHelper.isRoleExist(currStdID)
    }

    private fun firebaseLogin() {
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                // login success
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this, "Logged in as ${email}", Toast.LENGTH_SHORT).show()
                val intent: Intent = Intent(this,ProfileActivity::class.java)
                intent.putExtra("user", user)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener{ e->
                progressDialog.dismiss()
                Toast.makeText(this, "Login failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun firebaseSignup() {

        progressDialog.show()
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                // signup success
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email

                var Gender: String? = "none"
                var Email: String? = "none"
                var Dob: String? = "none"
                var ContactNo: String? = "none"
                var Name: String? = "none"
                var ProfileIMG: String? = "https://www.business2community.com/wp-content/uploads/2017/08/blank-profile-picture-973460_640.png"
                var EnrollmentDate: String? = "none"
                var JoinedClubs: ArrayList<JoinedClub>? = null
                var Role: ArrayList<Role>? = null

                var currStdID =
                    Regex("^([^@]+)").find(FirebaseAuth.getInstance().currentUser!!.email!!)?.value.toString()

                val std = Student(
                    currStdID,email,Gender,Email,Dob,password,
                    ContactNo,Name,ProfileIMG,EnrollmentDate,JoinedClubs,Role)

                firebaseDbHelper.addStudent(std)

                Toast.makeText(this, "Account created with email ${email}", Toast.LENGTH_SHORT).show()
//                startActivity(Intent(this, ProfileActivity::class.java))
//                finish()
                val intent: Intent = Intent(this,ProfileActivity::class.java)
                intent.putExtra("user", user)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener{ e->
                progressDialog.dismiss()
                Toast.makeText(this, "Signup failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
        // also store into students table

    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null){
            // user logged in
//            startActivity(Intent(this, ProfileActivity::class.java))
//            finish()
            val intent: Intent = Intent(this,ProfileActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
            finish()
        }
    }

    private fun loginSignUp(){
        if(isEmailOk() && isPasswordOk()){
            if(login_signUp.equals(LOGIN)){
                if(user.equals(COMMITTEE_MEMBER)){
                    login()
                }else{
                    firebaseLogin()
                }
            }else if(login_signUp.equals(SIGN_UP)){
                firebaseSignup()
            }
        }
    }
    private fun isEmailOk():Boolean{
        email = binding.emailEt.text.toString().trim()
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEt.error = "Invalid email format"
            return false
        }
        return true
    }
    private fun isPasswordOk():Boolean{
        password = binding.passwordEt.text.toString().trim()
        if(TextUtils.isEmpty(password)){
            binding.passwordEt.error = "Please enter password"
            return false
        }
        else if(password.length<6){
            binding.passwordEt.error = "Password must more than 6"
            return false
        }
        return true
    }

// ------------------------------override method--------------------------------------------- //

    override fun onClick(view: View?) {
        when (view?.id){
            R.id.buttonLoginSignUp -> loginSignUp()
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() // go back to previous activity, when back button of actionbar clicked
        return super.onSupportNavigateUp()
    }

    override fun passData(std: Student){}
    override fun passSimplifiedClubs(clubs: ArrayList<SimplifiedClub>){}
    override fun passMemberListing(memberListing: ArrayList<String>){}
    override fun passEventListing(eventListing: ArrayList<Event>){}
    override fun passAllClubs(sClubs: ArrayList<Club>){}
    override fun passAllNotification(notifsList: ArrayList<StudentNotification>){}

    override fun isCommitteMember(ya: Boolean) {
        if(ya){
            firebaseLogin()
        }else{
            Toast.makeText(this,"You are not committee or please sign up an account", Toast.LENGTH_SHORT).show()
        }
    }
}
