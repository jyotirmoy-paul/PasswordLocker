package android.cipherresfeber.passwordlocker;

import android.cipherresfeber.passwordlocker.Constants.UserConstants;
import android.cipherresfeber.passwordlocker.MainActivityFragments.AboutFragment;
import android.cipherresfeber.passwordlocker.MainActivityFragments.AddPasswordFragment;
import android.cipherresfeber.passwordlocker.MainActivityFragments.EditProfileFragment;
import android.cipherresfeber.passwordlocker.MainActivityFragments.ProfileDisplayFragment;
import android.cipherresfeber.passwordlocker.MainActivityFragments.RetrievePasswordFragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.auth.User;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, EditProfileFragment.OnFragmentInteractionListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences(UserConstants.SHARED_PREFERENCE_NAME,
                MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new RetrievePasswordFragment()).commit();
            getSupportActionBar().setTitle("My Passwords");
            navigationView.setCheckedItem(R.id.navMyPasswords);
        }

        // set the user name, profile picture and contact detail
        View headerView = navigationView.getHeaderView(0);
        TextView textViewUserProfilePicDisplay = headerView.findViewById(R.id.txvUserProfilePicDisplay);
        TextView textViewUserNameDisplay = headerView.findViewById(R.id.txvUserNameDisplay);
        TextView textViewUserPhoneNumberDisplay = headerView.findViewById(R.id.txvUserPhoneNumberDisplay);

        textViewUserProfilePicDisplay.setText(preferences.getString(UserConstants.USER_NAME,
                "Default User").substring(0,1));
        textViewUserNameDisplay.setText(preferences.getString(UserConstants.USER_NAME,
                "Default User"));
        textViewUserPhoneNumberDisplay.setText("+91 " + preferences.getString(UserConstants.USER_PHONE_NUMBER,
                "9998887654"));

    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.navMyPasswords:
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in_frag, R.anim.fade_out_frag)
                        .replace(R.id.fragment_container, new RetrievePasswordFragment()).commit();
                getSupportActionBar().setTitle("My Passwords");
                break;
            case R.id.navAddPassword:
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in_frag, R.anim.fade_out_frag)
                        .replace(R.id.fragment_container, new AddPasswordFragment()).commit();
                getSupportActionBar().setTitle("Add Password");
                break;
            case R.id.navProfile:
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in_frag, R.anim.fade_out_frag)
                        .replace(R.id.fragment_container, new ProfileDisplayFragment()).commit();
                getSupportActionBar().setTitle("Profile");
                break;
            case R.id.navAbout:
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in_frag, R.anim.fade_out_frag)
                        .replace(R.id.fragment_container, new AboutFragment()).commit();
                getSupportActionBar().setTitle("About");
                break;
            case R.id.navRate:
                Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                }
                catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
                }
                break;
            case R.id.navShare:
                // let the user share the app
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Password Locker - Safely store all your online passwords");
                    String sAux = "\nPassword Locker stores passwords on cloud using AES Algorithm. You bet, it's secure. " +
                            "Download Now:\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=" + this.getPackageName();
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "Share using"));
                } catch(Exception e) {
                    Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    Log.i("MainActivity", e.getMessage());
                }
                break;
            case R.id.navEmail:
                // call an intent to Gmail app
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "cipherresfeber@gmail.com"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Password Locker Feedback");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Chooser Title"));
                break;
            case R.id.navGithub:
                Intent viewOnGithub = new Intent(Intent.ACTION_VIEW);
                viewOnGithub.setData(Uri.parse("https://github.com/jyotirmoy-paul/PasswordLocker"));
                startActivity(viewOnGithub);
                break;
            case R.id.navPrivacyPolicy:
                startActivity(
                        new Intent(Intent.ACTION_VIEW)
                                .setData(Uri.parse("https://fir-credebit.firebaseapp.com/"))
                );
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
