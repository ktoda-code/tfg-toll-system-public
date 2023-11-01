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

package eus.ehu.tfg.ktoda.tollsystem.sign;

import eus.ehu.tfg.ktoda.tollsystem.address.Address;
import eus.ehu.tfg.ktoda.tollsystem.address.AddressService;
import eus.ehu.tfg.ktoda.tollsystem.telephone.Telephone;
import eus.ehu.tfg.ktoda.tollsystem.telephone.TelephoneService;
import eus.ehu.tfg.ktoda.tollsystem.user.User;
import eus.ehu.tfg.ktoda.tollsystem.user.UserService;
import eus.ehu.tfg.ktoda.tollsystem.user.dto.UserSimpleDTO;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.Vehicle;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.VehicleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {
    private final UserService userService;
    private final AddressService addressService;
    private final VehicleService vehicleService;
    private final TelephoneService telephoneService;

    @Override
    @Transactional
    public UserSimpleDTO registerUser(User user, Address address, Vehicle vehicle, Telephone telephone) {
        log.info("Registering user: " + user);
        userService.register(user);

        if (telephone != null) {
            telephone.setUser(user);
            user.addTelephone(telephone);
            telephoneService.save(telephone);
        }

        user.setAddress(address);
        address.setUser(user);
        user.addVehicle(vehicle);
        vehicle.setUser(user);
        // Just for demo
        if (user.getEmail().contains("auToll")) {
            user.addRole("ADMIN");
        }
        user.addRole("USER");


        addressService.save(address);
        vehicleService.save(vehicle);
        userService.save(user);
        log.info("Saved user: " + user);
        return userService.findByEmail(user.getEmail());
    }
}
