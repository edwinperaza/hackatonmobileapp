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
import cl.moriahdp.tarbaychile.models.product.StockNotification;

/**
 * Created by edwinmperazaduran on 7/31/16.
 */
public class StockListAdapter extends ArrayAdapter<StockNotification> {

    List<StockNotification> mProductsList;

    public StockListAdapter(Context context, List<StockNotification> objects) {
        super(context, 0, objects);
        mProductsList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StockNotification stockNotification = mProductsList.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_notification_item, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.setProduct(convertView, stockNotification);
        return convertView;
    }

    private static class ViewHolder {
        ImageView mProductMainImageView;
        TextView mProductTitleView;
        TextView mProductCodeView;
        TextView mProductPromQtyView;

        public ViewHolder(View view){
//            mProductMainImageView = (ImageView) view.findViewById(R.id.ivMainImageProductList);
            mProductTitleView = (TextView) view.findViewById(R.id.tvTitleProductList);
            mProductCodeView = (TextView) view.findViewById(R.id.tv_code);
            mProductPromQtyView = (TextView) view.findViewById(R.id.tv_prom_qty);

        }

        public void setProduct(View view, StockNotification stockNotification){
            this.mProductTitleView.setText(stockNotification.analisis);
            this.mProductCodeView.setText(stockNotification.producto);
            this.mProductPromQtyView.setText(String.valueOf(stockNotification.prm_stock));
//
//            if (product.getUrlMainImage() != null) {
//                String img = product.getUrlMainImage().trim();
//                if (!img.equals("")) {
//                    Picasso.with(view.getContext()).load(img).placeholder(R.drawable.falabellachile).fit().into(mProductMainImageView);
//                }
//            }
        }

    }
}