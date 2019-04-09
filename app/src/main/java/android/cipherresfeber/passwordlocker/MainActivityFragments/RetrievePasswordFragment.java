package android.cipherresfeber.passwordlocker.MainActivityFragments;

import android.cipherresfeber.passwordlocker.Adapters.PasswordAdapter;
import android.cipherresfeber.passwordlocker.R;
import android.cipherresfeber.passwordlocker.UserDataTypes.PasswordData;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class RetrievePasswordFragment extends Fragment {

    RecyclerView recyclerView;
    LinearLayout parentLayoutDataAvail;
    LinearLayout parentLayoutDataNotAvail;
    EditText editTextSearchPasswords;

    PasswordAdapter adapter;
    ArrayList<PasswordData> passwordList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_retreive_passwords, container, false);

        // referencing to the views
        recyclerView = view.findViewById(R.id.recyclerView);
        parentLayoutDataAvail = view.findViewById(R.id.parentLayoutDataAvail);
        parentLayoutDataNotAvail = view.findViewById(R.id.parentLayoutDataNotAvail);
        editTextSearchPasswords = view.findViewById(R.id.etSearchPasswords);

        passwordList = new ArrayList<>();


        // TODO: populate the passwordList from firebase database


        // recycler view adapter and layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new PasswordAdapter(getContext(), passwordList, getFragmentManager());
        recyclerView.setAdapter(adapter);

        // edit text for filtering the list according to user's search
        editTextSearchPasswords.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterResult(s.toString().toLowerCase());
            }
        });

        return view;
    }

    // utility method for filtering user searched keyword through the entire password database
    private void filterResult(String value){
        ArrayList<PasswordData> filteredList = new ArrayList<>();
        for(PasswordData pd : passwordList){
            String provider = pd.getServiceProvider();
            String id = pd.getLoginId();
            if(provider.toLowerCase().contains(value) || id.toLowerCase().contains(value)){
                filteredList.add(pd);
            }
        }
        adapter.filterList(filteredList);
    }

}
