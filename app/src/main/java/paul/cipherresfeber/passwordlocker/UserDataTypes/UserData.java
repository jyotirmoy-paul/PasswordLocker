package paul.cipherresfeber.passwordlocker.UserDataTypes;

public class UserData {

    public String userName;
    public String userPhone;
    public String uid;
    public String accountCreationDate;

    public UserData(){
        // empty constructor needed for firebase
    }

    public UserData(String userName, String userPhone, String uid, String accountCreationDate){
        this.userName = userName;
        this.userPhone = userPhone;
        this.uid = uid;
        this.accountCreationDate = accountCreationDate;
    }

}
