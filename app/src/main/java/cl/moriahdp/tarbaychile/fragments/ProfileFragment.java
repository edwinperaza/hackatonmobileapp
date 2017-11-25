package cl.moriahdp.tarbaychile.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import cl.moriahdp.tarbaychile.R;


public class ProfileFragment extends Fragment {

    private onOptionSelectedListener mListener;

    private SimpleDraweeView mProfileImageView;
    private TextView mLogOutView;
    private TextView mContactUs;
    private ImageView mAddFriend;
    private Context mContext;

    public ProfileFragment(){}

    public interface onOptionSelectedListener {
        void onLogOutSelectListener();
        void onContactUsListener();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof onOptionSelectedListener) {
            mListener = (onOptionSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ProfileFragment.onLogOutSelectListener");
        }
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mProfileImageView = (SimpleDraweeView) view.findViewById(R.id.sdv_profile_image);
        mLogOutView = (TextView) view.findViewById(R.id.tv_log_out);
        mContactUs = (TextView) view.findViewById(R.id.tv_contact_us);
        mAddFriend = (ImageView) view.findViewById(R.id.iv_add_friend);

        Uri uri = Uri.parse("http://evento.juridia.co/wp-content/uploads/2016/05/client2.png");
        mProfileImageView.setImageURI(uri);
        mProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(getTag(), "Image Profile");
            }
        });
        mLogOutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLogOutSelectListener();
            }
        });

        mContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onContactUsListener();
            }
        });

        mAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAction();
            }
        });
        return view;
    }

    public void shareAction(){
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, "http://www.tarbay.com/");
        startActivity(Intent.createChooser(share, getResources().getString(R.string.profile_share_title)));
    }

}