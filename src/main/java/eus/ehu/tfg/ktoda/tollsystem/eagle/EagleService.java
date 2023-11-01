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

public interface EagleService {
    /**
     * Processes a vehicle detection event.
     *
     * @param request The EagleRequest containing the detection point ID and image data.
     * @return A TransactionDTO for the created transaction, or null if no transaction is required.
     */
    TransactionDTO detectVehicle(EagleRequest request);

    /**
     * Processes a vehicle detection event. V2
     *
     * @param requestV2 The EagleRequest containing the detection point ID and image data.
     * @return A TransactionDTO for the created transaction, or null if no transaction is required.
     */
    TransactionDTO detectVehicleV2(EagleRequestV2 requestV2);

    /**
     * Processes a vehicle detection event. V3
     *
     * @param requestV2 The EagleRequest containing the detection point ID and image data.
     * @return A TransactionDTO for the created transaction, or null if no transaction is required.
     */
    TransactionDTO detectVehicleV3(EagleRequestV2 requestV2);

}
