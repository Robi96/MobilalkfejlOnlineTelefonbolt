package com.example.mobilalkfejl_online_telefonbolt;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ShopMainSite extends AppCompatActivity {

    private static final String LOG_TAG = ShopMainSite.class.getName();
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;
    private RecyclerView mRecycleView;
    private ArrayList<PhoneItem> mItemList;
    private ArrayList<PhoneItem> cartItems;
    private ItemAdapter mAdapter;
    private FrameLayout circle;
    private TextView contentTextView;
    private int cartItemCount = 0;
    private int item_limit = 6;
    private ArrayList<String> cartNames = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop_main_site);
        mAuth = FirebaseAuth.getInstance();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            Log.d(LOG_TAG, "Autentikált felhasználó");
        }else {
            Log.d(LOG_TAG, "Sikertelen azonosítás");
            finish();
        }

        Bundle temp = getIntent().getExtras();
        assert temp != null;
        cartItemCount = temp.getInt("count");
        cartNames = temp.getStringArrayList("names");
        mRecycleView = findViewById(R.id.shopRecyclerView);
        mRecycleView.setLayoutManager(new GridLayoutManager(this, 1));
        mItemList = new ArrayList<>();

        mAdapter = new ItemAdapter(this, mItemList);
        mRecycleView.setAdapter(mAdapter);
        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Items");
        queryData();
    }

    private void queryData(){
        mItemList.clear();

        mItems.orderBy("cartCount", Query.Direction.DESCENDING).limit(item_limit).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                PhoneItem item = doc.toObject(PhoneItem.class);
                item.setID(doc.getId());
                mItemList.add(item);
            }
            if(mItemList.isEmpty()) {
                initializeData();
                queryData();
            }
            mAdapter.notifyDataSetChanged();
        });

        }
    private void initializeData() {
        String[] itemsList = getResources().getStringArray(R.array.phone_names);
        String[] itemsInfo = getResources().getStringArray(R.array.phone_desc);
        String[] itemsPrice = getResources().getStringArray(R.array.phone_price);
        TypedArray itemsImageRes = getResources().obtainTypedArray(R.array.phone_images);

        mItemList.clear();

        for(int i = 0; i < itemsList.length; i++){
            mItems.add(new PhoneItem(itemsList[i], itemsInfo[i], itemsPrice[i], itemsImageRes.getResourceId(i, 0), 0));
            //mItemList.add(new PhoneItem(itemsList[i], itemsInfo[i], itemsPrice[i], itemsImageRes.getResourceId(i, 0)));
        }
        itemsImageRes.recycle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.phone_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(LOG_TAG, newText);
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            if(item.getItemId() == R.id.logout) {
                Log.d(LOG_TAG, "Logout clicked");
                FirebaseAuth.getInstance().signOut();
                logOutToMain();
                return true;
            }else if(item.getItemId() == R.id.cart) {
                Log.d(LOG_TAG, "Cart clicked");
                startCheckout();
                return true;
            }else if(item.getItemId() == R.id.search_bar) {
                Log.d(LOG_TAG, "Search clicked");
                return true;
            }else {
                return super.onOptionsItemSelected(item);
            }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();
        circle = (FrameLayout) rootView.findViewById(R.id.red_circle);
        contentTextView = (TextView) rootView.findViewById(R.id.alert_count);
        rootView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }
    public void update(PhoneItem item){
        cartNames.add(item.getName());
        cartItemCount = (cartItemCount + 1);
        if(cartItemCount > 0){
            circle.setVisibility(View.VISIBLE);
            contentTextView.setText(String.valueOf(cartItemCount));
        }else{
            contentTextView.setText("");
        }
        item.cartCountUp();
        mItems.document(item._getID()).update("cartCount", item.getCartCount()).addOnFailureListener(fail ->{
            Toast.makeText(this, "Telefon kosárba adása sikertelen: " + item._getID(), Toast.LENGTH_LONG).show();
        });
        queryData();
    }
    public void delete(PhoneItem item){
        DocumentReference ref = mItems.document(item._getID());

        ref.delete().addOnSuccessListener(success ->{
            Log.d(LOG_TAG, "Telefon törölve: " + item._getID());
        }).addOnFailureListener(fail -> {
            Toast.makeText(this, "Telefon törlése sikertelen: " + item._getID(), Toast.LENGTH_LONG).show();
        });
        queryData();
    }
    public void updatePrice(PhoneItem item, String newPrice){
        mItems.document(item._getID()).update("price", newPrice).addOnFailureListener(fail ->{
            Toast.makeText(this, "Telefon árának felülírása sikertelen: " + item._getID(), Toast.LENGTH_LONG).show();
        });
        queryData();
    }
    private void startCheckout() {
        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra("count", cartItemCount);
        intent.putExtra("names", cartNames);
        startActivity(intent);
    }
    private void logOutToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}