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
import eus.ehu.tfg.ktoda.tollsystem.address.AddressRequestObj;
import eus.ehu.tfg.ktoda.tollsystem.telephone.Telephone;
import eus.ehu.tfg.ktoda.tollsystem.telephone.TelephoneRequestObj;
import eus.ehu.tfg.ktoda.tollsystem.user.User;
import eus.ehu.tfg.ktoda.tollsystem.user.UserRequestObj;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.Vehicle;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.VehicleRequestObj;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class RegistrationRequest {
    @Valid
    private UserRequestObj userRequest;
    @Valid
    private Address addressRequest;
    @Valid
    private Telephone telephoneRequest;
    @Valid
    private Vehicle vehicleRequest;
    private boolean termsAndPolicy;
    // + files but that is for feature uses
}
