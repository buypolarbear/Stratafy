package com.stratafy.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.like.LikeButton;
import com.stratafy.R;
import com.stratafy.activities.MainActivity;
import com.stratafy.fragment.DialogStatastic;
import com.stratafy.helper.Glob;
import com.stratafy.helper.MyApplication;
import com.stratafy.model.Laws;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cn on 8/21/2017.
 */

public class AdapterLaws extends RecyclerView.Adapter<AdapterLaws.MyViewHolder> {
    private Context context;
    private List<Laws> mLawsList;
    private final LayoutInflater inflater;
    boolean isSelectAgree, isSelectDisAgree;

    public AdapterLaws(Context context, List<Laws> mLawsList){
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.mLawsList = mLawsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_laws, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final Laws laws = mLawsList.get(position);
        holder.txtLawName.setText(position + 1 + " | " + laws.getLaw_name());
        holder.txtLawText.setText(laws.getLaw_text());

        if(laws.getStatus() != null && !laws.getStatus().isEmpty()){
            Log.d("isStaus", "Yes");
            if(laws.getStatus().equals("1")){
                holder.ic_Agree.setDrawingCacheBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                holder.ic_Agree.setLiked(true);
                holder.txtAgreed.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                holder.ic_disagree.setLiked(false);
                holder.txtDisagreed.setTextColor(ContextCompat.getColor(context, R.color.greyDark));
            }else {
                holder.ic_disagree.setLiked(true);
                holder.txtDisagreed.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                holder.ic_Agree.setLiked(false);
                holder.txtAgreed.setTextColor(ContextCompat.getColor(context, R.color.greyDark));
            }
        }else {
            Log.d("isStaus", "NO");
        }

        holder.llDisagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.ic_disagree.setLiked(true);
                holder.txtDisagreed.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                holder.ic_Agree.setLiked(false);
                holder.txtAgreed.setTextColor(ContextCompat.getColor(context, R.color.greyDark));
                postStatus(context, laws.getLaw_id(), "0");
            }
        });

        holder.llAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.ic_Agree.setDrawingCacheBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                holder.ic_Agree.setLiked(true);
                holder.txtAgreed.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                holder.ic_disagree.setLiked(false);
                holder.txtDisagreed.setTextColor(ContextCompat.getColor(context, R.color.greyDark));
                postStatus(context, laws.getLaw_id(), "1");
            }
        });

        holder.img_statastic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("law_name", laws.getLaw_name());
                bundle.putString("law_text", laws.getLaw_text());
                bundle.putString("agree", laws.getAgree());
                bundle.putString("disagree", laws.getDisagree());
                FragmentTransaction ft = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                DialogStatastic newFragment = DialogStatastic.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }
        });

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mLawsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtLawName, txtLawText, txtAgreed, txtDisagreed, txtComment;
        LinearLayout llDisagree, llAgree;
        LikeButton ic_disagree, ic_Agree;
        ImageView img_statastic;
        public MyViewHolder(View itemView) {
            super(itemView);

            txtLawName = (TextView)itemView.findViewById(R.id.txtLawName);
            txtLawText = (TextView)itemView.findViewById(R.id.txtLawText);
            txtAgreed = (TextView)itemView.findViewById(R.id.txtAgreed);
            txtDisagreed = (TextView)itemView.findViewById(R.id.txtDisagreed);
            txtComment = (TextView)itemView.findViewById(R.id.txtComment);

            llDisagree = (LinearLayout) itemView.findViewById(R.id.llDisagree);
            llAgree = (LinearLayout) itemView.findViewById(R.id.llAgree);

            ic_disagree = (LikeButton)itemView.findViewById(R.id.ic_disagree);
            ic_Agree = (LikeButton)itemView.findViewById(R.id.ic_agree);

            img_statastic = (ImageView)itemView.findViewById(R.id.img_statastic);
        }
    }

    private void postStatus(final Context context, final String law_id, final String status) {
        String url = Glob.API_AGREE_DISAGREE + MainActivity.property_id + ".json";
        Log.d("URL", url);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "EVENT Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    int status = jObj.getInt("status");
                    final String errorMsg = jObj.getString("errorMsg");
                    if (status != 0) {
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (final JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                Log.e("TAG", "Login Error: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", String.valueOf(MainActivity.uid));
                params.put("law_id", law_id);
                params.put("status", status);
                Log.d("PARAM", params.toString());
                return params;
            }
        };
        MyApplication.getInstance().addToRequestQueue(strReq);
    }
}
