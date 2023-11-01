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

package eus.ehu.tfg.ktoda.tollsystem.point;

import eus.ehu.tfg.ktoda.tollsystem.geoLocation.GeoLocation;
import eus.ehu.tfg.ktoda.tollsystem.geoLocation.dto.GeoLocationDTO;
import eus.ehu.tfg.ktoda.tollsystem.point.dto.PointDTO;
import eus.ehu.tfg.ktoda.tollsystem.pointlog.PointLog;
import eus.ehu.tfg.ktoda.tollsystem.section.Section;
import eus.ehu.tfg.ktoda.tollsystem.user.User;
import eus.ehu.tfg.ktoda.tollsystem.user.dto.UserDTO;

import java.util.List;


public interface PointService {
    /**
     * Retrieves a list of all Points.
     *
     * @return A list of PointDTO objects.
     */
    List<PointDTO> findAll();

    /**
     * Retrieves a Point by its unique ID.
     *
     * @param pointDTO The PointDTO to retrieve.
     * @return The Point object if found, or null if not found.
     */
    Point findByDTO(PointDTO pointDTO);

    /**
     * Retrieves a PointDTO by its unique ID.
     *
     * @param id The ID of the Point to retrieve.
     * @return The PointDTO object if found, or null if not found.
     */
    PointDTO findByIdDTO(Long id);


    /**
     * Retrieves a Point by its unique ID.
     *
     * @param id The ID of the Point to retrieve.
     * @return The Point object if found, or null if not found.
     */
    Point findById(long id);

    /**
     * Removes a Point by its unique ID.
     *
     * @param id The ID of the Point to remove.
     * @return true if the Point was successfully removed, false if not found.
     */
    boolean removeById(long id);

    /**
     * Saves a new Point.
     *
     * @param point The Point object to save.
     */
    void save(Point point);

    /**
     * Saves a new Point.
     *
     * @param pointRequest The Point object to save.
     * @return The saved PointDTO object.
     */
    PointDTO save(PointRequest pointRequest);

    /**
     * Update a Point.
     *
     * @param id           The ID of the Point to update.
     * @param pointRequest The Point request object to update.
     * @return The updated PointDTO object.
     */
    PointDTO update(long id, PointRequest pointRequest);

    /**
     * Assign a GeoLocation to a Point.
     *
     * @param point       The Point to which the GeoLocation should be assigned.
     * @param geoLocation The GeoLocation to assign.
     * @return The updated PointDTO object.
     */
    PointDTO assignGeoLocation(Point point, GeoLocation geoLocation);

    /**
     * Assign a Section to a Point.
     *
     * @param point   The Point to which the Section should be assigned.
     * @param section The Section to assign.
     * @return The updated PointDTO object.
     */
    PointDTO assignSection(Point point, Section section);

    /**
     * Add a point log to a point.
     *
     * @param point The Point to which the PointLog should be added.
     * @param log   The PointLog to add.
     * @return The updated PointDTO object.
     */
    PointDTO addLog(Point point, PointLog log);

    /**
     * Remove a point log from a point.
     *
     * @param point The Point from which the PointLog should be removed.
     * @param log   The PointLog to remove.
     * @return The updated PointDTO object.
     */
    PointDTO removeLog(Point point, PointLog log);


}
