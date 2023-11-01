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

package eus.ehu.tfg.ktoda.tollsystem.transaction;

import eus.ehu.tfg.ktoda.tollsystem.section.Section;
import eus.ehu.tfg.ktoda.tollsystem.transaction.dto.TransactionDTO;
import eus.ehu.tfg.ktoda.tollsystem.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionService {
    /**
     * Retrieves a list of all transactions.
     *
     * @return A list of TransactionDTO objects.
     */
    List<TransactionDTO> findAll();

    Page<Transaction> findByUser(User user, Pageable pageable);

    /**
     * Retrieves a transaction.
     *
     * @param transactionDTO The TransactionDTO to retrieve.
     * @return The Transaction object if found, or null if not found.
     */
    Transaction findByDTO(TransactionDTO transactionDTO);

    /**
     * Retrieves a transaction by its unique ID.
     *
     * @param id The ID of the transaction to retrieve.
     * @return The TransactionDTO object if found, or null if not found.
     */
    TransactionDTO findById(long id);

    /**
     * Removes a transaction by its unique ID.
     *
     * @param id The ID of the transaction to remove.
     * @return true if the transaction was successfully removed, false if not found.
     */
    boolean removeById(long id);

    /**
     * Saves a new transaction.
     *
     * @param transaction The Transaction object to save.
     */
    void save(Transaction transaction);

    /**
     * Saves a new transaction.
     *
     * @param transactionRequest The Transaction object to save.
     * @return The saved TransactionDTO object.
     */
    TransactionDTO save(TransactionRequest transactionRequest);

    /**
     * Update a transaction.
     *
     * @param id                 The ID of the transaction to update.
     * @param transactionRequest The Transaction request object to update.
     * @return The updated TransactionDTO object.
     */
    TransactionDTO update(long id, TransactionRequest transactionRequest);

    /**
     * Assign a Section to a transaction.
     *
     * @param transaction The transaction to which the Section should be assigned.
     * @param section     The Section to assign.
     * @return The updated TransactionDTO object.
     */
    TransactionDTO assignSection(Transaction transaction, Section section);

    /**
     * Assign a User to a transaction.
     *
     * @param transaction The transaction to which the User should be assigned.
     * @param user        The User to assign.
     * @return The updated TransactionDTO object.
     */
    TransactionDTO assignUser(Transaction transaction, User user);

}
