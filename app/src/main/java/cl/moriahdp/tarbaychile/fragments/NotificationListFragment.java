package cl.moriahdp.tarbaychile.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cl.moriahdp.tarbaychile.R;
import cl.moriahdp.tarbaychile.adapters.CategoriesListExpandableAdapter;
import cl.moriahdp.tarbaychile.adapters.ProductsListAdapter;
import cl.moriahdp.tarbaychile.adapters.StockListAdapter;
import cl.moriahdp.tarbaychile.models.category.Category;
import cl.moriahdp.tarbaychile.models.product.Product;
import cl.moriahdp.tarbaychile.models.product.ProductRequestManager;
import cl.moriahdp.tarbaychile.models.product.StockNotification;
import cl.moriahdp.tarbaychile.models.subcategory.SubCategory;
import cl.moriahdp.tarbaychile.network.AppResponseListener;
import cl.moriahdp.tarbaychile.network.VolleyManager;

public class NotificationListFragment extends Fragment {

    private static final String ARG_TITLE = "TITLE";
    private ListView mProductsListView;
    private StockListAdapter mstockListAdapter;
    private ArrayList<StockNotification> mProductsArrayList;
    private Context mContext;
    private NotificationListFragment.onItemSelectedListener mListener;
    private View mLoadingOverlay;

    public NotificationListFragment() {
    }

    public interface onItemSelectedListener {
        void onNotificationItemSelected(StockNotification stockNotification);
    }

    public static NotificationListFragment newInstance(String title) {
        NotificationListFragment fragment = new NotificationListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProductsArrayList = new ArrayList<>();
        mstockListAdapter = new StockListAdapter(getActivity(), mProductsArrayList);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof NotificationListFragment.onItemSelectedListener) {
            mListener = (NotificationListFragment.onItemSelectedListener) context;
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
        mProductsListView.setAdapter(mstockListAdapter);
        populateProducts();


        mProductsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StockNotification stockNotification = (StockNotification) parent.getItemAtPosition(position);
                mListener.onNotificationItemSelected(stockNotification);
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
                List<StockNotification> stockNotificationList = StockNotification.fromJsonArray(jsonArrayProducts);

                if (!stockNotificationList.isEmpty()) {
                    for (int i = 0; i < stockNotificationList.size(); i++) {
                        mstockListAdapter.insert(stockNotificationList.get(i), i);
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
        JsonObjectRequest request = ProductRequestManager.getStockList(appResponseListener);
        VolleyManager.getInstance(getContext()).addToRequestQueue(request);
    }
}