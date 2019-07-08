package com.example.jun.myapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.text.TextUtils;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    private Button button;

    private LinearLayoutManager layoutManager;

    private String[] contacts;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_contact:
                    mTextMessage.setText(R.string.title_contact);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_gallery);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);

        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recylcer);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        // initialize contact

        contacts = new String[] {
                "Wow", "Briliant", "Adagio", "Presto"
        };

        renderContacts();

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public static final String BASE_URL = "http://b5998751.ngrok.io/";

            @Override
            public void onClick(View v) {
                // TODO:: DoSomething...

                OkHttpClient client = new OkHttpClient.Builder()
                        .addNetworkInterceptor(new StethoInterceptor())
                        .build();

                Retrofit retrofit = new Retrofit.Builder()
                        .client(client)
                        .baseUrl(BASE_URL)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                MyApiEndpointInterface apiService = retrofit.create(MyApiEndpointInterface.class);
                Call<Contact[]> call = apiService.listContacts();
                call.enqueue(new Callback<Contact[]>() {
                    @Override
                    public void onResponse(Call<Contact[]> call, Response<Contact[]> response) {
//                        Log.d("WOWOW", response.body()[0].name);
//
//                        List<String> l = Arrays.asList(new String[] {});
//                        for (Contact contact : response.body()) {
//                             l.add(contact.name);
//                        }
//                        Log.d("WOWOW", TextUtils.join(" ", l));
                    }

                    @Override
                    public void onFailure(Call<Contact[]> call, Throwable t) {
                        Log.d("WOWOW", "FAIL: ".concat(t.getMessage()));

                    }
                });


            }
        });
    }

    public interface MyApiEndpointInterface {
        @GET("/")
        Call<String> getRoot();

        @GET("/contacts")
        Call<Contact[]> listContacts();
    }

    class Contact {
        public String name;
        public String number;
    }

    class RootResponse {

    }

    private void renderContacts() {
        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(contacts);

        if (recyclerView.getAdapter() != null) {
            recyclerView.setAdapter(mAdapter);
            return;
        }

        recyclerView.swapAdapter(mAdapter, true);

    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private String[] mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public LinearLayout view;
            public MyViewHolder(LinearLayout v) {
                super(v);
                view = v;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(String[] myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
            // create a new view
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.contact_item, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            ((TextView) holder.view.findViewById(R.id.name)).setText(mDataset[position]);

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.length;
        }
    }


}
