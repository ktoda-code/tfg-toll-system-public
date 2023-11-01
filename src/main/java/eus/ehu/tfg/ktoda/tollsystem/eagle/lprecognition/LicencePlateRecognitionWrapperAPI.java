/*
 * Copyright (c) Toll System Prototype, KONSTANTIN TODOROV ANDREEV 2023.
 *
 * Licensed under the Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://creativecommons.org/licenses/by-nc-nd/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package eus.ehu.tfg.ktoda.tollsystem.eagle.lprecognition;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Base64;

/**
 * Wrapper class to connect with the python dedicated license plate recognition algorithm
 *
 * @author Konstantin Andreev
 */
@Service
@Slf4j
public class LicencePlateRecognitionWrapperAPI {
    @Value("${python.env.path}")
    private String pythonPath;
    @Value("${python.eagle.lpr.path}")
    private String lprV3;


    public String licensePlateRecognizer(String base64Img) {
        if (base64Img == null || base64Img.isEmpty()) {
            log.warn("Received an empty or null base64 image for license plate recognition.");
            throw new IllegalArgumentException("Base64 Image string cannot be null or empty.");
        }

        StringBuilder outputBuilder = new StringBuilder();
        StringBuilder errorBuilder = new StringBuilder();

        File tempFile = null;
        try {
            tempFile = File.createTempFile("base64Image", ".jpg");
            byte[] imgBytes = Base64.getDecoder().decode(base64Img);
            Files.write(tempFile.toPath(), imgBytes);

            log.info(tempFile.getAbsolutePath());

            ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, lprV3, tempFile.getAbsolutePath());
            processBuilder.redirectErrorStream(false);
            Process process = processBuilder.start();

            try (BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = outputReader.readLine()) != null) {
                    outputBuilder.append(line).append("\n");
                }
                while ((line = errorReader.readLine()) != null) {
                    errorBuilder.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("Python script execution failed with exit code {}. Error: {}", exitCode, errorBuilder);
                throw new RuntimeException("Python script execution failed. Error: " + errorBuilder);
            }

        } catch (Exception e) {
            log.error("Error executing license plate recognition.", e);
            throw new RuntimeException("Error executing license plate recognition.", e);
        } finally {
            // Clean up the temporary file
            if (tempFile != null) {
                tempFile.delete();
            }
        }

        String result = outputBuilder.toString().trim();
        log.info("License plate recognition result: {}", result);
        return result.isEmpty() ? null : result;
    }
}

