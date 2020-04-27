import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

OLD_UID = u''
NEW_UID = u''

# Use a service account
cred = credentials.Certificate('service_key.json')
firebase_admin.initialize_app(cred)

db = firestore.client()

old_ref = db.collection(u'password_collections').document(OLD_UID).collection(u'user_passwords')
old_pass = old_ref.stream()
entities = []
for p in old_pass:
    entities.append(p.to_dict())

new_ref = db.collection(u'password_collections').document(NEW_UID).collection(u'user_passwords')
for e in entities:
    new_ref.document(e['firebaseKey']).set(e)
