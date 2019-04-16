package paul.cipherresfeber.passwordlocker.Adapters;

import paul.cipherresfeber.passwordlocker.BottomSheetFragment.DecryptionBottomSheetFragment;
import paul.cipherresfeber.passwordlocker.Constants.DatabaseConstants;
import paul.cipherresfeber.passwordlocker.R;
import paul.cipherresfeber.passwordlocker.UserDataTypes.PasswordData;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.PasswordViewHolder>{

    Context context;
    ArrayList<PasswordData> list;
    FragmentManager fragmentManager;

    // constructor for the PasswordAdapter class
    public PasswordAdapter(Context context, ArrayList<PasswordData> list, FragmentManager fragmentManager){
        this.context = context;
        this.list = list;
        this.fragmentManager = fragmentManager;
    }

    public class PasswordViewHolder extends RecyclerView.ViewHolder{

        TextView txvServiceProviderName;
        TextView txvLoginId;
        RelativeLayout parentLayout;
        TextView txvLastUpdatedDate;

        public PasswordViewHolder(@NonNull View itemView) {
            super(itemView);
            txvServiceProviderName = itemView.findViewById(R.id.txvServiceProviderName);
            txvLoginId = itemView.findViewById(R.id.txvLoginId);
            parentLayout = itemView.findViewById(R.id.parentLayoutPasswordItem);
            txvLastUpdatedDate = itemView.findViewById(R.id.txvLastUpdatedDate);
        }
    }

    @NonNull
    @Override
    public PasswordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.password_item, viewGroup,
                false);
        return new PasswordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PasswordViewHolder passwordViewHolder, int i) {
        final PasswordData data = list.get(i);
        passwordViewHolder.txvServiceProviderName.setText(data.getServiceProvider());
        passwordViewHolder.txvLoginId.setText(data.getLoginId());
        passwordViewHolder.itemView.setTag(data.getFirebaseKey());
        passwordViewHolder.txvLastUpdatedDate.setText(data.getLastUpdatedDate());

        passwordViewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open the bottom sheet
                DecryptionBottomSheetFragment bottomSheet = new DecryptionBottomSheetFragment();

                // using bundle to pass the service provider, login id and password info
                Bundle bundle = new Bundle();
                bundle.putString(DatabaseConstants.SERVICE_PROVIDER_NAME, data.getServiceProvider());
                bundle.putString(DatabaseConstants.LOGIN_ID, data.getLoginId());
                bundle.putString(DatabaseConstants.PASSWORD, data.getPassword());

                bottomSheet.setArguments(bundle);
                bottomSheet.show(fragmentManager, "DecryptionBottomSheetDialog");
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filterList(ArrayList<PasswordData> filteredList){
        list = filteredList;
        notifyDataSetChanged();
    }

}
