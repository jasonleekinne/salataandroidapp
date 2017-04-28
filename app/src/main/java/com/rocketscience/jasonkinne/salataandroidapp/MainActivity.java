package com.rocketscience.jasonkinne.salataandroidapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private FirebaseDatabase mFirebaseDb;
    private DatabaseReference mHomeDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseDb = FirebaseDatabase.getInstance();
        mHomeDbRef = mFirebaseDb.getReference("home");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Query query = mHomeDbRef.child("banner_images");
        query.addValueEventListener(mValueEventListener);
    }

    private List<Pair<String,String>> mBannerList;
    private ValueEventListener mValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot != null) {
                mBannerList = fetchBannerList(dataSnapshot);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private List<Pair<String, String>> fetchBannerList(DataSnapshot dataSnapshot) {
        List<Pair<String,String>> dataList= new ArrayList<>();
        if (dataSnapshot != null) {
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                String key = snapshot.getKey();
                Map<String,String> map = (Map<String,String>) snapshot.getValue();
                Pair<String,String> data = new Pair<>(map.get("imageUrl"), map.get("title"));
                dataList.add(data);
            }
        }
        return dataList;
    }
    private class HomeImagesAdapter extends RecyclerView.Adapter<ImageViewHolder> {
        private final Context mContext;
        private List<Pair<String,String>> mImageDataList;

        private HomeImagesAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(ImageViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    private class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.title_text);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
        }
        public void bind(Context context, Pair<String,String> data) {
            titleText.setText(data.second);
            String imageUrl = data.first;
            Picasso.with(context)
                    .load(imageUrl)
                    .into(imageView);
        }
    }
}
