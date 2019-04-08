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
    ArrayList<PasswordData> data;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_retreive_passwords, container, false);

        // referencing to the views
        recyclerView = view.findViewById(R.id.recyclerView);
        parentLayoutDataAvail = view.findViewById(R.id.parentLayoutDataAvail);
        parentLayoutDataNotAvail = view.findViewById(R.id.parentLayoutDataNotAvail);
        editTextSearchPasswords = view.findViewById(R.id.etSearchPasswords);

        data = new ArrayList<>();
        data.add(new PasswordData("Github.com","fakename@gmail.com","anything"));
        data.add(new PasswordData("Google","coolname@gmail.com","anything"));
        data.add(new PasswordData("Google","whoever@whatever.com","anything"));
        data.add(new PasswordData("Netflix","who@anything.com","anything"));
        data.add(new PasswordData("Protonmail","fake@original.com","anything"));
        data.add(new PasswordData("Amazon.com","9988776655","anything"));
        data.add(new PasswordData("Amazon.com","me@whatever.com","anything"));
        data.add(new PasswordData("Netflix","people@company.com","anything"));
        data.add(new PasswordData("Netflix","man@enterprise.com","anything"));
        data.add(new PasswordData("Amazon","women@something.com","anything"));


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new PasswordAdapter(getContext(), data);
        recyclerView.setAdapter(adapter);

        // edit text for filtering the list
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

    private void filterResult(String value){

        ArrayList<PasswordData> filteredList = new ArrayList<>();

        for(PasswordData pd : data){
            String provider = pd.getServiceProvider();
            String id = pd.getLoginId();
            if(provider.toLowerCase().contains(value) || id.toLowerCase().contains(value)){
                filteredList.add(pd);
            }
        }

        adapter.filterList(filteredList);

    }

}
