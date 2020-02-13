package gh.chig.com.myapplication;

import android.content.Context;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.pm.PackageManager;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.WRITE_CONTACTS,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    private MyAdapter myAdapter;
    private RecyclerView myRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Toolbar mToolbar ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar =  findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        // Swipe Refresh Layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                GetData service = RetrofitClient.getRetrofitInstance().create(GetData.class);
                Call<List<RetroUsers>> call = service.getAllUsers();
                call.enqueue(new Callback<List<RetroUsers>>() {

                    @Override
                    public void onResponse(Call<List<RetroUsers>> call, Response<List<RetroUsers>> response) {
                        List<RetroUsers> clients =  response.body();
                        for(RetroUsers client: clients){
                            addContact(client.getName(), client.getPhone(), client.getEmail(),client.getLocation(), client.getCode());
                        }
                        loadDataList(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<RetroUsers>> call, Throwable throwable) {
                        Toast.makeText(MainActivity.this, "Unable to load users", Toast.LENGTH_SHORT).show();
                    }
                });
                mSwipeRefreshLayout.setRefreshing(false);
            }

        });
        GetData service = RetrofitClient.getRetrofitInstance().create(GetData.class);
        Call<List<RetroUsers>> call = service.getAllUsers();
        call.enqueue(new Callback<List<RetroUsers>>() {

            @Override
            public void onResponse(Call<List<RetroUsers>> call, Response<List<RetroUsers>> response) {
                List<RetroUsers> clients =  response.body();
                for(RetroUsers client: clients){
                    addContact(client.getName(), client.getPhone(), client.getEmail(),client.getLocation(), client.getCode());
                }
                loadDataList(response.body());
            }

            @Override
            public void onFailure(Call<List<RetroUsers>> call, Throwable throwable) {
                Toast.makeText(MainActivity.this, "Unable to load data", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void loadDataList(List<RetroUsers> usersList) {

        myRecyclerView = findViewById(R.id.myRecyclerView);
        myAdapter = new MyAdapter(usersList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        myRecyclerView.setLayoutManager(layoutManager);
        myRecyclerView.setAdapter(myAdapter);
    }


    private void addContact(String name, String phoneNumber, String email, String location, String code) {
        String strName = code+"-"+name+"-"+location;
        String strPhoneNumber = phoneNumber;
        String strEmail = email;

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        ArrayList<ContentProviderOperation> operationList = new ArrayList<>();
        operationList.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // first and last names
        operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, strName)
                .build());

        operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, strPhoneNumber)
                .build());
        operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)

                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, strEmail)
                .build());

        try {
            ContentProviderResult[] results = getContentResolver().applyBatch(ContactsContract.AUTHORITY, operationList);

            Uri myContactUri = results[0].uri;
            int lastSlash = myContactUri.toString().lastIndexOf("/");
            int length = myContactUri.toString().length();
            int contactID = Integer.parseInt((String) myContactUri.toString().subSequence(lastSlash+1, length));
            System.out.println("------------contactID------"+contactID);
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.d("success", "contacts added successfully: ");

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        myAdapter.filter(newText);
        return true;
    }
}
