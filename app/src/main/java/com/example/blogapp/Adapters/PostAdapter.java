package com.example.blogapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.blogapp.Activities.PostDetailActivity;
import com.example.blogapp.Models.Post;
import com.example.blogapp.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.myViewHolder > {
    Context mContext;
    List<Post> mData;//en esta lista ira la info de la clase Post (imagen de perfil, de usuario, etc.)

    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item, parent, false);//creamos las filas que tendran cada post y le damos el diseño que hicimos row_post_item
        return new myViewHolder(row);//retornamos la fila
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.tvTittle.setText(mData.get(position).getsTittle());//le damos al textView que creamos el titulo que esta en la lista (info de la clase post)
        Glide.with(mContext).load(mData.get(position).getsPicture()).into(holder.imgPost);
        Glide.with(mContext).load(mData.get(position).getsUserPhoto()).into(holder.imgPostProfile);
    }

    @Override
    public int getItemCount() {//obtenemos la cantidad de items
        return mData.size();//retornamos la cantidad de posts que hay en la lista
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView tvTittle;//creamos los objetos equivalentes a los que tenemos en nuestro layout row_post_item
        ImageView imgPost, imgPostProfile;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTittle = itemView.findViewById(R.id.row_post_tittle);//conectamos los componenetes graficos con estos objetos
            imgPost = itemView.findViewById(R.id.row_post_img);
            imgPostProfile = itemView.findViewById(R.id.row_post_profile_img);

            itemView.setOnClickListener(new View.OnClickListener() {//cuando se presioné sobre el post
                @Override
                public void onClick(View view) {
                    Intent postDetailActivity = new Intent(mContext, PostDetailActivity.class);//creamos un intent para pasar al activity que tendra toda la info de ese post
                    int position = getAdapterPosition();

                    postDetailActivity.putExtra("title",mData.get(position).getsTittle());//pasamos la informacion del post con el intent
                    postDetailActivity.putExtra("postImage",mData.get(position).getsPicture());
                    postDetailActivity.putExtra("description",mData.get(position).getsDecription());
                    postDetailActivity.putExtra("postKey",mData.get(position).getsPostKey());
                    postDetailActivity.putExtra("userPhoto",mData.get(position).getsUserPhoto());

                    long timeStamp = (long) mData.get(position).getTimeStamp();
                    postDetailActivity.putExtra("postDate", timeStamp);
                    mContext.startActivity(postDetailActivity);
                }
            });
        }
    }
}
