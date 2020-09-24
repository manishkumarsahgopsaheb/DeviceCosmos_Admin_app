package com.example.addblog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class bloglist extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference databaseReferenceofadmin,databaseReferenceforblogs;
    String Uid;
    private RecyclerView blogrecycle;
    String currentemail;
   // int k = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloglist);


        blogrecycle = (RecyclerView) findViewById(R.id.recycle_id);
        blogrecycle.setHasFixedSize(true);
        blogrecycle.setLayoutManager(new LinearLayoutManager(this));


        mAuth = FirebaseAuth.getInstance();
        Uid = mAuth.getCurrentUser().getUid();
        currentemail = mAuth.getCurrentUser().getEmail();
        databaseReferenceofadmin = FirebaseDatabase.getInstance().getReference().child("admin");
        databaseReferenceforblogs = FirebaseDatabase.getInstance().getReference().child("Blogs");
        databaseReferenceforblogs.keepSynced(true);

        showbloglist();

    }

    private void showbloglist() {

        Query query = databaseReferenceforblogs.orderByChild("Email").equalTo(currentemail);
        query.keepSynced(true);
        //Query query = databaseReferenceforblogs.orderByChild("likecount").equalTo(Uid) this was for getting a liked post in user mode

        FirebaseRecyclerAdapter<blogrefernceforrecycle, BloggettingHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<blogrefernceforrecycle, BloggettingHolder>
                (blogrefernceforrecycle.class, R.layout.blogrecycle, BloggettingHolder.class, query) {
            @Override
            protected void populateViewHolder(BloggettingHolder viewHolder, blogrefernceforrecycle model, int position) {
                viewHolder.setHeading(model.getHeading());
                viewHolder.setTime(model.getTime());
                viewHolder.setDate(model.getDate());
                viewHolder.setPhoto(model.getPhoto());
                viewHolder.setCount(model.getCount());
                final String userid = getRef(position).getKey();

                ImageView deleteimg = (ImageView) viewHolder.itemView.findViewById(R.id.delete_id);


                deleteimg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final AlertDialog.Builder builder = new AlertDialog.Builder(bloglist.this);
                        builder.setMessage("You want to delete this blog?")
                                .setCancelable(true)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, final int id) {

                                        DatabaseReference deleteblog = FirebaseDatabase.getInstance().getReference("Blogs").child(userid);
                                        deleteblog.removeValue();
                                        Toast.makeText(getApplicationContext(),"deleted",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, final int id) {
                                        dialog.cancel();
                                    }
                                });
                        final AlertDialog alert = builder.create();
                        alert.show();
                    }
                });

                // ImageView like = (ImageView)viewHolder.itemView.findViewById(R.id.like_id);

              /*  like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int sum = k + 1;
                        TextView intcount = (TextView)findViewById(R.id.integer_id);
                        intcount.setText(sum);
                    }
                });
                */
            }

        };

        blogrecycle.setAdapter(firebaseRecyclerAdapter);
    }


    public static class BloggettingHolder extends RecyclerView.ViewHolder
    {
        View myview;

        public BloggettingHolder(View itemView) {
            super(itemView);
            myview = itemView;

        }

        public void setHeading(String heading){
            TextView mess = (TextView)myview.findViewById(R.id.text_id);
            mess.setText(heading);
        }
        public void setTime(String time){
            TextView tim = (TextView)myview.findViewById(R.id.timeretrieve_id);
            tim.setText(time);
        }
        public void setDate(String date){
            TextView dat = (TextView)myview.findViewById(R.id.dateretrieve_id);
            dat.setText(date);
        }

        public void setPhoto(String photo){
            ImageView imgv = (ImageView)myview.findViewById(R.id.photoretrieve_id);
            Picasso.get().load(photo).into(imgv);
        }

        public void setCount(String count){
            TextView likecount = (TextView)myview.findViewById(R.id.view_id);
            likecount.setText(count);
        }

    }

}
