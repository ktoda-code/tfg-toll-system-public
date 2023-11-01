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

package eus.ehu.tfg.ktoda.tollsystem.geoLocation;

import eus.ehu.tfg.ktoda.tollsystem.geoLocation.dto.GeoLocationDTO;
import eus.ehu.tfg.ktoda.tollsystem.point.Point;

import java.util.List;

public interface GeoLocationService {
    /**
     * Retrieves a list of all GeoLocations.
     *
     * @return A list of GeoLocationDTO objects.
     */
    List<GeoLocationDTO> findAll();

    /**
     * Retrieves a GeoLocation by its unique ID.
     *
     * @param geoLocationDTO The the GeoLocationDTO to retrieve.
     * @return The GeoLocation object if found, or null if not found.
     */
    GeoLocation findByDTO(GeoLocationDTO geoLocationDTO);

    /**
     * Retrieves a GeoLocation by its unique ID.
     *
     * @param id The ID of the GeoLocation to retrieve.
     * @return The GeoLocationDTO object if found, or null if not found.
     */
    GeoLocationDTO findById(long id);

    /**
     * Removes a GeoLocation by its unique ID.
     *
     * @param id The ID of the GeoLocation to remove.
     * @return true if the GeoLocation was successfully removed, false if not found.
     */
    boolean removeById(long id);

    /**
     * Saves a new GeoLocation.
     *
     * @param geoLocation The GeoLocation object to save.
     */
    void save(GeoLocation geoLocation);

    /**
     * Saves a new GeoLocation.
     *
     * @param geoLocationRequest The GeoLocation object to save.
     * @return The saved GeoLocation object.
     */
    GeoLocationDTO save(GeoLocationRequest geoLocationRequest);

    /**
     * Update a GeoLocation.
     *
     * @param id                 The ID of the GeoLocation to update.
     * @param geoLocationRequest The GeoLocation request object to update.
     * @return The updated GeoLocation object.
     */
    GeoLocationDTO update(long id, GeoLocationRequest geoLocationRequest);

    /**
     * Assigns a Point to a GeoLocation.
     * <p>
     * This method associates a Point with a GeoLocation, effectively marking the geographic
     * coordinates represented by the GeoLocation with the provided Point.
     *
     * @param geoLocation The GeoLocation to which the Point will be assigned.
     * @param point       The Point to assign to the GeoLocation.
     * @return A new GeoLocation object representing the same geographic coordinates as the
     * original GeoLocation but with the assigned Point.
     */
    GeoLocationDTO assignPoint(GeoLocation geoLocation, Point point);
}
