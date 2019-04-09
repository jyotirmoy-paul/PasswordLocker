package android.cipherresfeber.passwordlocker.Adapters;

import android.cipherresfeber.passwordlocker.BottomSheetFragment.PasswordBottomSheetFragment;
import android.cipherresfeber.passwordlocker.R;
import android.cipherresfeber.passwordlocker.UserDataTypes.PasswordData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
        TextView txvPassword;
        LinearLayout parentLayout;

        public PasswordViewHolder(@NonNull View itemView) {
            super(itemView);
            txvServiceProviderName = itemView.findViewById(R.id.txvServiceProviderName);
            txvLoginId = itemView.findViewById(R.id.txvLoginId);
            txvPassword = itemView.findViewById(R.id.txvPassword);
            parentLayout = itemView.findViewById(R.id.parentLayoutPasswordItem);
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
        PasswordData data = list.get(i);
        passwordViewHolder.txvServiceProviderName.setText(data.getServiceProvider());
        passwordViewHolder.txvLoginId.setText(data.getLoginId());
        passwordViewHolder.txvPassword.setText(data.getPassword());

        passwordViewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open the bottom sheet
                PasswordBottomSheetFragment bottomSheet = new PasswordBottomSheetFragment();
                bottomSheet.show(fragmentManager, "passwordBottomSheetDialog");
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
