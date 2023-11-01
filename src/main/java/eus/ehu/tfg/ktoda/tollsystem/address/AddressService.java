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

package eus.ehu.tfg.ktoda.tollsystem.address;

import eus.ehu.tfg.ktoda.tollsystem.address.dto.AddressDTO;
import eus.ehu.tfg.ktoda.tollsystem.user.User;

import java.util.List;

public interface AddressService {
    /**
     * Retrieves a list of all address entries.
     *
     * @return A list of AddressDTO objects.
     */
    List<AddressDTO> findAll();

    /**
     * Retrieves an Address.
     *
     * @param addressDTO The Address to retrieve.
     * @return The AddressDTO object if found, or null if not found.
     */
    Address findByDTO(AddressDTO addressDTO);

    /**
     * Retrieves an address by its unique ID.
     *
     * @param id The ID of the address to retrieve.
     * @return The AddressDTO object if found, or null if not found.
     */
    AddressDTO findById(Long id);

    /**
     * Removes an address by its unique ID.
     *
     * @param id The ID of the address to remove.
     * @return true if the address was successfully removed, false if not found.
     */
    boolean removeById(Long id);

    /**
     * Saves a new address entry.
     *
     * @param address The Address object to save.
     */
    void save(Address address);

    /**
     * Saves a new address entry based on an address request.
     *
     * @param addressRequest The AddressRequest object containing address details to save.
     * @return The saved AddressDTO object.
     */
    AddressDTO save(AddressRequest addressRequest);

    /**
     * Update an address.
     *
     * @param id             The ID of the address to update.
     * @param addressRequest The Address request object to update.
     * @return The updated AddressDTO object.
     */
    AddressDTO update(Long id, AddressRequest addressRequest);

    /**
     * Assign Address to a User.
     *
     * @param address The Address to which the User should be assigned.
     * @param user    The User to assign.
     * @return The updated UserDTO object.
     */
    AddressDTO assignUser(Address address, User user);

}
