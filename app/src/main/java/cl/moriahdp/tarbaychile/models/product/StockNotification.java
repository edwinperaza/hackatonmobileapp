package cl.moriahdp.tarbaychile.models.product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by edwinperaza on 11/25/17.
 */

public class StockNotification implements Serializable {

    public float prm_vta;
    public String analisis;
    public int prm_stock;
    public String producto;


    public static StockNotification fromJsonObject(JSONObject jsonObjectProduct){
        StockNotification stockNotification = new StockNotification();

        try {

            stockNotification.prm_vta = jsonObjectProduct.getLong("prm_vta");
            stockNotification.analisis = jsonObjectProduct.getString("analisis");
            stockNotification.prm_stock = jsonObjectProduct.getInt("prm_stock");
            stockNotification.producto = jsonObjectProduct.getString("producto");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  stockNotification;

    }

    public static List<StockNotification> fromJsonArray(JSONArray jsonArrayProducts){
        List<StockNotification> stockNotificationList = new ArrayList<>();

        for (int i=0; i < jsonArrayProducts.length(); i++){
            try {
                JSONObject jsonObject = jsonArrayProducts.getJSONObject(i);
                StockNotification stockNotification = StockNotification.fromJsonObject(jsonObject);

                if (stockNotification != null){
                    stockNotificationList.add(stockNotification);
                }

            }catch (JSONException e){
                e.printStackTrace();
                continue;
            }
        }

        return stockNotificationList;
    }
}
