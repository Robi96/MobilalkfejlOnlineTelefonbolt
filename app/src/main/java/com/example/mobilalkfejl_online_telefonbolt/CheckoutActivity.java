package com.example.mobilalkfejl_online_telefonbolt;

import android.content.Intent;
import android.os.Bundle;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class CheckoutActivity extends AppCompatActivity {
    private static final String LOG_TAG = CheckoutActivity.class.getName();

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;
    private RecyclerView mRecycleView;
    private ArrayList<PhoneItem> mItemList;
    private CheckoutAdapter mAdapter;
    private ArrayList<String> cartNames = new ArrayList<>();
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.checkout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Bundle checkoutBundle = getIntent().getExtras();
        if(checkoutBundle != null){
            Log.d(LOG_TAG, "Data transferred");
        }
        assert checkoutBundle != null;
        cartNames = checkoutBundle.getStringArrayList("names");
        count = checkoutBundle.getInt("count");
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            Log.d(LOG_TAG, "Autentikált felhasználó");
        }else {
            Log.d(LOG_TAG, "Sikertelen azonosítás");
            finish();
        }
        mRecycleView = findViewById(R.id.checkoutRecyclerView);
        mRecycleView.setLayoutManager(new GridLayoutManager(this, 1));
        mItemList = new ArrayList<>();

        mAdapter = new CheckoutAdapter(this, mItemList);
        mRecycleView.setAdapter(mAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Items");
        queryData();
    }

    public void queryData(){
        mItemList.clear();
        TextView asd = findViewById(R.id.cart_total);
        String temp = count + " termék van a kosárban!";
        asd.setText(temp);
        mItems.orderBy("price").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (String name : cartNames) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    PhoneItem item = doc.toObject(PhoneItem.class);
                    if (Objects.equals(name, item.getName())) {
                        Log.d("ID", String.valueOf(item.getResource()));
                        item.setID(doc.getId());
                        mItemList.add(item);
                    }
                }
            }
            mAdapter.notifyDataSetChanged();
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.checkout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        back();
        return true;
    }
    public void back(){
        Intent intent = new Intent(this, ShopMainSite.class);
        intent.putExtra("count", count);
        intent.putExtra("names", cartNames);
        startActivity(intent);
    }
    public void update(PhoneItem item){
        count = (count - 1);
        cartNames.remove(item.getName());
        item.cartCountDown();
        int temp = item.getCartCount();
        mItems.document(item._getID()).update("cartCount", temp).addOnFailureListener(fail ->{
            Toast.makeText(this, "Telefon kosárból törlése sikertelen: " + item._getID(), Toast.LENGTH_LONG).show();
        });
        queryData();
    }
    public void purgeCart(View view){
        count = 0;
        cartNames.clear();
        for(PhoneItem item : mItemList){
            item.setCartCount(0);
            mItems.document(item._getID()).update("cartCount", 0).addOnFailureListener(fail ->{
                Toast.makeText(this, "Teljes kosár törlése sikertelen: " + item._getID(), Toast.LENGTH_LONG).show();
            });
        }
        queryData();
    }
}