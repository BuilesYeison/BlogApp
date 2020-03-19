package com.example.blogapp.Activities;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.blogapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class Home extends AppCompatActivity {

    FirebaseUser currentUser;//creamos el objeto que contiene la infromacion del usuario
    FirebaseAuth mAuth;//y la autentificacion firebase
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialize
        mAuth = FirebaseAuth.getInstance();//obtenemoe la autentificacion del usuario actual
        currentUser = mAuth.getCurrentUser();//y en el objeto que contendra la info del usuario asignamos la info del usuario que esta actualemnte en linea

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
