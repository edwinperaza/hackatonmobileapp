package cl.moriahdp.tarbaychile.models.product;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import cl.moriahdp.tarbaychile.network.AppRequestManager;
import cl.moriahdp.tarbaychile.network.AppResponseListener;

/**
 * Created by edwinmperazaduran on 7/31/16.
 */
public class ProductRequestManager extends AppRequestManager {

    public static final String PRODUCT_LIST_API = BASE_URL + "products/list/1";

    /**
     * Creates a new JsonObjectRequest for get a list of stories
     *
     * @param responseListener The listener for on success and error callbacks
     * @return The created JsonObjectRequest for create user webservice
     */
    public static JsonObjectRequest getProductsList(AppResponseListener<JSONObject> responseListener) {

        JSONObject params = new JSONObject();

        return new JsonObjectRequest(Request.Method.GET, PRODUCT_LIST_API, params, responseListener,
                                     responseListener);
    }

    public static JsonObjectRequest getInformationByTags(AppResponseListener<JSONObject> responseListener, String tags) {

        JSONObject params = new JSONObject();
        try {
            params.put("tags", tags);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new JsonObjectRequest(Request.Method.POST, PRODUCT_LIST_API, params, responseListener,
                responseListener);
    }
}