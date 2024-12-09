const admin = require('firebase-admin');
const serviceAccount = require('./adminsdkkey.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

// Function to assign admin role
const setAdminRole = async (uid) => {
  try {
    await admin.auth().setCustomUserClaims(uid, { admin: true });
    console.log(`Admin role assigned to user: ${uid}`);
  } catch (error) {
    console.error('Error setting custom claims:', error);
  }
};

// Replace 'USER_UID' with the UID of the user you want to make admin
setAdminRole('AUCfA6jDYifehAcqoUwVRwHo7yb2');
