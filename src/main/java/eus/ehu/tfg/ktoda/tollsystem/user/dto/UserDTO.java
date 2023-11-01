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

package eus.ehu.tfg.ktoda.tollsystem.user.dto;

import eus.ehu.tfg.ktoda.tollsystem.address.dto.AddressDTO;
import eus.ehu.tfg.ktoda.tollsystem.telephone.dto.TelephoneDTO;
import eus.ehu.tfg.ktoda.tollsystem.transaction.dto.TransactionDTO;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.dto.VehicleDTO;

import java.util.List;
import java.util.UUID;

public record UserDTO(UUID userId,
                      String firstName,
                      String lastName,
                      String email,
                      List<TransactionDTO> transactions,
                      List<TelephoneDTO> telephones,
                      List<VehicleDTO> vehicles,
                      AddressDTO address) {
}
