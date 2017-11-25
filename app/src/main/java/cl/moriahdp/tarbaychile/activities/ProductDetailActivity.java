package cl.moriahdp.tarbaychile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.toolbox.JsonObjectRequest;
import com.roughike.bottombar.BottomBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cl.moriahdp.tarbaychile.R;
import cl.moriahdp.tarbaychile.models.product.Product;
import cl.moriahdp.tarbaychile.models.product.ProductRequestManager;
import cl.moriahdp.tarbaychile.network.AppResponseListener;
import cl.moriahdp.tarbaychile.network.VolleyManager;

public class ProductDetailActivity extends GeneralActivity {

    private BottomBar mBottomBar;
    private String mProductCode;
    private Product mProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Intent intent = getIntent();
        mProduct =  (Product) intent.getSerializableExtra("product");
        setSupportActionBar(toolbar);
        populateProductDetail();

    }

    void populateProductDetail() {
            AppResponseListener<JSONObject> appResponseListener = new AppResponseListener<JSONObject>(
                    getApplicationContext()){

                @Override
                public void onResponse(JSONObject response) {
                    JSONArray jsonArrayProducts = null;
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = response.getJSONArray("").getJSONObject(0);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    List<Product> newsProducts = Product.fromJsonArray(jsonArrayProducts);

                }

                @Override
                public void noInternetConnectionError() {
                    super.noInternetConnectionError();
                }

                @Override
                public void noInternetError() {
                    super.noInternetError();
                }
            };

            //We add the request
            JsonObjectRequest request = ProductRequestManager.getProductDetail(appResponseListener, mProduct.getCode());
            VolleyManager.getInstance(this).addToRequestQueue(request);
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
        switch (id){
            case R.id.logOut: showLogOutAlertDialogLogOut(); break;
        }

        return super.onOptionsItemSelected(item);
    }
}
