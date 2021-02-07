# Password Locker
Locker for storing passwords securely (using AES Algorithm) locally as well as on cloud.

## Web app avaiable [here](https://plocker.web.app/#/)


## App Screenshots
<p align="center">
  <img align="left" src="https://github.com/jyotirmoy-paul/PasswordLocker/blob/master/samples/screenshot1.png" width=250>
  <img src="https://github.com/jyotirmoy-paul/PasswordLocker/blob/master/samples/screenshot2.png" width=250>
  <img align="right" src="https://github.com/jyotirmoy-paul/PasswordLocker/blob/master/samples/screenshot3.png" width=250>
</p>

## Getting Started
The following instuctions will get you a copy of the project up and running on your local machine for development and testing purpose.

### Prerequisites
What things you need to install the software
```
1. Latest version of android studio should be set-up and running on your system.
2. Add Firebase to your project. (https://firebase.google.com/docs/android/setup)
3. And finally, an android device (or emulator) running on Jelly Bean or higher.
```
### Setting up Firebase - You have to add your own 'google-services.json' file
By now, you would have already downloaded the "google-services.json" file and connected your app to Firebase Server. Finally, to set the server side Authentication, Realtime Database and Storage provided by Google Firebase, follow the steps below:
1. Check for the following dependencies in app-level gradle file
     ```
    implementation 'com.google.firebase:firebase-core:16.0.8'
    implementation 'com.google.firebase:firebase-auth:16.2.1'
    implementation 'com.google.firebase:firebase-firestore:18.2.0'
    implementation 'com.google.firebase:firebase-database:16.1.0'
    ```
2. Add the following database rules, in Firebase console:
```
{
  "rules": {
    
    // nobody can read/write the developer_master_key (for security purpose)
    "developer_master_key": {
      ".write": false,
        ".read": false
    },
      
    // don't allow anyone to read from user_data section
    // write is only possible if user is authenticated
    "user_data": {
      ".write": "auth != null",
      ".read": false
    }
      
  }
}
```
3. Add the following cloud firestore rules, in Firebase console:
```
service cloud.firestore {
  match /databases/{database}/documents {
  	// allow a user to modify data only under his document
    match /password_collections/{user_uid}/user_passwords/{password_id} {
      allow read, write, delete, update: if request.auth != null && request.auth.uid == user_uid;
    }
  }
}
```
4. For Authentication, "Phone" in Firebase should be enabled and also add your SHA1 certificate to firebase
### Installing the App
Import the app to Android Studio, build the project and finally deploy it in a device (or emulator).

## Main Highlights:
1. Encrypted Password Storage
2. Inbuilt App Lock
3. Clean and Easy to use UI
4. Open Source Project

##  Build With
* [Google Firebase](https://firebase.google.com/)

## Authors
* **Jyotirmoy Paul** - Initial work - [jyotirmoy-paul](https://github.com/jyotirmoy-paul)
