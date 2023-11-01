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

package eus.ehu.tfg.ktoda.tollsystem.fee.dto;

import eus.ehu.tfg.ktoda.tollsystem.fee.Fee;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class FeeDTOMapper implements Function<Fee, FeeDTO> {
    @Override
    public FeeDTO apply(Fee fee) {
        if (fee == null) {
            return null;
        }

        return new FeeDTO(
                fee.getFeeId(),
                fee.getQuantity(),
                fee.getVehicleType(),
                fee.getActive()
        );
    }
}
