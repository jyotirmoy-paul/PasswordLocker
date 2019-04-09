package android.cipherresfeber.passwordlocker.UserDataTypes;

public class PasswordData {

    public String serviceProvider;
    public String loginId;
    public String password;
    public String firebaseKey;

    // no-argument constructor used by firebase
    public PasswordData(){

    }

    public PasswordData(String serviceProvider, String loginId, String password, String firebaseKey){
        this.serviceProvider = serviceProvider;
        this.loginId = loginId;
        this.password = password;
        this.firebaseKey = firebaseKey;
    }

    public String getServiceProvider(){
        return serviceProvider;
    }

    public String getLoginId(){
        return loginId;
    }

    public String getPassword(){
        return password;
    }

    public String getFirebaseKey(){
        return firebaseKey;
    }

}
