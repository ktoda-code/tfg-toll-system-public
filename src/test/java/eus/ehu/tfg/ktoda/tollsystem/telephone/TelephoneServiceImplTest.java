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

package eus.ehu.tfg.ktoda.tollsystem.telephone;

import eus.ehu.tfg.ktoda.tollsystem.telephone.dto.TelephoneDTO;
import eus.ehu.tfg.ktoda.tollsystem.telephone.dto.TelephoneDTOMapper;
import eus.ehu.tfg.ktoda.tollsystem.telephone.exception.TelephoneAlreadyExistsException;
import eus.ehu.tfg.ktoda.tollsystem.telephone.exception.TelephoneNotFoundException;
import eus.ehu.tfg.ktoda.tollsystem.user.User;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.*;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.dto.VehicleDTO;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class TelephoneServiceImplTest {
    @Mock
    private TelephoneRepository telephoneRepository;

    @Mock
    private TelephoneDTOMapper telephoneDTOMapper;

    @InjectMocks
    private TelephoneServiceImpl telephoneService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_ReturnsListOfTelephoneDTO() {
        List<Telephone> telephones = List.of(
                new Telephone("+34000111222"),
                new Telephone("+34999888777")
        );
        when(telephoneRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(telephones));
        when(telephoneDTOMapper.apply(any(Telephone.class))).thenAnswer(invocation -> {
            Telephone telephone = invocation.getArgument(0);
            return new TelephoneDTO(
                    telephone.getTelephoneId(),
                    telephone.getNumber()
            );
        });

        // Act
        List<TelephoneDTO> result = telephoneService.findAll();

        // Assert
        verify(telephoneRepository).findAll(any(Pageable.class));
        verify(telephoneDTOMapper, times(2)).apply(any(Telephone.class));
        assertThat(result).hasSize(2);
        assertThat(result.get(0).number()).isEqualTo("+34000111222");
        assertThat(result.get(1).number()).isEqualTo("+34999888777");
    }

    @Test
    void findById_existingId_shouldReturnTelephoneDTO() {
        // Arrange
        long existingId = 1L;
        Telephone existingTelephone = new Telephone("+34000111222");
        when(telephoneRepository.findById(existingId)).thenReturn(Optional.of(existingTelephone));
        when(telephoneDTOMapper.apply(existingTelephone)).thenAnswer(invocation -> {
            Telephone telephone = invocation.getArgument(0);
            return new TelephoneDTO(
                    telephone.getTelephoneId(),
                    telephone.getNumber()
            );
        });

        // Act
        TelephoneDTO result = telephoneService.findById(existingId);

        // Assert
        verify(telephoneRepository).findById(existingId);
        assertThat(result.number()).isEqualTo("+34000111222");
    }

    @Test
    void findById_nonExistingId_shouldThrowException() {
        // Arrange
        long nonExistingId = 999L;
        when(telephoneRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TelephoneNotFoundException.class,
                () -> telephoneService.findById(nonExistingId));
        verify(telephoneRepository).findById(nonExistingId);
    }

    @Test
    void removeById_ExistingId_ReturnsTrue() {
        // Arrange
        long existingId = 1L;
        when(telephoneRepository.existsById(existingId)).thenReturn(true);

        // Act
        boolean result = telephoneService.removeById(existingId);

        // Assert
        assertTrue(result);
        verify(telephoneRepository).deleteById(existingId);
        verify(telephoneRepository, times(1)).existsById(existingId);
    }

    @Test
    void removeById_NonExistingId_ReturnsFalse() {
        // Arrange
        long nonExistingId = -1L;
        when(telephoneRepository.existsById(nonExistingId)).thenReturn(false);

        // Act
        boolean result = telephoneService.removeById(nonExistingId);

        // Assert
        assertFalse(result);
        verify(telephoneRepository, never()).deleteById(anyLong());
    }

    @Test
    void saveTelephoneEntity() {
        // Arrange
        Telephone telephone = new Telephone();
        when(telephoneRepository.save(telephone)).thenReturn(telephone);

        // Act
        telephoneService.save(telephone);

        // Assert
        verify(telephoneRepository).save(telephone);
    }

    @Test
    void saveTelephoneRequest() {
        // Arrange
        TelephoneRequest telephoneRequest = new TelephoneRequest("+1230");
        Telephone savedTelephone = new Telephone(
                telephoneRequest.number()
        );
        TelephoneDTO expectedTelephoneDTO = new TelephoneDTO(
                savedTelephone.getTelephoneId(),
                savedTelephone.getNumber()
        );

        when(telephoneRepository.save(any(Telephone.class))).thenReturn(savedTelephone);
        when(telephoneDTOMapper.apply(savedTelephone)).thenReturn(expectedTelephoneDTO);

        // Act
        TelephoneDTO result = telephoneService.save(telephoneRequest);

        // Assert
        verify(telephoneRepository).save(any(Telephone.class));
        assertThat(result.number()).isEqualTo(expectedTelephoneDTO.number());
        assertThat(result).isInstanceOf(TelephoneDTO.class).isEqualTo(expectedTelephoneDTO);
    }

    @Test
    void saveTelephoneRequest_shouldThrowException() {
        // Arrange
        TelephoneRequest telephoneRequest = new TelephoneRequest("+1230");

        when(telephoneRepository.existsByNumber(telephoneRequest.number()))
                .thenReturn(true);

        // Act & Assert
        assertThrows(TelephoneAlreadyExistsException.class, () -> telephoneService.save(telephoneRequest));
    }

    @Test
    void update_ExistingId_ReturnsUpdatedTelephoneDTO() {
        // Arrange
        long existingId = 1L;
        TelephoneRequest telephoneRequest = new TelephoneRequest("+1230");
        Telephone existingTelephone = new Telephone(existingId, telephoneRequest.number(), null);
        Telephone updatedTelephone = new Telephone(
                telephoneRequest.number());
        TelephoneDTO expectedVehicleDTO = new TelephoneDTO(
                updatedTelephone.getTelephoneId(),
                updatedTelephone.getNumber()
        );

        when(telephoneRepository.findById(existingId)).thenReturn(Optional.of(existingTelephone));
        when(telephoneRepository.save(any(Telephone.class))).thenReturn(updatedTelephone);
        when(telephoneDTOMapper.apply(updatedTelephone)).thenReturn(expectedVehicleDTO);

        // Act
        TelephoneDTO result = telephoneService.update(existingId, telephoneRequest);

        // Assert
        verify(telephoneRepository, times(1)).findById(existingId);
        verify(telephoneRepository, times(1)).save(any(Telephone.class));
        assertThat(result.number()).isEqualTo(expectedVehicleDTO.number());
    }

    @Test
    void update_NonExistingId_ThrowsException() {
        // Arrange
        long nonExistingId = -1L;
        TelephoneRequest telephoneRequest = new TelephoneRequest("+43112");
        when(telephoneRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TelephoneNotFoundException.class,
                () -> telephoneService.update(nonExistingId, telephoneRequest));
        verify(telephoneRepository).findById(nonExistingId);
        verify(telephoneRepository, never()).save(any(Telephone.class));
    }


    @Test
    void assignUser() {
        // Arrange
        Telephone telephone = new Telephone(
                0L,
                "+34500123456",
                null
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
        Telephone savedTelephone = new Telephone(
                0L, telephone.getNumber(), user

        );
        TelephoneDTO expectedDTO = new TelephoneDTO(
                savedTelephone.getTelephoneId(),
                savedTelephone.getNumber()
        );

        when(telephoneRepository.save(telephone)).thenReturn(savedTelephone);
        when(telephoneDTOMapper.apply(savedTelephone)).thenReturn(expectedDTO);

        // Act & Assert
        TelephoneDTO result = telephoneService.assignUser(telephone, user);
        assertThat(result).isEqualTo(expectedDTO);
        verify(telephoneDTOMapper, times(1)).apply(any(Telephone.class));
    }
}