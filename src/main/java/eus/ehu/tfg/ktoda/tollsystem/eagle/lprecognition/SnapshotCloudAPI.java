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

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Optional;

/**
 * Third-party API for licence plate recognition
 *
 * @author Konstantin Andreev
 */
@Service
@Getter
public class SnapshotCloudAPI {
    // Get api key from https://app.platerecognizer.com/start/ and replace MY_API_KEY
    @Value("${snapshot.api.cloud.token}")
    private String token;

    /**
     * Reads all license plates from an image
     * Should use: "C:/Users/ktoda/dev/java/dev-projects/tfg-toll-system/toll-imgs/IMG-20230731-WA0006.jpg" full path
     *
     * @param file the license plates to be read
     * @return a http response with the json data
     */
    public String recognizeVehicle(File file, Optional<Date> timestamp) {
        HttpResponse<JsonNode> response = null;
        if (timestamp.isEmpty()) {
            response = Unirest.post("https://api.platerecognizer.com/v1/plate-reader/")
                    .header("Authorization", "Token " + token)
                    .field("upload", new File(file.getAbsolutePath()))
                    .asJson();
            // Can add timestamp and other fields
            // https://guides.platerecognizer.com/docs/snapshot/api-reference/#:~:text=from%20all%20origins.-,POST,-Parameters%E2%80%8B
        } else {
            Date date = timestamp.get();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            String formattedTimestamp = dateFormat.format(date);

            response = Unirest.post("https://api.platerecognizer.com/v1/plate-reader/")
                    .header("Authorization", "Token " + token)
                    .field("upload", new File(file.getAbsolutePath()))
                    .field("timestamp", formattedTimestamp)
                    .asJson();
        }
        System.out.println(response.getBody().toString());
        String licensePlateExtracted = returnLicensePlateValue(response.getBody());
        return licensePlateExtracted == null ? "null" : licensePlateExtracted.toUpperCase();
    }

    /**
     * Reads all license plates from an image
     *
     * @param file the license plates to be read
     * @return a http response with the json data
     */
    public String recognizeVehicleV2(String file, Optional<Date> timestamp) {
        HttpResponse<JsonNode> response = null;
        if (timestamp.isEmpty()) {
            response = Unirest.post("https://api.platerecognizer.com/v1/plate-reader/")
                    .header("Authorization", "Token " + token)
                    .field("upload", file)
                    .asJson();
        } else {
            Date date = timestamp.get();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            String formattedTimestamp = dateFormat.format(date);

            response = Unirest.post("https://api.platerecognizer.com/v1/plate-reader/")
                    .header("Authorization", "Token " + token)
                    .field("upload", file)
                    .field("timestamp", formattedTimestamp)
                    .asJson();
        }
        System.out.println(response.getBody().toString());
        String licensePlateExtracted = returnLicensePlateValue(response.getBody());
        return licensePlateExtracted == null ? "null" : licensePlateExtracted.toUpperCase();
    }

    /**
     * Extracts the license plate value from the JSON response body.
     *
     * @param body The JSON response body containing license plate information.
     * @return The extracted license plate value, or null if not found.
     */
    private String returnLicensePlateValue(JsonNode body) {
        if (body.getObject().has("results")) {
            JSONArray results = body.getObject().getJSONArray("results");

            // Assuming there can be multiple results, loop through them
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);

                // Check if the result has a "plate" field
                if (result.has("plate")) {
                    String plate = result.getString("plate");
                    System.out.println("License Plate: " + plate);
                    return plate;
                }
            }
        }

        // Return null if no license plate was found
        return null;
    }

    /**
     * Get number of recognition calls done during the current month.
     * This API endpoint is for Snapshot Cloud only.
     *
     * @return number of recognition calls done during the current month
     */
    public String statistics() {
        HttpResponse<String> response = Unirest.get("https://api.platerecognizer.com/v1/statistics/")
                .header("Authorization", "Token " + token)
                .asString();
        System.out.println("Usage:");
        System.out.println(response.getBody());
        return response.getBody();
    }

}
