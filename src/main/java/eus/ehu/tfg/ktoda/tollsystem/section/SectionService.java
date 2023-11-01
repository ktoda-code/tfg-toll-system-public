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

package eus.ehu.tfg.ktoda.tollsystem.section;


import eus.ehu.tfg.ktoda.tollsystem.fee.Fee;
import eus.ehu.tfg.ktoda.tollsystem.point.Point;
import eus.ehu.tfg.ktoda.tollsystem.section.dto.SectionDTO;
import eus.ehu.tfg.ktoda.tollsystem.section.dto.SectionSimpleDTO;
import eus.ehu.tfg.ktoda.tollsystem.section.dto.SectionWithFeesDTO;
import eus.ehu.tfg.ktoda.tollsystem.section.dto.SectionWithPointsDTO;
import eus.ehu.tfg.ktoda.tollsystem.transaction.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SectionService {
    /**
     * Retrieves a list of all Sections.
     *
     * @return A list of SectionWithFeesDTO objects.
     */
    List<SectionSimpleDTO> findAll();

    /**
     * Retrieves a list of all Sections.
     *
     * @return A list of SectionWithPointsDTO objects.
     */
    List<SectionWithPointsDTO> findAllWithPoints();

    /**
     * Retrieves a list of all Sections.
     *
     * @return A list of SectionWithFeesDTO objects.
     */
    List<SectionWithFeesDTO> findAllWithFees();

    Section findSectionById(String sectionId);

    /**
     * Retrieves a Section by its unique ID.
     *
     * @param sectionWithFeesDTO The SectionWithFeesDTO to retrieve.
     * @return The Section object if found, or null if not found.
     */
    Section findByDTO(SectionSimpleDTO sectionWithFeesDTO);

    /**
     * Retrieves a Section by its unique ID.
     *
     * @param id The ID of the Section to retrieve.
     * @return The SectionWithFeesDTO object if found, or null if not found.
     */
    SectionSimpleDTO findById(String id);

    /**
     * Removes a Section by its unique ID.
     *
     * @param id The ID of the Section to remove.
     * @return true if the Section was successfully removed, false if not found.
     */
    boolean removeById(String id);

    /**
     * Saves a new Section.
     *
     * @param section The Section object to save.
     */
    void save(Section section);

    /**
     * Saves a new Section.
     *
     * @param sectionRequest The Section object to save.
     * @return The saved SectionWithFeesDTO object.
     */
    SectionSimpleDTO save(SectionRequest sectionRequest);

    /**
     * Update a Section.
     *
     * @param id             The ID of the Section to update.
     * @param sectionRequest The Section request object to update.
     * @return The updated SectionWithFeesDTO object.
     */
    SectionSimpleDTO update(String id, SectionRequest sectionRequest);

    /**
     * Add a Fee to a Section.
     *
     * @param section The Section to which the Fee should be added.
     * @param fee     The Fee to add.
     * @return The updated SectionWithFeesDTO object.
     */
    SectionSimpleDTO addFee(Section section, Fee fee);

    /**
     * Remove a Fee from a Section.
     *
     * @param section The Section from which the Fee should be removed.
     * @param fee     The Fee to remove.
     * @return The updated SectionWithFeesDTO object.
     */
    SectionSimpleDTO removeFee(Section section, Fee fee);

    /**
     * Add a Point to a Section.
     *
     * @param section The Section to which the Point should be added.
     * @param point   The Point to add.
     * @return The updated SectionWithFeesDTO object.
     */
    SectionSimpleDTO addPoint(Section section, Point point);

    /**
     * Remove a Point from a Section.
     *
     * @param section The Section from which the Point should be removed.
     * @param point   The Point to remove.
     * @return The updated SectionWithFeesDTO object.
     */
    SectionSimpleDTO removePoint(Section section, Point point);

    /**
     * Add a Transaction to a Section.
     *
     * @param section     The Section to which the Transaction should be added.
     * @param transaction The Transaction to add.
     * @return The updated SectionWithFeesDTO object.
     */
    SectionSimpleDTO addTransaction(Section section, Transaction transaction);

    /**
     * Remove a Transaction from a Section.
     *
     * @param section     The Section from which the Transaction should be removed.
     * @param transaction The Transaction to remove.
     * @return The updated SectionWithFeesDTO object.
     */
    SectionSimpleDTO removeTransaction(Section section, Transaction transaction);


    Page<SectionWithPointsDTO> findAllWithPointsPage(Pageable pageable);

    Page<SectionWithFeesDTO> findAllWithFeesPage(Pageable pageable);

    Page<SectionDTO> findAllPage(Pageable pageable);
}
