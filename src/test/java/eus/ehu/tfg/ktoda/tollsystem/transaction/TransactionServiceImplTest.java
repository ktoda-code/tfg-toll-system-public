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
import eus.ehu.tfg.ktoda.tollsystem.section.dto.SectionSimpleDTO;
import eus.ehu.tfg.ktoda.tollsystem.section.dto.SectionWithFeesDTO;
import eus.ehu.tfg.ktoda.tollsystem.transaction.dto.TransactionDTO;
import eus.ehu.tfg.ktoda.tollsystem.transaction.dto.TransactionDTOMapper;
import eus.ehu.tfg.ktoda.tollsystem.transaction.exception.TransactionNotFoundException;
import eus.ehu.tfg.ktoda.tollsystem.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class TransactionServiceImplTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionDTOMapper transactionDTOMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_ReturnsListOfTransactionDTO() {
        List<Transaction> transactions = List.of(
                new Transaction(23, null, null),
                new Transaction(145, null, null)
        );
        SectionSimpleDTO sectionWithFeesDTO = new SectionSimpleDTO(
                "AP-8", 80, true
        );

        when(transactionRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(transactions));
        when(transactionDTOMapper.apply(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);
            return new TransactionDTO(
                    transaction.getTransactionId(),
                    transaction.getAmount(),
                    transaction.getTimestamp(),
                    transaction.getStatus(),
                    sectionWithFeesDTO
            );
        });

        // Act
        List<TransactionDTO> result = transactionService.findAll();

        // Assert
        verify(transactionRepository).findAll(any(Pageable.class));
        verify(transactionDTOMapper, times(2)).apply(any(Transaction.class));
        assertThat(result).hasSize(2);
        assertThat(result.get(0).amount()).isEqualTo(23);
        assertThat(result.get(1).amount()).isEqualTo(145);
    }

    @Test
    void findById_existingId_shouldReturnSectionDTO() {
        // Arrange
        Long existingId = 1L;
        Transaction existingTransaction = new Transaction(
                existingId,
                null,
                null,
                150,
                null,
                null);
        when(transactionRepository.findById(existingId)).thenReturn(Optional.of(existingTransaction));
        when(transactionDTOMapper.apply(existingTransaction)).thenAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);
            return new TransactionDTO(
                    transaction.getTransactionId(),
                    transaction.getAmount(),
                    transaction.getTimestamp(),
                    transaction.getStatus(),
                    null
            );
        });

        // Act
        TransactionDTO result = transactionService.findById(existingId);

        // Assert
        verify(transactionRepository).findById(existingId);
        assertThat(result.amount()).isEqualTo(150);
    }

    @Test
    void findById_nonExistingId_shouldThrowException() {
        // Arrange
        Long nonExistingId = 999L;
        when(transactionRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        TransactionNotFoundException exception = assertThrows(TransactionNotFoundException.class,
                () -> transactionService.findById(nonExistingId));
        assertEquals("Transaction object not found with id" + nonExistingId, exception.getMessage());
        verify(transactionRepository).findById(nonExistingId);
    }

    @Test
    void removeById_ExistingId_ReturnsTrue() {
        // Arrange
        long existingId = 1L;
        when(transactionRepository.existsById(existingId)).thenReturn(true);

        // Act
        boolean result = transactionService.removeById(existingId);

        // Assert
        assertTrue(result);
        verify(transactionRepository).deleteById(existingId);
        verify(transactionRepository, times(1)).existsById(existingId);
    }

    @Test
    void removeById_NonExistingId_ReturnsFalse() {
        // Arrange
        long nonExistingId = -1L;
        when(transactionRepository.existsById(nonExistingId)).thenReturn(false);

        // Act
        boolean result = transactionService.removeById(nonExistingId);

        // Assert
        assertFalse(result);
        verify(transactionRepository, never()).deleteById(anyLong());
    }

    @Test
    void saveTransactionEntity() {
        // Arrange
        Transaction section = new Transaction();
        when(transactionRepository.save(section)).thenReturn(section);

        // Act
        transactionService.save(section);

        // Assert
        verify(transactionRepository).save(section);
    }

    @Test
    void saveTransactionRequest() {
        // Arrange
        TransactionRequest transactionRequest = new TransactionRequest(
                14, null, null);
        Transaction savedTransaction = new Transaction(
                transactionRequest.amount(),
                transactionRequest.timestamp(),
                transactionRequest.status()
        );
        TransactionDTO expectedTransactionDTO = new TransactionDTO(
                0L,
                14,
                null,
                null,
                null
        );

        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        when(transactionDTOMapper.apply(savedTransaction)).thenReturn(expectedTransactionDTO);

        // Act
        TransactionDTO result = transactionService.save(transactionRequest);

        // Assert
        verify(transactionRepository).save(any(Transaction.class));
        assertThat(result.amount()).isEqualTo(expectedTransactionDTO.amount());
        assertThat(result).isInstanceOf(TransactionDTO.class).isEqualTo(expectedTransactionDTO);
    }

    @Test
    void update_ExistingId_ReturnsUpdatedTransactionDTO() {
        // Arrange
        long existingId = 1L;
        TransactionRequest transactionRequest = new TransactionRequest(
                14, null, null);
        Transaction existingTransaction = new Transaction(existingId,
                null, null, 8, null, null);
        Transaction updatedTransaction = new Transaction(transactionRequest.amount(),
                transactionRequest.timestamp(), transactionRequest.status());
        TransactionDTO expectedPointDTO = new TransactionDTO(
                updatedTransaction.getTransactionId(),
                updatedTransaction.getAmount(),
                updatedTransaction.getTimestamp(),
                updatedTransaction.getStatus(),
                null
        );

        when(transactionRepository.findById(existingId)).thenReturn(Optional.of(existingTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(updatedTransaction);
        when(transactionDTOMapper.apply(updatedTransaction)).thenReturn(expectedPointDTO);

        // Act
        TransactionDTO result = transactionService.update(existingId, transactionRequest);

        // Assert
        verify(transactionRepository, times(1)).findById(existingId);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        assertThat(result.amount()).isEqualTo(expectedPointDTO.amount());
    }

    @Test
    void update_NonExistingId_ThrowsException() {
        // Arrange
        long nonExistingId = -1L;
        TransactionRequest transactionRequest = new TransactionRequest(
                14, null, null);
        when(transactionRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TransactionNotFoundException.class,
                () -> transactionService.update(nonExistingId, transactionRequest));
        verify(transactionRepository).findById(nonExistingId);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void assignSection() {
        // Arrange
        Transaction transaction = new Transaction(
                0L,
                null,
                null,
                160,
                null,
                TransactionStatus.PENDING
        );
        Section section = new Section(
                "test",
                200,
                false,
                null,
                null,
                List.of(transaction));
        Transaction savedTransaction = new Transaction(
                0L,
                null,
                section,
                160,
                null,
                TransactionStatus.PENDING
        );
        SectionSimpleDTO expectedSectionWithFeesDTO = new SectionSimpleDTO(
                section.getSectionId(),
                section.getLongKm(),
                section.getActive()
        );
        TransactionDTO expectedDTO = new TransactionDTO(
                savedTransaction.getTransactionId(),
                savedTransaction.getAmount(),
                savedTransaction.getTimestamp(),
                savedTransaction.getStatus(),
                expectedSectionWithFeesDTO
        );
        when(transactionRepository.findById(transaction.getTransactionId())).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(transaction)).thenReturn(savedTransaction);
        when(transactionDTOMapper.apply(savedTransaction)).thenReturn(expectedDTO);

        // Act & Assert
        TransactionDTO result = transactionService.assignSection(transaction, section);
        assertThat(result).isEqualTo(expectedDTO);
        assertThat(result.section()).isEqualTo(expectedSectionWithFeesDTO);
        verify(transactionDTOMapper, times(1)).apply(any(Transaction.class));
    }

    @Test
    void assignUser() {
        // Arrange
        Transaction transaction = new Transaction(
                0L,
                null,
                null,
                160,
                null,
                TransactionStatus.PENDING
        );
        User user = new User(
                new UUID(2, 1),
                "Test",
                "Transaction"
                , "12a@A!asd",
                "test@test.com",
                null,
                true, null,
                null, null, null, null,null);
        Transaction savedTransaction = new Transaction(
                0L,
                null,
                null,
                160,
                null,
                TransactionStatus.PENDING
        );
        TransactionDTO expectedDTO = new TransactionDTO(
                savedTransaction.getTransactionId(),
                savedTransaction.getAmount(),
                savedTransaction.getTimestamp(),
                savedTransaction.getStatus(),
                null
        );

        when(transactionRepository.findById(transaction.getTransactionId())).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(transaction)).thenReturn(savedTransaction);
        when(transactionDTOMapper.apply(savedTransaction)).thenReturn(expectedDTO);

        // Act & Assert
        TransactionDTO result = transactionService.assignUser(transaction, user);
        assertThat(result).isEqualTo(expectedDTO);
        verify(transactionDTOMapper, times(1)).apply(any(Transaction.class));
    }
}