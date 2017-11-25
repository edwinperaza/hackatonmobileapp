package cl.moriahdp.tarbaychile.models.product;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.microsoft.projectoxford.vision.contract.Tag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cl.moriahdp.tarbaychile.network.AppRequestManager;
import cl.moriahdp.tarbaychile.network.AppResponseListener;

/**
 * Created by edwinmperazaduran on 7/31/16.
 */
public class ProductRequestManager extends AppRequestManager {

    public static final String PRODUCT_LIST_API = BASE_URL + "products/list/1";
    public static final String PRODUCT_DETAIL = BASE_URL + "product/";
    public static final String PRODUCT_SUGGESTIONS = BASE_URL + "verifyByImage";

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

    public static JsonObjectRequest getInformationByTags(AppResponseListener<JSONObject> responseListener, List<Tag> tags) {

        JSONArray jsonArray = new JSONArray();
        JSONObject params = new JSONObject();
        for (Tag t: tags) {
            jsonArray.put(t.name);
        }
        try {
            params.put("tags", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new JsonObjectRequest(Request.Method.POST, PRODUCT_SUGGESTIONS, params, responseListener,
                responseListener);
    }

    public static JsonObjectRequest getProductDetail(AppResponseListener<JSONObject> responseListener, String productId) {

        JSONObject params = new JSONObject();
        String url = PRODUCT_DETAIL + productId;

        return new JsonObjectRequest(Request.Method.GET, url, params, responseListener,
                responseListener);
    }
}