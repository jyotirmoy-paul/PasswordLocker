package android.cipherresfeber.passwordlocker.AuthenticationActivityFragments;

import android.cipherresfeber.passwordlocker.R;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class InitialLogoDisplayFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logo_display, container, false);

        ImageView imageViewLogoDisplay = view.findViewById(R.id.imvLogoDisplay);
        TextView textViewAppName = view.findViewById(R.id.txvAppName);
        TextView textViewTagLine = view.findViewById(R.id.txvTagLine);

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);

        imageViewLogoDisplay.startAnimation(animation);
        textViewAppName.startAnimation(animation);
        textViewTagLine.startAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left)
                        .remove(InitialLogoDisplayFragment.this).commit();
            }
        }, 1500); // exit fragment after 1.5 sec

        return view;
    }
}
