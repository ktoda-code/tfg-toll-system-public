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

package eus.ehu.tfg.ktoda.tollsystem.user;

import eus.ehu.tfg.ktoda.tollsystem.address.Address;
import eus.ehu.tfg.ktoda.tollsystem.pointlog.PointLog;
import eus.ehu.tfg.ktoda.tollsystem.telephone.Telephone;
import eus.ehu.tfg.ktoda.tollsystem.transaction.Transaction;
import eus.ehu.tfg.ktoda.tollsystem.user.dto.UserProfileUpdateDTO;
import eus.ehu.tfg.ktoda.tollsystem.user.dto.UserSimpleDTO;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.Vehicle;
import jakarta.validation.constraints.Email;

import java.util.List;
import java.util.UUID;

public interface UserService {
    /**
     * Retrieves a list of all users.
     *
     * @return A list of UserDTO objects.
     */
    List<UserSimpleDTO> findAll();

    /**
     * Retrieves a User.
     *
     * @param userDTO The UserDTO to retrieve.
     * @return The User object if found, or null if not found.
     */
    User findByDTO(UserSimpleDTO userDTO);

    /**
     * Retrieves a user by their unique ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The UserDTO object if found, or null if not found.
     */
    UserSimpleDTO findById(UUID id);

    /**
     * Retrieves a user by their unique email.
     *
     * @param email The email of the user to retrieve.
     * @return The UserDTO object if found, or null if not found.
     */
    UserSimpleDTO findByEmail(@Email String email);

    /**
     * Removes a user by their unique ID.
     *
     * @param id The ID of the user to remove.
     * @return true if the user was successfully removed, false if not found.
     */
    boolean removeById(UUID id);

    /**
     * Saves a new user.
     *
     * @param user The User object to save.
     */
    void save(User user);

    void register(User user);

    /**
     * Saves a new user.
     *
     * @param userRequest The User object to save.
     * @return The saved UserDTO object.
     */
    UserSimpleDTO save(UserRequest userRequest);

    /**
     * Update a user.
     *
     * @param id          The ID of the user to update.
     * @param userRequest The User request object to update.
     * @return The updated UserDTO object.
     */
    UserSimpleDTO update(UUID id, UserRequest userRequest);

    /**
     * Assign Address to a User
     *
     * @param user    The User to which the Address should be assigned.
     * @param address The Address to assign.
     * @return The updated UserDTO object.
     */
    UserSimpleDTO assignAddress(User user, Address address);

    /**
     * Add a vehicle to a user.
     *
     * @param user    The User to which the Vehicle should be added.
     * @param vehicle The Vehicle to add.
     * @return The updated UserDTO object.
     */
    UserSimpleDTO addVehicle(User user, Vehicle vehicle);

    /**
     * Remove a vehicle from a user.
     *
     * @param user    The User from which the Vehicle should be removed.
     * @param vehicle The Vehicle to remove.
     * @return The updated UserDTO object.
     */
    UserSimpleDTO removeVehicle(User user, Vehicle vehicle);

    /**
     * Add a telephone to a user.
     *
     * @param user      The User to which the Telephone should be added.
     * @param telephone The Telephone to add.
     * @return The updated UserDTO object.
     */
    UserSimpleDTO addTelephone(User user, Telephone telephone);

    /**
     * Remove a telephone from a user.
     *
     * @param user      The User from which the Telephone should be removed.
     * @param telephone The Telephone to remove.
     * @return The updated UserDTO object.
     */
    UserSimpleDTO removeTelephone(User user, Telephone telephone);

    /**
     * Add a transaction to a user.
     *
     * @param user        The User to which the Transaction should be added.
     * @param transaction The Transaction to add.
     * @return The updated UserDTO object.
     */
    UserSimpleDTO addTransaction(User user, Transaction transaction);

    /**
     * Remove a transaction from a user.
     *
     * @param user        The User from which the Transaction should be removed.
     * @param transaction The Transaction to remove.
     * @return The updated UserDTO object.
     */
    UserSimpleDTO removeTransaction(User user, Transaction transaction);

    /**
     * Add a point log to a user.
     *
     * @param user The User to which the PointLog should be added.
     * @param log  The PointLog to add.
     * @return The updated UserDTO object.
     */
    UserSimpleDTO addLog(User user, PointLog log);

    /**
     * Remove a point log from a user.
     *
     * @param user The User from which the PointLog should be removed.
     * @param log  The PointLog to remove.
     * @return The updated UserDTO object.
     */
    UserSimpleDTO removeLog(User user, PointLog log);

    /**
     * Finds a user by their license plate.
     *
     * @param licencePlate The license plate to search for.
     * @return The user associated with the provided license plate, or null if not found.
     */
    User findUserByLicensePlate(String licencePlate);

    User findByUUID(UUID userId);

    void update(String email, UserProfileUpdateDTO userProfileUpdateDTO);
}
