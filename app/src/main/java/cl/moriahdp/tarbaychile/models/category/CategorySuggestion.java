package cl.moriahdp.tarbaychile.models.category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cl.moriahdp.tarbaychile.models.product.Product;

/**
 * Created by edwinperaza on 11/25/17.
 */

public class CategorySuggestion implements Serializable {

    public String primaryNav;
    public String count;
    public String label;
    public Boolean selected;
    public String url;

    public CategorySuggestion() {
    }

    public static List<CategorySuggestion> fromJsonArray(JSONArray jsonArray){
        List<CategorySuggestion> categorySuggestions = new ArrayList<>();

        for (int i=0; i < jsonArray.length(); i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CategorySuggestion categorySuggestion = CategorySuggestion.fromJsonObject(jsonObject);

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

    public static CategorySuggestion fromJsonObject(JSONObject jsonObjectProduct){
        CategorySuggestion categorySuggestion = new CategorySuggestion();

        try {

            categorySuggestion.primaryNav = jsonObjectProduct.getString("primaryNav");
            categorySuggestion.count = jsonObjectProduct.getString("count");
            categorySuggestion.label = jsonObjectProduct.getString("label");
            categorySuggestion.selected = jsonObjectProduct.getBoolean("selected");
            categorySuggestion.url = jsonObjectProduct.getString("url");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  categorySuggestion;

    }

}
