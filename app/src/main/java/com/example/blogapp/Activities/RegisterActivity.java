package com.example.blogapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.blogapp.R;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    ImageView ImgUserPhoto;
    static int PReqCode=1;
    static int REQUESCODE=1;
    Uri pickedImgUri;//identificador de recursos uniforme guardar imagen de el usuario
    private EditText userName, userMail, userPassword, userPassword2;
    private ProgressBar loadingProgress;
    private Button regBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ImgUserPhoto = (ImageView)findViewById(R.id.regUserPhoto);
        userMail = (EditText)findViewById(R.id.regMail);
        userName = (EditText)findViewById(R.id.regName);
        userPassword = (EditText)findViewById(R.id.regPassword);
        userPassword2 = (EditText)findViewById(R.id.regPassword2);
        loadingProgress = (ProgressBar)findViewById(R.id.regProgressBar);
        regBtn = (Button)findViewById(R.id.regBtn);
        loadingProgress.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regBtn.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);

                final String sEmail = userMail.getText().toString();//la palabra reservada final lo que indica que a esa variable solo se le puede asignar un valor u objeto una única vez.
                final String sPassword = userPassword.getText().toString();
                final String sPassword2 = userPassword2.getText().toString();//obtenemos los datos de registro
                final String sName = userName.getText().toString();

                if(sEmail.isEmpty() || sPassword.isEmpty()||sPassword2.isEmpty()||sName.isEmpty() || !sPassword.equals(sPassword2)){
                    //no se llenaron todos los campos o la segunda contraseña no es equivalente a la primera que se ingreso
                    showMessage("Please verify all fields");
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                }else{//el usuario si lleno correctamente los campos
                    //el siguiente metodo intentara crear la cuenta si el email es valido

                    CreateUserAccount(sEmail,sName,sPassword);
                }
            }
        });

        ImgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= 22){

                    checkAndRequestForPermission();

                }else{
                    openGallery();
                }
            }
        });
    }

    private void CreateUserAccount(String sEmail, final String sName, String sPassword) {
        //este metodo crea la cuenta de usuario con una contraseña y un un email especifico
        mAuth.createUserWithEmailAndPassword(sEmail,sPassword)//llamamos un metodo de firebase que ingresa el email y la contraseña
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {//si la tarea de crear la cuenta es satisfactoria...
                            // user account created successfully
                            showMessage("Account created");
                            // despues de haber creado la cuenta se necesita actualizar el nombre y la foto de perfil de el usuario
                            updateUserInfo( sName,pickedImgUri,mAuth.getCurrentUser());
                        }
                        else//sino se creo la cuenta
                        {
                            // account creation failed
                            showMessage("account creation failed" + task.getException().getMessage());//obtenemos el error y lo mostramos
                            regBtn.setVisibility(View.VISIBLE);
                            loadingProgress.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    //actualizar foto y nombre de el usuario
    private void updateUserInfo(final String sName, final Uri pickedImgUri, final FirebaseUser currentUser) {
        // primero se necesita subir a la base de datos de firebase la foto de perfil del usuario y obtener uri
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //imagen cargada correctamente
                //ahora podemos obtener el url de la imagen
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //uri contain user image url
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()//creamos un objeto en los que agregaremos lo datos para actualizar datos de usuario
                                .setDisplayName(sName)//agregamos el nombre del usuario anteriormente otenido
                                .setPhotoUri(uri)//y la foto en formato uri
                                .build();
                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            //informacion de usuario actualizada correctamente
                                            showMessage("Register complete");
                                            updateUI();
                                        }
                                    }
                                });
                    }
                });
            }
        });
    }

    private void updateUI() {
        Intent homeActivity = new Intent(getApplicationContext(),HomeActivity.class);//creamos un intent para pasar de activity
        startActivity(homeActivity);//ejecutamos el activity
        finish();//y matamos el proceso de este activity para que no consuma
    }

    //metodo simple para mostrar mensajes flotantes (toast)
    private void showMessage(String message) {//este metodo lo que hace es recibir como string un mensaje cuando se llame
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();//y ubicamos el mensaje que se ingreso

    }

    private void openGallery() {
        //TODO: Abrir la galeria y esperar a que el usuario seleccione una imagen

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }

    private void checkAndRequestForPermission() {

        if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED){//sino se tiene el permiso de leer el almacenamiento externo

            if(ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){//si no se acepta los permisos
                Toast.makeText(this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            }else{//mostrar el recuadro para solicitar los permisos y guardar que si se dio el permiso
                ActivityCompat.requestPermissions(RegisterActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        }else{//si ya se tiene el permiso de ingresar al almacenamiento externo
            openGallery();//abrir galeria
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUESCODE && data != null){
            //el usuario ha elegido con exito una imagen de su galeria y necesitamos guardar su referencia en una variable
            pickedImgUri = data.getData();//guardar la imagen seleccionada por el usuario
            ImgUserPhoto.setImageURI(pickedImgUri);//poner la imagen seleccionada por el usuario en el imageview
        }
    }

}
