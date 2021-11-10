package com.kimi.easyget;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.kimi.easyget.cart.CartFragment;
import com.kimi.easyget.cart.model.ProductTransaction;
import com.kimi.easyget.cart.model.UserShoppingCart;
import com.kimi.easyget.categories.CategoriesFragment;
import com.kimi.easyget.home.HomeFragment;
import com.kimi.easyget.lists.ListsFragment;
import com.kimi.easyget.offer.OffersFragment;
import com.kimi.easyget.products.models.ProductTransactionViewModel;
import com.kimi.easyget.search.SearchFragment;
import com.kimi.easyget.user.AccountFragment;
import com.kimi.easyget.user.models.Session;
import com.kimi.easyget.user.models.User;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static java.util.Objects.isNull;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private TextView textCartItemCount;
    private List<ProductTransaction> products = new ArrayList<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        setWidget();
        registerSession();

        final ProductTransactionViewModel productTransactionViewModel =
                new ViewModelProvider(this).get(ProductTransactionViewModel.class);
        productTransactionViewModel.getSelectedProduct().observe(this, new Observer<ProductTransaction>() {
            @Override
            public void onChanged(ProductTransaction productTransaction) {
                addProductToUserCart(productTransaction);

            }
        });
    }

    private void registerSession() {
        final User user = getCurrentUser();
        Session session = Session.builder()
                .userId(user.getUid())
                .device("SAMSUNG SM-A205G")
                .OS("Android 10, API 29")
                .registration(FieldValue.serverTimestamp())
                .build();

        db.collection("userSessionModels")
                .add(session)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error writing document", e);
                });
    }

    private void getProductToUserCart() {
        final User user = getCurrentUser();
        db.collection("userShoppingCarts")
                .document("cart-" + user.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("carrito compras failed", "Listen failed.", e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            Log.d(TAG, "carrito compras succ: " + snapshot.getData());
                            final UserShoppingCart userShoppingCarts =
                                    snapshot.toObject(UserShoppingCart.class);
                            products = isNull(userShoppingCarts.getProducts()) ? new ArrayList<>() : userShoppingCarts.getProducts();

                            final int size = isNull(userShoppingCarts.getProducts()) ? 0 : userShoppingCarts.getProducts().size();
                            textCartItemCount.setText(String.valueOf(size));
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });
    }

    private void addProductToUserCart(final ProductTransaction product) {

        final User user = getCurrentUser();
        products.add(product);

        final UserShoppingCart userShoppingCart = UserShoppingCart.builder()
                .user(user)
                .products(products)
                .build();

        db.collection("userShoppingCarts")
                .document("cart-" + user.getUid())
                .set(userShoppingCart)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                    Toast.makeText(getApplicationContext(),
                            String.valueOf("Se agrego " + product.getName() + " con exito!"), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error writing document", e);
                });


    }

    private User getCurrentUser() {
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        return User.builder()
                .displayName(firebaseUser.getDisplayName())
                .email(firebaseUser.getEmail())
                .uid(firebaseUser.getUid())
                .build();

    }

    private void setWidget() {
        final BottomNavigationView bottomNavigationMenu = findViewById(R.id.main_menu);
        final FrameLayout cart = findViewById(R.id.cart);
        textCartItemCount = findViewById(R.id.cart_badge);
        bottomNavigationMenu.setOnNavigationItemSelectedListener(navListener);
        addFragment(new HomeFragment());
        bottomNavigationMenu.setSelectedItemId(R.id.home);


        //SEARCHVIEW
        final SearchView searchProduct = (SearchView)findViewById(R.id.search_product);
        View v = searchProduct.findViewById(R.id.search_plate);
        v.setBackgroundColor(getColor(R.color.white));
        EditText searchEditText = searchProduct.findViewById(R.id.search_src_text);
        searchEditText.setTextColor(getColor(R.color.black));
        searchEditText.setHintTextColor(getColor(R.color.gris_sof));
        ImageView icon = searchProduct.findViewById(R.id.search_button);
        icon.setColorFilter(Color.BLACK);
        searchProduct.setFocusable(false);
        searchProduct.setIconified(false);
        searchProduct.setIconifiedByDefault(false);
        searchProduct.clearFocus();

        searchProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Click lis", "hola");
            }
        });

        searchProduct.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchFragment searchFragment = SearchFragment.newInstance(query);
                addFragment(searchFragment);
                searchProduct.setQuery("", false);
                searchProduct.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("Click lis", "hola");
                return false;
            }
        });


        cart.setOnClickListener(view -> addFragment(new CartFragment()));

        getProductToUserCart();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = menuItem -> {
        final int id = menuItem.getItemId();
        switch (id) {
            case R.id.home_option:
                addFragment(new HomeFragment());
                break;
            case R.id.lists_option:
                addFragment(new ListsFragment());
                break;
            case R.id.ofert_option:
                addFragment(new OffersFragment());
                break;
            case R.id.categories_options:
                addFragment(new CategoriesFragment());
                break;
            case R.id.accout_option:
                addFragment(new AccountFragment());
                break;
        }
        return true;
    };

    private void addFragment(final Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

//        final MenuItem menuItem = menu.findItem(R.id.action_cart);
//        View actionView = menuItem.getActionView();
//        textCartItemCount = actionView.findViewById(R.id.cart_badge);
//        actionView.setOnClickListener(view -> onOptionsItemSelected(menuItem));


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_cart: {
                Log.d("hola", "hice click");
                addFragment(new CartFragment());
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void setupBadge() {
        Log.d("setupBadge", products.toString());
        final int mCartItemCount = products.size();

        Log.d("setupBadge mCartItemCount", String.valueOf(mCartItemCount));
        if (textCartItemCount != null) {
            if (mCartItemCount == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(mCartItemCount, 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public boolean onSearchRequested() {
        return super.onSearchRequested();
    }
}