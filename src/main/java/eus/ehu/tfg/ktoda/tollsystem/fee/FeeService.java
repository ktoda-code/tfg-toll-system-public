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

package eus.ehu.tfg.ktoda.tollsystem.fee;

import eus.ehu.tfg.ktoda.tollsystem.fee.dto.FeeDTO;
import eus.ehu.tfg.ktoda.tollsystem.geoLocation.GeoLocation;
import eus.ehu.tfg.ktoda.tollsystem.geoLocation.dto.GeoLocationDTO;
import eus.ehu.tfg.ktoda.tollsystem.section.Section;

import java.util.List;

public interface FeeService {
    /**
     * Retrieves a list of all Fees.
     *
     * @return A list of FeeDTO objects.
     */
    List<FeeDTO> findAll();

    Fee findFeeById(Long feeId);

    /**
     * Retrieves a Fee by its unique ID.
     *
     * @param feeDTO The FeeDTO to retrieve.
     * @return The Fee object if found, or null if not found.
     */
    Fee findByDTO(FeeDTO feeDTO);

    /**
     * Retrieves a Fee by its unique ID.
     *
     * @param id The ID of the Fee to retrieve.
     * @return The FeeDTO object if found, or null if not found.
     */
    FeeDTO findById(long id);

    /**
     * Removes a Fee by its unique ID.
     *
     * @param id The ID of the Fee to remove.
     * @return true if the Fee was successfully removed, false if not found.
     */
    boolean removeById(long id);

    /**
     * Saves a new Fee.
     *
     * @param fee The Fee object to save.
     */
    void save(Fee fee);

    /**
     * Saves a new Fee.
     *
     * @param feeRequest The Fee object to save.
     * @return The saved FeeDTO object.
     */
    FeeDTO save(FeeRequest feeRequest);

    /**
     * Update a Fee.
     *
     * @param id         The ID of the Fee to update.
     * @param feeRequest The Fee request object to update.
     * @return The updated FeeDTO object.
     */
    FeeDTO update(long id, FeeRequest feeRequest);

    /**
     * Assign a Section to a Fee.
     *
     * @param fee     The Fee to which the Section should be assigned.
     * @param section The Section to assign.
     * @return The updated FeeDTO object.
     */
    FeeDTO assignSection(Fee fee, Section section);

    /**
     * Retrieves a list of all Fees of the specific section.
     *
     * @return A list of Fee objects.
     */
    List<Fee> findFeesBySectionId(String sectionId);


}
