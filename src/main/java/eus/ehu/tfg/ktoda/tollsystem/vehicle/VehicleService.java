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

package eus.ehu.tfg.ktoda.tollsystem.vehicle;


import eus.ehu.tfg.ktoda.tollsystem.user.User;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.dto.VehicleDTO;

import java.util.List;

public interface VehicleService {
    /**
     * Retrieves a list of all vehicles.
     *
     * @return A list of VehicleDTO objects.
     */
    List<VehicleDTO> findAll();

    /**
     * Retrieves a Vehicle by its unique ID.
     *
     * @param vehicleDTO The VehicleDTO to retrieve.
     * @return The Vehicle object if found, or null if not found.
     */
    Vehicle findByDTO(VehicleDTO vehicleDTO);

    /**
     * Retrieves a vehicle by its unique ID.
     *
     * @param id The ID of the vehicle to retrieve.
     * @return The VehicleDTO object if found, or null if not found.
     */
    VehicleDTO findById(String id);

    /**
     * Removes a vehicle by its unique ID.
     *
     * @param id The ID of the vehicle to remove.
     * @return true if the vehicle was successfully removed, false if not found.
     */
    boolean removeById(String id);

    /**
     * Saves a new vehicle.
     *
     * @param vehicle The Vehicle object to save.
     */
    void save(Vehicle vehicle);

    /**
     * Saves a new vehicle based on a vehicle request.
     *
     * @param vehicleRequest The VehicleRequest object containing vehicle details to save.
     * @return The saved VehicleDTO object.
     */
    VehicleDTO save(VehicleRequest vehicleRequest);

    /**
     * Update a vehicle.
     *
     * @param id             The ID of the vehicle to update.
     * @param vehicleRequest The Vehicle request object to update.
     * @return The updated VehicleDTO object.
     */
    VehicleDTO update(String id, VehicleRequest vehicleRequest);

    /**
     * Assign a user to a vehicle.
     *
     * @param vehicle The Vehicle to which the User should be assigned.
     * @param user    The User to assign.
     * @return The updated VehicleDTO object.
     */
    VehicleDTO assignUser(Vehicle vehicle, User user);

    Vehicle findByLP(String licensePlate);
}
