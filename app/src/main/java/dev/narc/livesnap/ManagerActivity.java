package dev.narc.livesnap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManagerActivity extends AppCompatActivity {

    DatabaseReference mRef;
    private AutoCompleteTextView txtSearch;
    private ImageButton mSearchBtn;
    private RecyclerView listdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        mRef = FirebaseDatabase.getInstance().getReference("Users");
        txtSearch = (AutoCompleteTextView)findViewById(R.id.search_field);
        listdata = (RecyclerView) findViewById(R.id.listdata);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        listdata.setLayoutManager(layoutManager);

        populateSearch();
    }


    private void populateSearch()
    {

        ValueEventListener eventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    ArrayList<String> names = new ArrayList<>();
                    for(DataSnapshot ds: snapshot.getChildren())
                    {
                        String n = ds.child("email").getValue(String.class);
                        names.add(n);
                    }
                    ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,names);
                    txtSearch.setAdapter(adapter);
                    txtSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String selection = parent.getItemAtPosition(position).toString();
                            getUsers(selection);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mRef.addListenerForSingleValueEvent(eventListener);
    }

    private void getUsers(String selection) {

        Query query = mRef.orderByChild("email").equalTo(selection);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    ArrayList<UserInfo> userInfos = new ArrayList<>();
                    for(DataSnapshot ds:snapshot.getChildren())
                    {
                        UserInfo userInfo = new UserInfo(ds.child("email").getValue(String.class),ds.child("name").getValue(String.class),ds.getKey());
                        userInfos.add(userInfo);
                    }
                    CustomAdapter adapter = new CustomAdapter(userInfos,ManagerActivity.this);
                    listdata.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addListenerForSingleValueEvent(eventListener);
    }
    class  UserInfo
    {
        public String email;
        public String name;
        public String key;


        public String getEmail() {
            return email;
        }

        public String getName() {
            return name;
        }

        public String getKey() {
            return key;
        }



        public UserInfo(String email, String name, String key) {
            this.email = email;
            this.name = name;
            this.key = key;

        }
    }

    public static class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        private ArrayList<UserInfo> localDataSet;

        private Context mContext;
        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView email;


            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View

                name = (TextView) view.findViewById(R.id.name_text);
                email = (TextView) view.findViewById(R.id.email_text);

            }

        }

        /**
         * Initialize the dataset of the Adapter.
         *
         * @param dataSet String[] containing the data to populate views to be used
         * by RecyclerView.
         */
        public CustomAdapter(ArrayList<UserInfo> dataSet, Context mContext) {
            this.localDataSet = dataSet;
            this.mContext = mContext;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
            View view = layoutInflater.inflate(R.layout.list_layout,viewGroup,false);
            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            UserInfo thisuser = localDataSet.get(position);
            viewHolder.name.setText(thisuser.getName());
            viewHolder.email.setText(thisuser.getEmail());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,UserDetails.class );
                    intent.putExtra("key",thisuser.getKey());
                    mContext.startActivity(intent);
                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return localDataSet.size();
        }
    }


}