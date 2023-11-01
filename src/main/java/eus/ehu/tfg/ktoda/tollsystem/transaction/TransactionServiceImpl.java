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

import eus.ehu.tfg.ktoda.tollsystem.point.Point;
import eus.ehu.tfg.ktoda.tollsystem.point.dto.PointDTO;
import eus.ehu.tfg.ktoda.tollsystem.point.exception.PointNotFoundException;
import eus.ehu.tfg.ktoda.tollsystem.section.Section;
import eus.ehu.tfg.ktoda.tollsystem.transaction.dto.TransactionDTO;
import eus.ehu.tfg.ktoda.tollsystem.transaction.dto.TransactionDTOMapper;
import eus.ehu.tfg.ktoda.tollsystem.transaction.exception.TransactionNotFoundException;
import eus.ehu.tfg.ktoda.tollsystem.user.User;
import eus.ehu.tfg.ktoda.tollsystem.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class TransactionServiceImpl implements TransactionService {
    private TransactionRepository transactionRepository;
    private TransactionDTOMapper transactionDTOMapper;

    @Override
    public List<TransactionDTO> findAll() {
        int pageInit = 0;
        int pageSize = 8;
        Pageable page = PageRequest.of(pageInit, pageSize);
        return transactionRepository
                .findAll(page)
                .stream()
                .map(transactionDTOMapper)
                .toList();
    }

    @Override
    public Page<Transaction> findByUser(User user, Pageable pageable) {
        return transactionRepository.findByUser(user, pageable);
    }

    @Override
    public Transaction findByDTO(TransactionDTO transactionDTO) {
        return transactionRepository
                .findById(transactionDTO.transactionId())
                .orElseThrow(TransactionNotFoundException::new);
    }

    @Override
    public TransactionDTO findById(long id) {
        Optional<TransactionDTO> transactionDTO = transactionRepository
                .findById(id)
                .map(transactionDTOMapper);
        return transactionDTO.orElseThrow(
                () -> new TransactionNotFoundException("Transaction object not found with id" + id));
    }

    @Override
    public boolean removeById(long id) {
        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    @Override
    public TransactionDTO save(TransactionRequest transactionRequest) {
        Transaction transaction = new Transaction(
                transactionRequest.amount(),
                transactionRequest.timestamp(),
                transactionRequest.status()
        );
        log.info("Creating object: " + transaction);
        return transactionDTOMapper.apply(
                transactionRepository.save(transaction)
        );
    }

    @Override
    public TransactionDTO update(long id, TransactionRequest transactionRequest) {
        log.info("Trying to modify: " + id);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() ->
                        new TransactionNotFoundException("Transaction object not found with id" + id));
        transaction.setAmount(transactionRequest.amount());
        transaction.setStatus(transactionRequest.status());
        transaction.setTimestamp(transactionRequest.timestamp());
        log.info("Modifying object " + transaction);
        return transactionDTOMapper.apply(
                transactionRepository.save(transaction)
        );
    }

    @Override
    public TransactionDTO assignSection(Transaction transaction, Section section) {
        Transaction managedTransaction = transactionRepository.findById(transaction.getTransactionId())
                .orElseThrow(() ->
                        new TransactionNotFoundException("Transaction not found with id: "
                                + transaction.getTransactionId()));

        managedTransaction.setSection(section);
        log.info("Saving object: " + managedTransaction);
        return transactionDTOMapper.apply(
                transactionRepository.save(managedTransaction)
        );
    }

    @Override
    public TransactionDTO assignUser(Transaction transaction, User user) {
        Transaction managedTransaction = transactionRepository.findById(transaction.getTransactionId())
                .orElseThrow(() ->
                        new TransactionNotFoundException("Transaction not found with id: "
                                + transaction.getTransactionId()));

        managedTransaction.setUser(user);
        log.info("Saving object: " + managedTransaction);
        return transactionDTOMapper.apply(
                transactionRepository.save(managedTransaction)
        );
    }
}
