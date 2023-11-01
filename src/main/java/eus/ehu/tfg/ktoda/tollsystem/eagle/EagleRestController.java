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

package eus.ehu.tfg.ktoda.tollsystem.eagle;

import eus.ehu.tfg.ktoda.tollsystem.transaction.dto.TransactionDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;

@RestController
@RequestMapping("api/v1/eagle")
@AllArgsConstructor
public class EagleRestController {
    private final EagleService eagleService;

    @PostMapping("detection/{pointId}")
    @Deprecated
    public ResponseEntity<EagleAPIResponse> detectVehicle(
            @PathVariable("pointId") Long pointId,
            @RequestBody String file_uri) {
        EagleRequest eagleRequest = new EagleRequest(pointId, file_uri, new Date(System.currentTimeMillis()));
        TransactionDTO transactionDTO = eagleService.detectVehicle(eagleRequest);
        return ResponseEntity.ok(new EagleAPIResponse("", transactionDTO));
    }

    @PostMapping("detection")
    public ResponseEntity<EagleAPIResponse> detectVehicle(@RequestBody EagleRequest request) {
        TransactionDTO transactionDTO = eagleService.detectVehicle(request);
        if (transactionDTO == null)
            return ResponseEntity.ok(new EagleAPIResponse("Vehicle detected at ENTRY ", null));
        return ResponseEntity.ok(new EagleAPIResponse("Vehicle detected at EXIT ", transactionDTO));
    }
}
