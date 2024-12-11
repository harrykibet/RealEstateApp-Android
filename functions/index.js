const functions = require("firebase-functions/v2/https");
const admin = require("firebase-admin");
const {ImageAnnotatorClient} = require("@google-cloud/vision");

const visionClient = new ImageAnnotatorClient();
admin.initializeApp();

exports.detectTextInImage = functions.onRequest(async (req, res) => {
  try {
    if (req.method !== "POST") {
      return res.status(405).send("Only POST method is allowed");
    }

    const image = req.body.image;
    if (!image) {
      return res.status(400).send("No image provided");
    }

    const [result] = await visionClient.textDetection(image);
    const detections = result.textAnnotations;

    if (detections.length === 0) {
      return res.status(404).send("No text detected");
    }

    return res.status(200).json({
      text: detections[0].description,
    });
  } catch (error) {
    console.error("Error in detecting text:", error);
    return res.status(500).send("Internal server error");
  }
});
