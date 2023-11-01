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
import eus.ehu.tfg.ktoda.tollsystem.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@AllArgsConstructor
public class UserSimpleDTOMapper implements Function<User, UserSimpleDTO> {
    private AddressDTOMapper addressDTOMapper;
    private TelephoneDTOMapper telephoneDTOMapper;

    @Override
    public UserSimpleDTO apply(User user) {
        if (user == null) return null;
        return new UserSimpleDTO(user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                addressDTOMapper.apply(user.getAddress()),
                user.getTelephones().stream().map(telephoneDTOMapper).toList());
    }
}
