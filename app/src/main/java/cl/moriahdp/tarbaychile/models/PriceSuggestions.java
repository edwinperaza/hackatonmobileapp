package cl.moriahdp.tarbaychile.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by edwinperaza on 11/25/17.
 */

public class PriceSuggestions implements Serializable {

    public String primaryNav;
    public int count;
    public String label;
    public Boolean selected;
    public String url;

    public PriceSuggestions() {
    }

    public static List<PriceSuggestions> fromJsonArray(JSONArray jsonArray){
        List<PriceSuggestions> categorySuggestions = new ArrayList<>();

        for (int i=0; i < jsonArray.length(); i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                PriceSuggestions categorySuggestion = PriceSuggestions.fromJsonObject(jsonObject);

                if (categorySuggestion != null){
                    categorySuggestions.add(categorySuggestion);
                }

            }catch (JSONException e){
                e.printStackTrace();
                continue;
            }
        }

        return categorySuggestions;
    }

    public static PriceSuggestions fromJsonObject(JSONObject jsonObjectProduct){
        PriceSuggestions categorySuggestion = new PriceSuggestions();

        try {

            categorySuggestion.primaryNav = jsonObjectProduct.getString("primaryNav");
            categorySuggestion.count = jsonObjectProduct.getInt("count");
            categorySuggestion.label = jsonObjectProduct.getString("label");
            categorySuggestion.selected = jsonObjectProduct.getBoolean("selected");
            categorySuggestion.url = jsonObjectProduct.getString("url");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  categorySuggestion;

    }

}
