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

package eus.ehu.tfg.ktoda.tollsystem.pointlog.dto;

import eus.ehu.tfg.ktoda.tollsystem.point.dto.PointDTOMapper;
import eus.ehu.tfg.ktoda.tollsystem.pointlog.PointLog;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.Vehicle;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PointLogDTOAnonymityMapper implements Function<PointLog, PointLogDTOAnonymity> {
    private PointDTOMapper pointDTOMapper;

    @Override
    public PointLogDTOAnonymity apply(PointLog log) {
        if (log == null) return null;

        return new PointLogDTOAnonymity(
                log.getLogId(),
                log.getTimestamp(),
                pointDTOMapper.apply(log.getPoint()),
                log.getUser()
                        .getVehicles()
                        .stream()
                        .map(vehicle -> anonymizeLicensePlate(vehicle.getLicensePlate()))
                        .collect(Collectors.toList())
        );
    }

    private String anonymizeLicensePlate(String licensePlate) {
        // Replace characters 1, 3, and 4 with placeholders (e.g., '*')
        char[] anonymizedChars = licensePlate.toCharArray();
        if (anonymizedChars.length >= 4) {
            anonymizedChars[0] = '*';
            anonymizedChars[2] = '*';
            anonymizedChars[3] = '*';
        }
        return new String(anonymizedChars);
    }
}
