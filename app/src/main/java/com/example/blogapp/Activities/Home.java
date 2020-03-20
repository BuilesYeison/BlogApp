package com.example.blogapp.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.blogapp.Fragments.HomeFragment;
import com.example.blogapp.Models.Post;
import com.example.blogapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends AppCompatActivity {

    private static final int REQUESCODE = 2;
    private static final int PReqCode = 2;
    FirebaseUser currentUser;//creamos el objeto que contiene la infromacion del usuario
    FirebaseAuth mAuth;//y la autentificacion firebase
    Dialog popAddPost;//creamos este objeto para agregar el layout que creamos del popup para añadir publicaciones
    ImageView popupUserImage, popupPostImage, popupAddBtn;//creamos los dos objetos correspondientes al foto de perfil que irá en el pop up, la imagen que cargará el usuario desde su galeria y para el boton de agregar
    ProgressBar popupProgressBar;
    EditText popupTittle, popupDescription;

    Uri pickedImgUri;//identificador de recursos uniforme guardar imagen de el usuario

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialize user data obtain
        mAuth = FirebaseAuth.getInstance();//obtenemoe la autentificacion del usuario actual
        currentUser = mAuth.getCurrentUser();//y en el objeto que contendra la info del usuario asignamos la info del usuario que esta actualemnte en linea

        //initialize PopUp
        iniPopup();//llamamos nuestro metodo que nos muestra en pantalla el popup para añadir publicaciones

        setupPopupImageClick();

        FloatingActionButton fab = findViewById(R.id.fab);//este es el boton flotante que por defecto tiene un icono de mensajería, aquí agregaremos nuestro popup para crear contenido dentro de la app
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddPost.show();//mostramos el popup
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_Profile, R.id.nav_Settings,
                R.id.nav_Logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                int menuId = destination.getId();//obtenemos la id del item al que el usuario da click en el navigation drawer
                switch (menuId){//hacemos un switch con la seleccion del usuario
                    case R.id.nav_Logout://en este caso si el usuario da click en el item LogOut
                        cerrarSesion();//llamamos el metodo que cierra la sesion del usuario
                        break;
                }
            }
        });

        updateNavHeader();//llamamos el metodo que actualiza la info en el nav header

        //poner el fragment home como default
        //getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
    }

    private void setupPopupImageClick() {//establecer imagen del post
        popupPostImage.setOnClickListener(new View.OnClickListener() {//cuando el usuario de click en la imagen
            @Override
            public void onClick(View view) {
                //abrir la galeria para que el usuario pueda escoger su imagen de preferencia
                if(Build.VERSION.SDK_INT >= 22){

                    checkAndRequestForPermission();//checkear si se dio acceso a la galeria del usuario

                }else{
                    openGallery();//abrir galeria
                }
            }
        });
    }

    private void openGallery() {
        //TODO: Abrir la galeria y esperar a que el usuario seleccione una imagen

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }

    private void checkAndRequestForPermission() {

        if(ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){//sino se tiene el permiso de leer el almacenamiento externo

            if(ActivityCompat.shouldShowRequestPermissionRationale(Home.this,Manifest.permission.READ_EXTERNAL_STORAGE)){//si no se acepta los permisos
                Toast.makeText(this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            }else{//mostrar el recuadro para solicitar los permisos y guardar que si se dio el permiso
                ActivityCompat.requestPermissions(Home.this,
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
            popupPostImage.setImageURI(pickedImgUri);//poner la imagen seleccionada por el usuario en el imageview
        }
    }


    private void iniPopup() {
        popAddPost = new Dialog(this);//decimos el contexto donde se cargará el popup
        popAddPost.setContentView(R.layout.popup_add_post);//le asignamos a la variable la vista de el popup que creamos
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//obtenemos la ventana donde se abre nuestro popup y le damos un color de fondo transparente para que el popup no se vea tan solido sino por encima del home
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);//escondemos la barra de tareas (la barra azul superior) porque allí se ubicará nuestro popup
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;//obtenemos la ventana actual donde se da la ejecucion del popup y le decimos que ubique nuestro popup en la parte de arriba de la ventana

        popupUserImage = popAddPost.findViewById(R.id.popup_user_image);//hacemos la conexion de lso objeto que acabamos de crear con los obejtos de nuestro popUp
        popupAddBtn = popAddPost.findViewById(R.id.popup_add);
        popupDescription = popAddPost.findViewById(R.id.popup_description);
        popupTittle = popAddPost.findViewById(R.id.popup_tittle);
        popupPostImage = popAddPost.findViewById(R.id.popup_image);
        popupProgressBar = popAddPost.findViewById(R.id.popup_progressBar);

        //cargar foto de perfil del usuario en el popup
        Glide.with(Home.this).load(currentUser.getPhotoUrl()).into(popupUserImage);

        //añadir evento cuando da click en el boton para agregar publicacion
        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupProgressBar.setVisibility(View.VISIBLE);//cuando aprete en el boton agregar aparecera el progressbar
                popupAddBtn.setVisibility(View.INVISIBLE);//y el btn lo ponemos invisible

                //verificar si todos los campos del popup estan vacios
                final String sPopupTittle = popupTittle.getText().toString();
                final String sPopupDescription = popupDescription.getText().toString();

                if(!sPopupTittle.isEmpty() && !sPopupDescription.isEmpty() && pickedImgUri != null){
                    //esta bien la informacion, no hay ningun campo vacio
                    //TODO ahora hay que realizar el Post y cargarlo a la base de datos FireBase
                    //primero necesitamos subir la foto del post
                    //acceso a la base de datos firebase
                    //implementation 'com.google.firebase:firebase-database:19.1.0' (agregar este codigo al build graddle app)

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_images");//referenciamos que queremos crear un espaciio en la base de datos llamado blog images donde guardaremos las imagenes de los posts
                    final StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());//obtenemos la imagen ultimamente agregada or el usuario para subirla a la base de datos
                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {//subir el archivo
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String ImageDownloadLink = uri.toString();
                                    //crear objeto 'Post'
                                    Post post = new Post(sPopupTittle, sPopupDescription, ImageDownloadLink, currentUser.getUid(), currentUser.getPhotoUrl().toString());
                                    //agregar el post a firebase
                                    addPost(post);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //un error ocurrio al subir la imagen
                                    showMessage(e.getMessage());
                                    popupAddBtn.setVisibility(View.VISIBLE);
                                    popupProgressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    });

                }else{
                    showMessage("Please verify all input fields and choose post image");
                    popupProgressBar.setVisibility(View.INVISIBLE);
                    popupAddBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void addPost(Post post) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();//creamos la database
        DatabaseReference myRef = database.getReference("Posts").push();//y dentro de la database creamos lo que contendrá todos nuestro posts

        //Id del post y actualizacion del post key
        String sKey = myRef.getKey();
        post.setsPostKey(sKey);

        //añadir los datos del post a la base de datos firebase
        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("Post Added Succesfully");
                popupProgressBar.setVisibility(View.INVISIBLE);
                popupAddBtn.setVisibility(View.VISIBLE);
                popAddPost.dismiss();
            }
        });

    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void cerrarSesion() {//metodo para cerrar sesion del usuario y para redirigirlo al activity inicio de sesion
        FirebaseAuth.getInstance().signOut();//obtenemos la instancia que seria el usuario y cerramos sesion
        Intent LoginActivity = new Intent(getApplicationContext(),LoginActivity.class);//intent para cambiar de pantalla
        startActivity(LoginActivity);//iniciamos intent
        finish();//cerramos este proceso
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void updateNavHeader(){//creo este metodo para actualizar la información del navheader
        NavigationView navigationView = findViewById(R.id.nav_view);//establecemos conexion con el nav header
        View headerView = navigationView.getHeaderView(0);//conexion con el headerView y sus componentes
        TextView navUsername = headerView.findViewById(R.id.navUsername);//creamos un obejto textview y lo conectamos con el objeto del nav Header navUsername
        TextView navUsermail = headerView.findViewById(R.id.navUsermail);
        ImageView navUserphoto = headerView.findViewById(R.id.navUserphoto);

        navUsername.setText(currentUser.getDisplayName());//agregamos en los textview la info del usuario
        navUsermail.setText(currentUser.getEmail());

        //utilizaremos GLide para cargar la imagen de usuario
        //primero necesitaremos intalarlo en el build gradle osea en nuestro entorno de trabajo

        Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserphoto);
    }
}
