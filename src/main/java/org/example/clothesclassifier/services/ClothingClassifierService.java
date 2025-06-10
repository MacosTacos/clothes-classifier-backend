package org.example.clothesclassifier.services;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtSession.Result;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Service
public class ClothingClassifierService {
    private static final String[] CLASS_NAMES = {
            "blouse", "boots", "dress", "hats", "pants",
            "shorts", "skirts", "sneakers", "tshirts", "winterJacket"
    };

    private final OrtEnvironment env;
    private final OrtSession session;

    public ClothingClassifierService() {
        try {
            env = OrtEnvironment.getEnvironment();
            OrtSession.SessionOptions options = new OrtSession.SessionOptions();
            try (InputStream modelStream = getClass().getClassLoader().getResourceAsStream("clothes_classifier_v4.onnx")) {
                if (modelStream == null) {
                    throw new FileNotFoundException("ONNX model not found in resources");
                }
                byte[] modelBytes = modelStream.readAllBytes();
                session = env.createSession(modelBytes, options);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to load ONNX model", e);
            }

        } catch (OrtException e) {
            throw new IllegalStateException("Failed to load ONNX model for clothing classification", e);
        }
    }

    public String classifyImage(InputStream imageFile) {
        try {
            BufferedImage original = ImageIO.read(imageFile);
            if (original == null) {
                throw new IOException("Unable to read image file for classification");
            }
            int targetWidth = 224, targetHeight = 224;
            BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resized.createGraphics();
            g.drawImage(original, 0, 0, targetWidth, targetHeight, null);
            g.dispose();

            int H = targetHeight;
            int W = targetWidth;
            float[][][][] inputData = new float[1][H][W][3];

            for (int i = 0; i < H; i++) {
                for (int j = 0; j < W; j++) {
                    int rgb = resized.getRGB(j, i);
                    int r = (rgb >> 16) & 0xFF;
                    int ge = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;

                    inputData[0][i][j][0] = r / 255.0f;
                    inputData[0][i][j][1] = ge / 255.0f;
                    inputData[0][i][j][2] = b / 255.0f;
                }
            }

            String inputName = session.getInputNames().iterator().next();
            try (OnnxTensor inputTensor = OnnxTensor.createTensor(env, inputData);
                 Result results = session.run(Map.of(inputName, inputTensor))) {
                float[][] output = (float[][]) results.get(0).getValue();
                float[] probabilities = output[0];
                int classIndex = 0;
                for (int k = 1; k < probabilities.length; k++) {
                    if (probabilities[k] > probabilities[classIndex]) {
                        classIndex = k;
                    }
                }
                return CLASS_NAMES[classIndex];
            }
        } catch (OrtException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error during image classification", e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        session.close();
        env.close();
        super.finalize();
    }
}