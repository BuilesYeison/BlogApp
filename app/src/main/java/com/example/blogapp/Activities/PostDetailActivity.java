package com.example.blogapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.blogapp.Models.Comment;
import com.example.blogapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    ImageView imgPost, imgUserPost, imgCurrentUser;
    TextView txtPostDesc, txtPostDateName, txtPostTittle;
    EditText editTextComment;
    Button btnAddComment;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    FirebaseDatabase firebaseDatabase;
    String PostKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getSupportActionBar().hide();//escoder la barra superior

        imgPost = (ImageView)findViewById(R.id.post_detail_img);
        imgUserPost = (ImageView)findViewById(R.id.post_detail_user_img);
        imgCurrentUser = (ImageView)findViewById(R.id.post_detail_currentuser_img);
        txtPostDateName = (TextView)findViewById(R.id.post_detail_date_name);
        txtPostDesc = (TextView)findViewById(R.id.post_detail_desc);
        txtPostTittle = (TextView)findViewById(R.id.post_detail_tittle);
        editTextComment = (EditText)findViewById(R.id.post_detail_comment);
        btnAddComment = (Button)findViewById(R.id.post_detail_add_comment_btn);

        firebaseAuth = FirebaseAuth.getInstance();//obtenemos la intancia del usuario logeado actualmente
        currentUser = firebaseAuth.getCurrentUser();//y guardamos sus datos
        firebaseDatabase =FirebaseDatabase.getInstance();//obtenemos la base de datos que estamos utilizando

        //Añadir evento al boton para agregar comentario
        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAddComment.setVisibility(View.INVISIBLE);
                DatabaseReference commentReference = firebaseDatabase.getReference("Comment").child(PostKey).push();//creamos un espacio en la base de datos por asi decirlo para agregar la info del comentario y le asignamos como codigo de la info del comentario el codigo del post al que comentó
                String commentContent = editTextComment.getText().toString();
                String uId = currentUser.getUid();
                String uName = currentUser.getDisplayName();
                String uImg = currentUser.getPhotoUrl().toString();

                Comment comment = new Comment(commentContent, uId, uImg, uName);

                commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMessage("Comment added");
                        editTextComment.setText("");
                        btnAddComment.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Fail to add comment"+e.getMessage());
                    }
                });
            }
        });


        //ahora traemos la informacion del post al que se dio click desde la clase PostAdapter y agregamos esta info a los componentes de este activity
        String postImage = getIntent().getExtras().getString("postImage");
        Glide.with(this).load(postImage).into(imgPost);

        String postTittle = getIntent().getExtras().getString("title");
        txtPostTittle.setText(postTittle);

        String userpostImage = getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(userpostImage).into(imgUserPost);

        String postDescription = getIntent().getExtras().getString("description");
        txtPostDesc.setText(postDescription);

        Glide.with(this).load(currentUser.getPhotoUrl()).into(imgCurrentUser);

        //obtener el id del post
        PostKey = getIntent().getExtras().getString("postKey");

        String date = timestampToString(getIntent().getExtras().getLong("postDate"));
        txtPostDateName.setText(date);
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String timestampToString(long time){
        Calendar calendar  = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", calendar).toString();
        return date;
    }
}
