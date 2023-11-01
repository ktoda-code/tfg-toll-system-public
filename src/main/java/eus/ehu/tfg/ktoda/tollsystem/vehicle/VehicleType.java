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

package eus.ehu.tfg.ktoda.tollsystem.vehicle;

/**
 * An enum representing the different vehicle types that are relevant to the toll system in Spain.
 */
public enum VehicleType {
    /**
     * Motorcycles, passenger cars, and passenger cars with single-axle trailers.
     */
    CATEGORY_I,
    /**
     * Passenger cars with double-axle trailers, buses, and motor homes.
     */
    CATEGORY_II,
    /**
     * Passenger cars with trailers from 2 or more axles, and buses/HGVs with 4 or more axles
     */
    CATEGORY_III
}
