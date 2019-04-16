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

import java.util.Random;

public class InitialLogoDisplayFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logo_display, container, false);

        ImageView imageViewDrawableDisplay = view.findViewById(R.id.imvDrawableDisplay);
        TextView textViewAppName = view.findViewById(R.id.txvAppName);
        TextView textViewTagLine = view.findViewById(R.id.txvTagLine);

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        imageViewDrawableDisplay.startAnimation(animation);
        textViewAppName.startAnimation(animation);
        textViewTagLine.startAnimation(animation);

        String[] tagLines = {
                "Focus on what you love, we will focus on how to secure them",
                "Adding a new password is as easy as 1,2,3",
                "Open Source Project, yes you can help us improve too",
                "Hold on, Unboxing Awesomeness",
                "Never lose a data, all passwords entries are synced",
                "Sit Back & Relax, let us handle those creepy Hackers",
                "Using our service will make you feel like on top of the world"
        };

        int[] drawableAssets = {
                R.drawable.hobby,
                R.drawable.add_password,
                R.drawable.open_source,
                R.drawable.unboxing,
                R.drawable.synchronized_passwords,
                R.drawable.my_passwords_five,
                R.drawable.about
        };

        // fill values to the views
        int index = (int) (Math.random()*tagLines.length);
        textViewTagLine.setText(tagLines[index]);
        imageViewDrawableDisplay.setImageResource(drawableAssets[index]);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left)
                        .remove(InitialLogoDisplayFragment.this).commit();
            }
        }, 2000); // exit fragment after 2 sec

        return view;
    }
}
