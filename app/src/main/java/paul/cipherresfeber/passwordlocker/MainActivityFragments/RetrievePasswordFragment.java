package paul.cipherresfeber.passwordlocker.MainActivityFragments;

import android.app.ProgressDialog;
import paul.cipherresfeber.passwordlocker.Adapters.PasswordAdapter;
import paul.cipherresfeber.passwordlocker.BottomSheetFragment.EditBottomSheetFragment;
import paul.cipherresfeber.passwordlocker.Constants.DatabaseConstants;

import paul.cipherresfeber.passwordlocker.R;
import paul.cipherresfeber.passwordlocker.UserDataTypes.PasswordData;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Random;

public class RetrievePasswordFragment extends Fragment {

    RecyclerView recyclerView;
    LinearLayout parentLayoutDataAvail;
    LinearLayout parentLayoutDataNotAvail;
    EditText editTextSearchPasswords;
    ImageView imageViewNoPasswordEntry;
    TextView textViewNoPasswordEntry;

    PasswordAdapter adapter;
    ArrayList<PasswordData> passwordList;

    CollectionReference reference;

    String userUid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_retreive_passwords, container, false);

        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseFirestore.getInstance()
                .collection(DatabaseConstants.DATABASE_PASSWORD_COLLECTION)
                .document(userUid)
                .collection(DatabaseConstants.DATABASE_USER_PASSWORD);

        // referencing to the views
        recyclerView = view.findViewById(R.id.recyclerView);
        editTextSearchPasswords = view.findViewById(R.id.etSearchPasswords);
        imageViewNoPasswordEntry = view.findViewById(R.id.imvNoPasswordEntry);
        textViewNoPasswordEntry = view.findViewById(R.id.txvNoPasswordEntry);

        int imageId[] = {
                R.drawable.my_passwords_one,
                R.drawable.my_passwords_two,
                R.drawable.my_passwords_three,
                R.drawable.my_passwords_four,
                R.drawable.my_passwords_five
        };

        String messageString[] = {
                "Waiting for something",
                "Feels a bit lonely",
                "Trying adding a password",
                "Passwords shown here",
                "Waiting for a miracle"
        };

        imageViewNoPasswordEntry.setImageResource(imageId[new Random().nextInt(imageId.length)]);
        textViewNoPasswordEntry.setText(messageString[new Random().nextInt(messageString.length)]);

        parentLayoutDataAvail = view.findViewById(R.id.parentLayoutDataAvail);
        parentLayoutDataNotAvail = view.findViewById(R.id.parentLayoutDataNotAvail);

        passwordList = new ArrayList<>();

        // get all the data for the first time
        reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot ds: queryDocumentSnapshots){
                    passwordList.add(ds.toObject(PasswordData.class));
                }
                adapter.notifyDataSetChanged();
                if(!passwordList.isEmpty()){
                    parentLayoutDataAvail.setVisibility(View.VISIBLE);
                    parentLayoutDataNotAvail.setVisibility(View.GONE);
                }
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
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {

                if(i == ItemTouchHelper.LEFT){
                    // left slide ---> ask the user for confirmation deletion of entry
                    new AlertDialog.Builder(getContext())
                            .setTitle("Delete Password Entry?")
                            .setCancelable(false)
                            .setMessage("Are you sure you want to delete this password entry?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    final ProgressDialog progressDialog = new ProgressDialog(getContext());
                                    progressDialog.setTitle("Please Wait");
                                    progressDialog.setMessage("Deleting Password Entry");
                                    progressDialog.setCancelable(false);
                                    progressDialog.setCanceledOnTouchOutside(false);
                                    progressDialog.show();

                                    String key = (String) viewHolder.itemView.getTag();
                                    reference.document(key).delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressDialog.cancel();
                                                    Toast.makeText(getContext(), "Password Removed", Toast.LENGTH_SHORT).show();
                                                    if(passwordList.isEmpty()){
                                                        parentLayoutDataAvail.setVisibility(View.GONE);
                                                        parentLayoutDataNotAvail.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(),
                                                    "Oops! Couldn't delete", Toast.LENGTH_SHORT).show();
                                            adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                            progressDialog.cancel();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // restore the removed password back to the list
                                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                }
                            })
                            .setIcon(R.drawable.ic_delete)
                            .show();


                } else{
                    // edit the entry ---> using an bottom sheet

                    EditBottomSheetFragment bottomSheet = new EditBottomSheetFragment();

                    String key = (String) viewHolder.itemView.getTag();
                    // search for the key in the list
                    for(PasswordData pd: passwordList){
                        String tempKey = pd.getFirebaseKey();
                        if(key.equals(tempKey)){

                            Bundle bundle = new Bundle();
                            bundle.putString(DatabaseConstants.SERVICE_PROVIDER_NAME, pd.getServiceProvider());
                            bundle.putString(DatabaseConstants.LOGIN_ID, pd.getLoginId());
                            bundle.putString(DatabaseConstants.DATABASE_KEY, pd.getFirebaseKey());

                            bottomSheet.setArguments(bundle);
                            bottomSheet.show(getFragmentManager(), "EditBottomSheetDialog");

                        }
                    }

                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                }

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
