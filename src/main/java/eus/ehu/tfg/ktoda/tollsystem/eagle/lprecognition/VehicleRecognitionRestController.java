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

import eus.ehu.tfg.ktoda.tollsystem.eagle.lprecognition.SnapshotCloudAPI;
import lombok.AllArgsConstructor;
import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Optional;

/**
 * Wrapper REST api for license plate recognition endpoints.
 *
 * @author Konstantin Andreev
 * @apiNote Feature migration to own implementation of license plate recognition
 */
@RestController
@RequestMapping("/api/v1/toll-system")
@AllArgsConstructor
@Deprecated
public class VehicleRecognitionRestController {
    private final SnapshotCloudAPI snapshotCloudAPI;
    // Feature impl
    //private final LicencePlateRecognitionWrapperAPI plateRecognitionWrapperAPI;
    //private final VehicleEnvironmentStickerAPI environmentStickerAPI;

    @PostMapping("/recognize")
    public ResponseEntity<?> recognize(@RequestBody String file_uri) {
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
//        return ResponseEntity.ok(snapshotCloudAPI.recognizeVehicle(
//                new File(file_uri
//                        .replaceAll("\"", "")),
//                Optional.empty()));
////                Optional.of(new Date(System.currentTimeMillis()))));
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> statistics() {
        return ResponseEntity.ok(snapshotCloudAPI.statistics());
    }
}
