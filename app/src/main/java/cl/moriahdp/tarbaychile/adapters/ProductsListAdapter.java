package cl.moriahdp.tarbaychile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import cl.moriahdp.tarbaychile.R;
import cl.moriahdp.tarbaychile.models.product.Product;

/**
 * Created by edwinmperazaduran on 7/31/16.
 */
public class ProductsListAdapter extends ArrayAdapter<Product> {
    List<Product> mProductsList;

    public ProductsListAdapter(Context context, List<Product> objects) {
        super(context, 0, objects);
        mProductsList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Product product = mProductsList.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_products_item, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.setProduct(convertView, product);
        return convertView;
    }

    private static class ViewHolder {
        ImageView mProductMainImageView;
        TextView mProductTitleView;

        public ViewHolder(View view){
            mProductMainImageView = (ImageView) view.findViewById(R.id.ivMainImageProductList);
            mProductTitleView = (TextView) view.findViewById(R.id.tvTitleProductList);

        }

        public void setProduct(View view, Product product){
            this.mProductTitleView.setText(product.getTitle());

            if (product.getUrlMainImage() != null) {
                String img = product.getUrlMainImage().trim();
                if (!img.equals("")) {
                    Picasso.with(view.getContext()).load(img).placeholder(R.drawable.falabellachile).fit().into(mProductMainImageView);
                }
            }
        }

    }
}