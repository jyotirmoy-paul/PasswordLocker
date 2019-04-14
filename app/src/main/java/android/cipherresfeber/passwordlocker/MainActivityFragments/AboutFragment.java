package android.cipherresfeber.passwordlocker.MainActivityFragments;

import android.cipherresfeber.passwordlocker.R;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        // reference to the views
        TextView textViewWhyPasswordLocker = view.findViewById(R.id.txvWhyPassworkLocker);

        String whyPasswordLocker =
                        "In one line, to give Hackers a hard time!\n" +
                        "\n" +
                        "It is quite surprising that almost everyone uses only a single password for all their online accounts. In case, if a hacker finds out any one of your passwords, you risk granting him/her full access of your entire online world! On the other hand, if you try using separate passwords for every different account you have, it would be a nightmare trying to remember all of them. \n" +
                        "\n" +
                        "Here is where Password Locker can help you manage all your passwords, storing them locally as well as on cloud in an encrypted format, such that only you could decrypt them.\n" +
                        "\n" +
                        "Main Highlights:\n" +
                        "\t1. Encrypted Password Storage\n" +
                        "\t2. Inbuilt App Locker PIN\n" +
                        "\t3. Clean and Easy to use UI\n" +
                        "\t4. Open Sourced Project";

        textViewWhyPasswordLocker.setText(whyPasswordLocker);

        return view;
    }
}
