/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const {onRequest} = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

const functions = require('firebase-functions');
const admin = require('firebase-admin');
const { ImageAnnotatorClient } = require('@google-cloud/vision');
const visionClient = new ImageAnnotatorClient();

// Initialize Firebase Admin SDK (optional if interacting with FireStore)
admin.initializeApp();

// Define the Firebase Function
exports.detectTextInImage = functions.https.onRequest(async (req, res) => {
    try {
        // Ensure that the request method is POST (you'll send a POST request from your app)
        if (req.method !== 'POST') {
            return res.status(405).send('Only POST method is allowed');
        }

        // Parse image data from the request body
        const image = req.body.image; // The image data passed from the app

        if (!image) {
            return res.status(400).send('No image provided');
        }

        // Use the Vision API to analyze the image
        const [result] = await visionClient.textDetection(image);
        const detections = result.textAnnotations;

        if (detections.length === 0) {
            return res.status(404).send('No text detected');
        }

        // Send the detected text back as a response
        return res.status(200).json({
            text: detections[0].description
        });

    } catch (error) {
        console.error('Error in detecting text:', error);
        return res.status(500).send('Internal server error');
    }
});
