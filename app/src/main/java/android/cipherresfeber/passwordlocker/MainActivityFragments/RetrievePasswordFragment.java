package android.cipherresfeber.passwordlocker.MainActivityFragments;

import android.cipherresfeber.passwordlocker.Adapters.PasswordAdapter;
import android.cipherresfeber.passwordlocker.Constants.DatabaseConstants;
import android.cipherresfeber.passwordlocker.Constants.UserConstants;
import android.cipherresfeber.passwordlocker.R;
import android.cipherresfeber.passwordlocker.UserDataTypes.PasswordData;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.SupportActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;

public class RetrievePasswordFragment extends Fragment {

    RecyclerView recyclerView;
    LinearLayout parentLayoutDataAvail;
    LinearLayout parentLayoutDataNotAvail;
    EditText editTextSearchPasswords;

    PasswordAdapter adapter;
    ArrayList<PasswordData> passwordList;

    CollectionReference reference;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_retreive_passwords, container, false);

        reference = FirebaseFirestore.getInstance()
                .collection(DatabaseConstants.DATABASE_PASSWORD_COLLECTION)
                .document("user_uid")
                .collection(DatabaseConstants.DATABASE_USER_PASSWORD);

        // referencing to the views
        recyclerView = view.findViewById(R.id.recyclerView);
        parentLayoutDataAvail = view.findViewById(R.id.parentLayoutDataAvail);
        parentLayoutDataNotAvail = view.findViewById(R.id.parentLayoutDataNotAvail);
        editTextSearchPasswords = view.findViewById(R.id.etSearchPasswords);

        passwordList = new ArrayList<>();

        // get all the data for the first time
        reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot ds: queryDocumentSnapshots){
                    passwordList.add(ds.toObject(PasswordData.class));
                }
                adapter.notifyDataSetChanged();
            }
        });


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

        // deletion of password entry --> swipe left
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                String key = (String) viewHolder.itemView.getTag();
                // delete the entry from firestore
                reference.document(key).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Delete!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).attachToRecyclerView(recyclerView);

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

    @Override
    public void onStart() {
        super.onStart();

        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.i("Main Activity", e.getMessage());;
                    return;
                }

                for(DocumentChange dc: queryDocumentSnapshots.getDocumentChanges()){
                    DocumentSnapshot documentSnapshot = dc.getDocument();

                    String id = documentSnapshot.getId();
                    int oldIndex = dc.getOldIndex();
                    int newIndex = dc.getNewIndex();

                    if(newIndex == -1){
                        // old document deleted
                        for(int i=0; i<passwordList.size(); i++){
                            if(id.equals(passwordList.get(i).getFirebaseKey())){
                                passwordList.remove(i);
                                adapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    } else if(oldIndex != -1){
                        // modification occurred
                        for(int i=0; i<passwordList.size(); i++){
                            if(id.equals(passwordList.get(i).getFirebaseKey())){
                                passwordList.set(i, documentSnapshot.toObject(PasswordData.class));
                                adapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }
            }
        });
    }
}
