package paul.cipherresfeber.passwordlocker.MainActivityFragments;

import paul.cipherresfeber.passwordlocker.Constants.UserConstants;
import paul.cipherresfeber.passwordlocker.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProfileDisplayFragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_display, container, false);

        TextView textViewUserName = view.findViewById(R.id.txvUserName);
        TextView textViewUserPhoneNumber = view.findViewById(R.id.txvUserPhoneNumber);

        FloatingActionButton floatingActionButtonEditProfile = view.findViewById(R.id.fabEditProfile);

        SharedPreferences preferences = getContext().getSharedPreferences(
                UserConstants.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE
        );

        final String name = preferences.getString(UserConstants.USER_NAME, "Default User");
        String phone = preferences.getString(UserConstants.USER_PHONE_NUMBER, "9998887776");

        textViewUserName.setText(name);
        textViewUserPhoneNumber.setText("+91" + " " + phone);

        floatingActionButtonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileFragment fragment = EditProfileFragment.newInstance(name);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                        R.anim.enter_from_right, R.anim.exit_to_right);
                transaction.addToBackStack(null);
                transaction.add(R.id.fragmentContainer, fragment, "EditProfileFragment").commit();
            }
        });

        return view;
    }

}
