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

import eus.ehu.tfg.ktoda.tollsystem.address.dto.AddressDTOMapper;
import eus.ehu.tfg.ktoda.tollsystem.telephone.dto.TelephoneDTOMapper;
import eus.ehu.tfg.ktoda.tollsystem.transaction.dto.TransactionDTOMapper;
import eus.ehu.tfg.ktoda.tollsystem.user.User;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.dto.VehicleDTOMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@AllArgsConstructor
public class UserDTOMapper implements Function<User, UserDTO> {
    private TransactionDTOMapper transactionDTOMapper;
    private TelephoneDTOMapper telephoneDTOMapper;
    private AddressDTOMapper addressDTOMapper;
    private VehicleDTOMapper vehicleDTOMapper;

    @Override
    public UserDTO apply(User user) {
        if (user == null) {
            return null;
        }

        return new UserDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getTransactions()
                        .stream()
                        .map(transactionDTOMapper)
                        .toList(),
                user.getTelephones()
                        .stream()
                        .map(telephoneDTOMapper)
                        .toList(),
                user.getVehicles()
                        .stream()
                        .map(vehicleDTOMapper)
                        .toList(),
                addressDTOMapper.apply(user.getAddress())
        );
    }
}
