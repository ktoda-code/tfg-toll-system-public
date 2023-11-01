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

package eus.ehu.tfg.ktoda.tollsystem.pointlog;

import eus.ehu.tfg.ktoda.tollsystem.pointlog.dto.PointLogDTO;
import eus.ehu.tfg.ktoda.tollsystem.pointlog.dto.PointLogDTOAnonymity;
import eus.ehu.tfg.ktoda.tollsystem.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Provides methods for managing PointLogs.
 */
public interface PointLogService {

    /**
     * Retrieves a list of all PointLogs.
     *
     * @return A list of PointLogDTO objects.
     */
    List<PointLogDTO> findAll();

    List<PointLogDTOAnonymity> findAllAnonymity();

    List<PointLogDTOAnonymity> findAllBySection(String id);

    /**
     * Retrieves a Point by its unique ID.
     *
     * @param pointDTO The PointDTO to retrieve.
     * @return The Point object if found, or null if not found.
     */
    PointLog findByDTO(PointLogDTO pointDTO);

    /**
     * Retrieves a PointLog by its unique ID.
     *
     * @param id The ID of the PointLog to retrieve.
     * @return The PointLogDTO object if found, or null if not found.
     */
    PointLogDTO findById(long id);

    /**
     * Removes a PointLog by its unique ID.
     *
     * @param id The ID of the PointLog to remove.
     * @return true if the PointLog was successfully removed, false if not found.
     */
    boolean removeById(long id);

    /**
     * Saves a new PointLog.
     *
     * @param pointLog The PointLog object to save.
     */
    void save(PointLog pointLog);

    /**
     * Saves a new PointLog.
     *
     * @param pointLogRequest The PointLog request object to save.
     * @return The saved PointLogDTO object.
     */
    PointLogDTO save(PointLogRequest pointLogRequest);

    /**
     * Updates a PointLog.
     *
     * @param id              The ID of the PointLog to update.
     * @param pointLogRequest The PointLog request object for the update.
     * @return The updated PointLogDTO object.
     */
    PointLogDTO update(long id, PointLogRequest pointLogRequest);

    /**
     * Finds the latest point log entry for a specific user based on the provided user ID and pagination parameters.
     *
     * @param userId The unique identifier (UUID) of the user for whom to retrieve the latest point log entry.
     * @return The latest PointLog entry for the specified user, or null if no matching entry is found.
     */
    PointLog findLatestENTERPointOfUser(UUID userId);


    Page<PointLog> findByUser(User user, Pageable pageable);
}
