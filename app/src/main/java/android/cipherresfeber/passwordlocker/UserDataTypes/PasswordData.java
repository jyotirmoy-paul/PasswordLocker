package android.cipherresfeber.passwordlocker.UserDataTypes;

public class PasswordData {

    private String serviceProvider;
    private String loginId;
    private String password;

    public PasswordData(String serviceProvider, String loginId, String password){
        this.serviceProvider = serviceProvider;
        this.loginId = loginId;
        this.password = password;
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

}
