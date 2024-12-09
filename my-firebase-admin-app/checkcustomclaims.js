const admin = require('firebase-admin');
const serviceAccount = require('./adminsdkkey.json');

// Initialize Firebase Admin SDK
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

// Function to get user custom claims
const checkAdminClaim = async (uid) => {
  try {
    const user = await admin.auth().getUser(uid);
    console.log(user.customClaims);  // Prints the custom claims, like {admin: true}
  } catch (error) {
    console.error('Error fetching user data:', error);
  }
};

// Call the function with the user UID
checkAdminClaim('AUCfA6jDYifehAcqoUwVRwHo7yb2');
