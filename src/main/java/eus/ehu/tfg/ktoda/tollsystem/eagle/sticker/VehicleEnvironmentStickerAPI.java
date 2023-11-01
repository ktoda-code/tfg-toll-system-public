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

package eus.ehu.tfg.ktoda.tollsystem.eagle.sticker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
@Slf4j
public class VehicleEnvironmentStickerAPI {
    // Web Scraping from https://sede.dgt.gob.es/es/vehiculos/distintivo-ambiental/?accion=1&matriculahd=&matricula=3356FKC&submit=Consultar
    @Value("${python.eagle.scrapy.da.path}")
    private String spider;
    @Value("${python.env.path}")
    private String pythonPath;

    public String detectSticker(String licensePlate) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, spider, licensePlate);
        processBuilder.redirectErrorStream(true);  // Redirect error stream to the standard output for reading
        Process process = processBuilder.start();

        String desiredOutput = null;
        String currentLine;
        try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while ((currentLine = stdInput.readLine()) != null) {
                if (desiredOutput != null) {
                    desiredOutput = currentLine.trim();  // This captures the line after "Spider closed (finished)"
                    break;  // exit the loop once we get the desired output
                }

                if (currentLine.contains("Spider closed (finished)")) {
                    desiredOutput = "";  // Set it to an empty string to indicate the next line should be captured
                }
            }
        }
        log.info("Output from the Python script: {}", desiredOutput);
        return desiredOutput;
    }

}
