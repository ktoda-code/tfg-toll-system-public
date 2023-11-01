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

package eus.ehu.tfg.ktoda.tollsystem.fee;

import eus.ehu.tfg.ktoda.tollsystem.fee.dto.FeeDTO;
import eus.ehu.tfg.ktoda.tollsystem.fee.dto.FeeDTOMapper;
import eus.ehu.tfg.ktoda.tollsystem.fee.exception.FeeNotFoundException;
import eus.ehu.tfg.ktoda.tollsystem.section.Section;
import eus.ehu.tfg.ktoda.tollsystem.section.SectionService;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FeeServiceImplTest {
    @Mock
    private FeeRepository feeRepository;
    @Mock
    private FeeDTOMapper feeDTOMapper;
    @Mock
    private SectionService sectionService;
    @InjectMocks
    private FeeServiceImpl feeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_ReturnsListOfFeeDTO() {
        // Arrange
        List<Fee> fees = Arrays.asList(
                new Fee(0, VehicleType.CATEGORY_I),
                new Fee()
        );

        when(feeRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(fees));
        when(feeDTOMapper.apply(any(Fee.class))).thenReturn(
                new FeeDTO(
                        0L,
                        5,
                        VehicleType.CATEGORY_I,
                        Boolean.TRUE)
        );

        // Act
        List<FeeDTO> result = feeService.findAll();

        // Assert
        System.out.println(result);
        verify(feeRepository, times(1)).findAll(any(Pageable.class));
        assertThat(result).hasSize(fees.size());
        assertThat(result.get(0).vehicleType()).isEqualTo(VehicleType.CATEGORY_I);
    }

    @Test
    void findById_existingId_shouldReturnFeeDTO() {
        // Arrange
        long existingId = 1L;
        Fee existingFee = new Fee(5, VehicleType.CATEGORY_I);
        when(feeRepository.findById(existingId)).thenReturn(Optional.of(existingFee));
        when(feeDTOMapper.apply(existingFee)).thenReturn(
                new FeeDTO(
                        existingFee.getFeeId(),
                        existingFee.getQuantity(),
                        existingFee.getVehicleType(),
                        existingFee.getActive())
        );

        // Act
        FeeDTO result = feeService.findById(existingId);

        // Assert
        verify(feeRepository).findById(existingId);
        assertThat(result.vehicleType()).isEqualTo(VehicleType.CATEGORY_I);
    }

    @Test
    void findById_nonExistingId_shouldThrowException() {
        // Arrange
        long nonExistingId = 2L;
        when(feeRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        FeeNotFoundException exception = assertThrows(FeeNotFoundException.class, () -> feeService.findById(nonExistingId));
        assertEquals("Fee object not found with id" + nonExistingId, exception.getMessage());
        verify(feeRepository).findById(nonExistingId);
    }

    @Test
    void removeById_ExistingId_ReturnsTrue() {
        // Arrange
        long existingId = 1L;
        when(feeRepository.existsById(existingId)).thenReturn(true);

        // Act
        boolean result = feeService.removeById(existingId);

        // Assert
        assertTrue(result);
        verify(feeRepository).deleteById(existingId);
    }

    @Test
    void removeById_NonExistingId_ReturnsFalse() {
        // Arrange
        long nonExistingId = 2L;
        when(feeRepository.existsById(nonExistingId)).thenReturn(false);

        // Act
        boolean result = feeService.removeById(nonExistingId);

        // Assert
        assertFalse(result);
        verify(feeRepository, never()).deleteById(anyLong());
    }

    @Test
    void saveFeeEntity() {
        // Arrange
        Fee fee = new Fee(6, VehicleType.CATEGORY_III);
        when(feeRepository.save(fee)).thenReturn(fee);

        // Act
        feeService.save(fee);

        // Assert
        verify(feeRepository).save(fee);
    }

    @Test
    void saveFeeRequest() {
        // Arrange
        FeeRequest feeRequest = new FeeRequest(2, VehicleType.CATEGORY_I, Boolean.TRUE);
        Fee savedFee = new Fee(feeRequest.quantity(), feeRequest.vehicleType());
        FeeDTO expectedFeeDTO = new FeeDTO(
                null,
                2,
                VehicleType.CATEGORY_I,
                Boolean.TRUE
        );

        when(feeRepository.save(any(Fee.class))).thenReturn(savedFee);
        when(feeDTOMapper.apply(savedFee)).thenReturn(expectedFeeDTO);

        // Act
        FeeDTO result = feeService.save(feeRequest);

        // Assert
        verify(feeRepository).save(any(Fee.class));
        assertThat(result.vehicleType()).isEqualTo(expectedFeeDTO.vehicleType());
        assertThat(result).isInstanceOf(FeeDTO.class).isEqualTo(expectedFeeDTO);
    }

    @Test
    void update_ExistingId_ReturnsUpdatedFeeDTO() {
        // Arrange
        long existingId = 1L;
        FeeRequest feeRequest = new FeeRequest(3, VehicleType.CATEGORY_II, Boolean.FALSE);
        Fee existingFee = new Fee(existingId, 3, VehicleType.CATEGORY_I, null, Boolean.TRUE);
        Fee updatedFee = new Fee(
                existingId,
                feeRequest.quantity(),
                feeRequest.vehicleType(),
                null,
                feeRequest.active());
        FeeDTO expectedPointDTO = new FeeDTO(
                updatedFee.getFeeId(),
                updatedFee.getQuantity(),
                updatedFee.getVehicleType(),
                updatedFee.getActive());

        when(feeRepository.findById(existingId)).thenReturn(Optional.of(existingFee));
        when(feeRepository.save(any(Fee.class))).thenReturn(updatedFee);
        when(feeDTOMapper.apply(updatedFee)).thenReturn(expectedPointDTO);

        // Act
        FeeDTO result = feeService.update(existingId, feeRequest);

        // Assert
        verify(feeRepository, times(1)).findById(existingId);
        verify(feeRepository, times(1)).save(any(Fee.class));
        assertThat(result.vehicleType()).isEqualTo(expectedPointDTO.vehicleType());
    }

    @Test
    void update_NonExistingId_ThrowsException() {
        // Arrange
        long nonExistingId = 2L;
        FeeRequest feeRequest = new FeeRequest(0, VehicleType.CATEGORY_II, true);
        when(feeRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(FeeNotFoundException.class, () -> feeService.update(nonExistingId, feeRequest));
        verify(feeRepository).findById(nonExistingId);
        verify(feeRepository, never()).save(any(Fee.class));
    }

    @Test
    void assignSection() {
        Section section = new Section("AP-8",
                100,
                true,
                null,
                null,
                null);
        Fee newFee = new Fee(5L, 23, VehicleType.CATEGORY_I, null, false);
        Fee savedFee = new Fee(5L, 23, VehicleType.CATEGORY_I, section, false);
        FeeDTO expectedFeeDTO = new FeeDTO(
                savedFee.getFeeId(),
                savedFee.getQuantity(),
                savedFee.getVehicleType(),
                savedFee.getActive()
        );

        when(feeRepository.findById(newFee.getFeeId())).thenReturn(Optional.of(newFee));
        when(feeRepository.save(newFee)).thenReturn(savedFee);
        when(feeDTOMapper.apply(savedFee)).thenReturn(expectedFeeDTO);

        // Act
        FeeDTO result = feeService.assignSection(newFee, section);

        // Assert
        verify(feeRepository, times(1)).save(any(Fee.class));
        verify(feeDTOMapper, times(1)).apply(any(Fee.class));
        assertThat(result.vehicleType()).isEqualTo(expectedFeeDTO.vehicleType());
    }
}