package com.example.blogapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.blogapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

public class LoginActivity extends AppCompatActivity {

    private EditText userMail, userPassword;
    private Button btnLogin;
    private ProgressBar loginProgress;
    private FirebaseAuth mAuth;//variable necesaria de autenticacion para iniciar sesion con la base de datos
    private Intent HomeActivity;//creamos variable intent para llamar otro activity
    private ImageView loginPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userMail = (EditText)findViewById(R.id.logMail);
        userPassword = (EditText)findViewById(R.id.logPassword);
        btnLogin = (Button)findViewById(R.id.logBtn);
        loginProgress = (ProgressBar)findViewById(R.id.logProgress);
        mAuth = FirebaseAuth.getInstance();//obtenemos la instancia
        HomeActivity = new Intent(this, com.example.blogapp.Activities.Home.class);//establecemos a que activity queremos que la variable cambie
        loginPhoto = (ImageView)findViewById(R.id.loginPhoto);

        loginPhoto.setOnClickListener(new View.OnClickListener() {//si tocan en la foto es para registrar un nuevo usuario
            @Override
            public void onClick(View view) {
                Intent RegisterActivity = new Intent(getApplicationContext(), com.example.blogapp.Activities.RegisterActivity.class);
                startActivity(RegisterActivity);
                finish();
            }
        });

        loginProgress.setVisibility(View.INVISIBLE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginProgress.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.VISIBLE);

                final String mail = userMail.getText().toString();
                final String password = userPassword.getText().toString();//obtener los datos personales del usuario

                if(mail.isEmpty() || password.isEmpty()){//el usuario no agrego alguno de los dos campos
                    showMessage("Please verify all fields");
                    btnLogin.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);
                }else{
                    signIn(mail,password);//creamos un metodo para iniciar sesion en la base de datos y le pasamos los datos del usuario
                }
            }
        });
    }

    private void signIn(String mail, String password) {//metodo para iniciar sesion en la base de datos
        mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {//llamamos un metodo de la variable mAuth de firebase que nos deja iniciar sesion con email y contraseña
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){//si si se inicio sesion correctamente
                    loginProgress.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                    updateUI();//metodo para llamar otro activity
                }else{//sino se logro iniciar sesion
                    showMessage("error: "+task.getException().getMessage());//obtenemos el error que se genero con la autentificacion de firebase
                    btnLogin.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    private void updateUI() {
        startActivity(HomeActivity);//llamamos el intent que pasará a la activity que queremos
        finish();//matamos el proceso de este activity
    }

    private void showMessage(String message) {//metodo para poner mensajes
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();//creamos un objeto usuario de la clase firebase user y obtenemos el usuario actual

        if(user != null){//si existe un usuario que ya inicio sesion
            //el usuario ya ha iniciado sesion y esta conectado por lo que necesitamos redireccionarlo a la pagina proncipal Home
            updateUI();//llamamos el otro activity
        }
    }
}
