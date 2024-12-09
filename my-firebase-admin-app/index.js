const admin = require('firebase-admin');

// Path to the service account key JSON file
const serviceAccount = require('C:/Users/USER/source/my-firebase-admin-app/adminsdkkey.json');

// Initialize the app with a service account, granting admin privileges
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

// You can now access Firestore, Authentication, Realtime Database, etc.
const db = admin.firestore();

// Example of accessing Firestore
db.collection('users').get()
  .then(snapshot => {
    snapshot.forEach(doc => {
      console.log(doc.id, '=>', doc.data());
    });
  })
  .catch(err => {
    console.error('Error getting documents', err);
  });
