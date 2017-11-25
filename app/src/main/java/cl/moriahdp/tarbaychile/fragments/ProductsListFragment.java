package cl.moriahdp.tarbaychile.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cl.moriahdp.tarbaychile.R;
import cl.moriahdp.tarbaychile.adapters.ProductsListAdapter;
import cl.moriahdp.tarbaychile.models.product.Product;
import cl.moriahdp.tarbaychile.models.product.ProductRequestManager;
import cl.moriahdp.tarbaychile.network.AppResponseListener;
import cl.moriahdp.tarbaychile.network.VolleyManager;

/**
 * Created by edwinmperazaduran on 7/31/16.
 */
public class ProductsListFragment extends Fragment {

    private static final String ARG_TITLE = "TITLE";
    private ListView mProductsListView;
    private ProductsListAdapter mProductsListAdapter;
    private ArrayList<Product> mProductsArrayList;
    private Context mContext;
    private onItemSelectedListener mListener;
    private View mLoadingOverlay;

    public ProductsListFragment() {

    }

    public interface onItemSelectedListener {
        void onProductItemSelected(Product product);
    }

    public static ProductsListFragment newInstance(String title) {
        ProductsListFragment fragment = new ProductsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProductsArrayList = new ArrayList<>();
        mProductsListAdapter = new ProductsListAdapter(getActivity(), mProductsArrayList);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof onItemSelectedListener) {
            mListener = (onItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ProductsListFragment.OnItemSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products_list, container, false);

        mProductsListView = (ListView) view.findViewById(R.id.lvProductsList);
        mLoadingOverlay = view.findViewById(R.id.pb_base);
        mLoadingOverlay.setVisibility(View.VISIBLE);
        mProductsListView.setAdapter(mProductsListAdapter);
        populateProducts();


        mProductsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product product = (Product) parent.getItemAtPosition(position);
                mListener.onProductItemSelected(product);
            }
        });
        return view;
    }

    public void populateProducts(){
        AppResponseListener<JSONObject> appResponseListener = new AppResponseListener<JSONObject>(
                                                            getActivity().getApplicationContext()){

            @Override
            public void onResponse(JSONObject response) {
                JSONArray jsonArrayProducts = null;
                try {
                    jsonArrayProducts = response.getJSONArray("lista");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                List<Product> newsProducts = Product.fromJsonArray(jsonArrayProducts);

                if (!newsProducts.isEmpty()) {
                    for (int i = 0; i < newsProducts.size(); i++) {
                        mProductsListAdapter.insert(newsProducts.get(i), i);
                    }
                }
                mLoadingOverlay.setVisibility(View.GONE);
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
        JsonObjectRequest request = ProductRequestManager.getProductsList(appResponseListener);
        VolleyManager.getInstance(getContext()).addToRequestQueue(request);
    }
}