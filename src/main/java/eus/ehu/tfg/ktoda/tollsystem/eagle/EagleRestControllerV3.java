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
@RequestMapping("api/v3/eagle")
@AllArgsConstructor
public class EagleRestControllerV3 {
    private final EagleService eagleService;

    @PostMapping("detection")
    public ResponseEntity<EagleAPIResponse> detectVehicle(@RequestBody EagleRequestV2 requestV2) {
        TransactionDTO transactionDTO = eagleService.detectVehicleV3(requestV2);
        if (transactionDTO == null)
            return ResponseEntity.ok(new EagleAPIResponse("Vehicle detected at ENTRY ", null));
        return ResponseEntity.ok(new EagleAPIResponse("Vehicle detected at EXIT ", transactionDTO));
    }
}
