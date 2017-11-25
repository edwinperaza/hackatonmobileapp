package cl.moriahdp.tarbaychile.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import cl.moriahdp.tarbaychile.R;
import cl.moriahdp.tarbaychile.fragments.CameraFragment;
import cl.moriahdp.tarbaychile.fragments.CategoriesListFragment;
import cl.moriahdp.tarbaychile.fragments.ContactUsFragment;
import cl.moriahdp.tarbaychile.fragments.NotificationListFragment;
import cl.moriahdp.tarbaychile.fragments.ProductsListFragment;
import cl.moriahdp.tarbaychile.fragments.ProfileFragment;
import cl.moriahdp.tarbaychile.models.product.Product;
import cl.moriahdp.tarbaychile.utils.PreferencesManager;

public class MainActivity extends GeneralActivity
        implements ProductsListFragment.onItemSelectedListener,
        ProfileFragment.onOptionSelectedListener {

    private BottomBar mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Context context = getApplicationContext();


        if (!PreferencesManager.isUserLogged(context)) {

            startActivityClosingAllOthers(LoginActivity.class);

        } else {
            mBottomBar = BottomBar.attach(this, savedInstanceState);
            /*
            * Prevent to add BottomBar background when we have more than 3 item, if we don't use
            * this method we will need to call for each tab the next method:
            * mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorToolBar));
            */
            mBottomBar.setMaxFixedTabs(5);
            // Set the layout to the BottomBar
            mBottomBar.setItems(R.menu.menu_bottom_bar);
            // Set the default tab for this BottomBar that is shown until the user changes the selection.
            mBottomBar.setDefaultTabPosition(0);
            // Set the Background Color of the BottomBar (Also) the application
            mBottomBar.setBackgroundColor(Color.WHITE);
            // Set the Color for the Active Tab of the BottomBar
            mBottomBar.setActiveTabColor(ContextCompat.getColor(context, R.color.plp_add_to_cart_button));

            mBottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
                @Override
                public void onMenuTabSelected(@IdRes int menuItemId) {
                    switch (menuItemId) {
                        case R.id.home_item: {
                            Log.d("MainActivity", "Home");
                            ShowProductListFragment();
                            //ShowCamera();
                            break;
                        }
                        case R.id.search_item: {
                            Log.d("MainActivity", "Search");
                            ShowCategoriesListFragment();
                            break;
                        }
//                        case R.id.favorite_item: {
//                            Log.d("MainActivity", "Favorite");
//                            break;
//                        }
                        case R.id.profile_item: {
                            Log.d("MainActivity", "Profile");
                            ShowProfileFragment();
                            break;
                        }
                    }
                }

                @Override
                public void onMenuTabReSelected(@IdRes int menuItemId) {
                    //TODO SCROLLING TO THE TOP OF OUR CONTENT
                }
            });
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
        mBottomBar.onSaveInstanceState(outState);
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.logOut:
//                showLogOutAlertDialogLogOut();
                Intent intent = new Intent(getApplicationContext(), AnalyzeActivity.class);
//                intent.putExtra("product", product);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProductItemSelected(Product product) {
        if (product != null) {

            Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);
            intent.putExtra("product", product);
            startActivity(intent);

        }
    }

    public void ShowCamera() {
        Fragment mProductsListFragment = CameraFragment.newInstance("Camera");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFragment, mProductsListFragment);
        ft.commit();
    }

    public void ShowProductListFragment(){
        Fragment mProductsListFragment = ProductsListFragment.newInstance("Productos");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFragment, mProductsListFragment);
        ft.commit();
    }

    public void ShowCategoriesListFragment(){
//        Fragment mCategoriesListFragment = CategoriesListFragment.newInstance();
        Fragment mCategoriesListFragment = NotificationListFragment.newInstance("Avisos");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFragment, mCategoriesListFragment);
        ft.commit();
    }

    public void ShowProfileFragment(){
        Fragment mProfileFragment = ProfileFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFragment, mProfileFragment);
        ft.commit();
    }

    @Override
    public void onLogOutSelectListener() {
        showLogOutAlertDialogLogOut();
    }

    @Override
    public void onContactUsListener() {

            Fragment mProfileFragment = ContactUsFragment.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFragment, mProfileFragment);
            ft.commit();

    }
}