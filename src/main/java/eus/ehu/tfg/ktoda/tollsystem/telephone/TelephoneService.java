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

package eus.ehu.tfg.ktoda.tollsystem.telephone;

import eus.ehu.tfg.ktoda.tollsystem.telephone.dto.TelephoneDTO;
import eus.ehu.tfg.ktoda.tollsystem.user.User;

import java.util.List;

public interface TelephoneService {
    /**
     * Retrieves a list of all telephone entries.
     *
     * @return A list of TelephoneDTO objects.
     */
    List<TelephoneDTO> findAll();

    /**
     * Retrieves a Telephone entry.
     *
     * @param telephoneDTO The TelephoneDTO entry to retrieve.
     * @return The Telephone object if found, or null if not found.
     */
    Telephone findByDTO(TelephoneDTO telephoneDTO);

    /**
     * Retrieves a telephone entry by its unique ID.
     *
     * @param id The ID of the telephone entry to retrieve.
     * @return The TelephoneDTO object if found, or null if not found.
     */
    TelephoneDTO findById(Long id);

    /**
     * Removes a telephone entry by its unique ID.
     *
     * @param id The ID of the telephone entry to remove.
     * @return true if the telephone entry was successfully removed, false if not found.
     */
    boolean removeById(Long id);

    /**
     * Saves a new telephone entry.
     *
     * @param telephone The Telephone object to save.
     */
    void save(Telephone telephone);

    /**
     * Saves a new telephone entry based on a telephone request.
     *
     * @param telephoneRequest The TelephoneRequest object containing telephone details to save.
     * @return The saved TelephoneDTO object.
     */
    TelephoneDTO save(TelephoneRequest telephoneRequest);

    /**
     * Update a telephone entry.
     *
     * @param id               The ID of the telephone entry to update.
     * @param telephoneRequest The Telephone request object to update.
     * @return The updated TelephoneDTO object.
     */
    TelephoneDTO update(Long id, TelephoneRequest telephoneRequest);

    /**
     * Assign a user to a telephone entry.
     *
     * @param telephone The Telephone entry to which the User should be assigned.
     * @param user      The User to assign.
     * @return The updated TelephoneDTO object.
     */
    TelephoneDTO assignUser(Telephone telephone, User user);


}
