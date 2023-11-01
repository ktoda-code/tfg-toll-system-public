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

import eus.ehu.tfg.ktoda.tollsystem.user.User;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.dto.VehicleDTO;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.dto.VehicleDTOMapper;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.exception.VehicleAlreadyExistsException;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.exception.VehicleNotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VehicleServiceImplTest {
    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleDTOMapper vehicleDTOMapper;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_ReturnsListOfTransactionDTO() {
        List<Vehicle> vehicles = List.of(
                new Vehicle("23", null, null, null),
                new Vehicle("145", null, null, null)
        );
        when(vehicleRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(vehicles));
        when(vehicleDTOMapper.apply(any(Vehicle.class))).thenAnswer(invocation -> {
            Vehicle vehicle = invocation.getArgument(0);
            return new VehicleDTO(
                    vehicle.getLicensePlate(),
                    vehicle.getBrand(),
                    vehicle.getModel(),
                    vehicle.getType()
            );
        });

        // Act
        List<VehicleDTO> result = vehicleService.findAll();

        // Assert
        verify(vehicleRepository).findAll(any(Pageable.class));
        verify(vehicleDTOMapper, times(2)).apply(any(Vehicle.class));
        assertThat(result).hasSize(2);
        assertThat(result.get(0).licensePlate()).isEqualTo("23");
        assertThat(result.get(1).licensePlate()).isEqualTo("145");
    }

    @Test
    void findById_existingId_shouldReturnSectionDTO() {
        // Arrange
        String existingId = "150";
        Vehicle existingVehicle = new Vehicle(
                existingId,
                null,
                null,
                null
        );
        when(vehicleRepository.findById(existingId)).thenReturn(Optional.of(existingVehicle));
        when(vehicleDTOMapper.apply(existingVehicle)).thenAnswer(invocation -> {
            Vehicle vehicle = invocation.getArgument(0);
            return new VehicleDTO(
                    vehicle.getLicensePlate(),
                    vehicle.getBrand(),
                    vehicle.getModel(),
                    vehicle.getType()
            );
        });

        // Act
        VehicleDTO result = vehicleService.findById(existingId);

        // Assert
        verify(vehicleRepository).findById(existingId);
        assertThat(result.licensePlate()).isEqualTo("150");
    }

    @Test
    void findById_nonExistingId_shouldThrowException() {
        // Arrange
        String nonExistingId = "999L";
        when(vehicleRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        VehicleNotFoundException exception = assertThrows(VehicleNotFoundException.class,
                () -> vehicleService.findById(nonExistingId));
        assertEquals("Vehicle not found with id " + nonExistingId, exception.getMessage());
        verify(vehicleRepository).findById(nonExistingId);
    }

    @Test
    void removeById_ExistingId_ReturnsTrue() {
        // Arrange
        String existingId = "1L";
        when(vehicleRepository.existsById(existingId)).thenReturn(true);

        // Act
        boolean result = vehicleService.removeById(existingId);

        // Assert
        assertTrue(result);
        verify(vehicleRepository).deleteById(existingId);
        verify(vehicleRepository, times(1)).existsById(existingId);
    }

    @Test
    void removeById_NonExistingId_ReturnsFalse() {
        // Arrange
        String nonExistingId = "-1L";
        when(vehicleRepository.existsById(nonExistingId)).thenReturn(false);

        // Act
        boolean result = vehicleService.removeById(nonExistingId);

        // Assert
        assertFalse(result);
        verify(vehicleRepository, never()).deleteById(anyString());
    }

    @Test
    void saveVehicleEntity() {
        // Arrange
        Vehicle vehicle = new Vehicle();
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);

        // Act
        vehicleService.save(vehicle);

        // Assert
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void saveVehicleRequest() {
        // Arrange
        VehicleRequest vehicleRequest = new VehicleRequest(
                "14", null, null, null);
        Vehicle savedVehicle = new Vehicle(
                vehicleRequest.licensePlate(),
                vehicleRequest.brand(),
                vehicleRequest.model(),
                vehicleRequest.vehicleType()
        );
        VehicleDTO expectedVehicleDTO = new VehicleDTO(
                "14",
                null,
                null,
                null
        );

        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(savedVehicle);
        when(vehicleDTOMapper.apply(savedVehicle)).thenReturn(expectedVehicleDTO);

        // Act
        VehicleDTO result = vehicleService.save(vehicleRequest);

        // Assert
        verify(vehicleRepository).save(any(Vehicle.class));
        assertThat(result.licensePlate()).isEqualTo(expectedVehicleDTO.licensePlate());
        assertThat(result).isInstanceOf(VehicleDTO.class).isEqualTo(expectedVehicleDTO);
    }

    @Test
    void saveVehicleRequest_shouldThrowException() {
        // Arrange
        VehicleRequest vehicleRequest = new VehicleRequest(
                "14", null, null, null);

        when(vehicleRepository.existsById(vehicleRequest.licensePlate()))
                .thenReturn(true);

        // Act & Assert
        assertThrows(VehicleAlreadyExistsException.class, () -> vehicleService.save(vehicleRequest));
    }

    @Test
    void update_ExistingId_ReturnsUpdatedSectionDTO() {
        // Arrange
        String existingId = "existingId";
        VehicleRequest vehicleRequest = new VehicleRequest(existingId,
                null, null, null);
        Vehicle existingVehicle = new Vehicle(existingId, null, null, null);
        Vehicle updatedVehicle = new Vehicle(
                vehicleRequest.licensePlate(),
                vehicleRequest.brand(),
                vehicleRequest.model(),
                vehicleRequest.vehicleType());
        VehicleDTO expectedVehicleDTO = new VehicleDTO(
                updatedVehicle.getLicensePlate(),
                updatedVehicle.getBrand(),
                updatedVehicle.getModel(),
                updatedVehicle.getType()
        );

        when(vehicleRepository.findById(existingId)).thenReturn(Optional.of(existingVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(updatedVehicle);
        when(vehicleDTOMapper.apply(updatedVehicle)).thenReturn(expectedVehicleDTO);

        // Act
        VehicleDTO result = vehicleService.update(existingId, vehicleRequest);

        // Assert
        verify(vehicleRepository, times(1)).findById(existingId);
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
        assertThat(result.licensePlate()).isEqualTo(expectedVehicleDTO.licensePlate());
    }

    @Test
    void update_NonExistingId_ThrowsException() {
        // Arrange
        String nonExistingId = "nonExistingId";
        VehicleRequest vehicleRequest = new VehicleRequest(nonExistingId,
                null, null, null);
        when(vehicleRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(VehicleNotFoundException.class,
                () -> vehicleService.update(nonExistingId, vehicleRequest));
        verify(vehicleRepository).findById(nonExistingId);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }


    @Test
    void assignUser() {
        // Arrange
        Vehicle vehicle = new Vehicle(
                "2155GHJ",
                "VW",
                null,
                VehicleType.CATEGORY_I
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
        Vehicle savedVehicle = new Vehicle(
                "2155GHJ",
                "VW",
                null,
                VehicleType.CATEGORY_I,
                user
        );
        VehicleDTO expectedDTO = new VehicleDTO(
                savedVehicle.getLicensePlate(),
                savedVehicle.getBrand(),
                savedVehicle.getModel(),
                savedVehicle.getType()
        );

        when(vehicleRepository.save(vehicle)).thenReturn(savedVehicle);
        when(vehicleDTOMapper.apply(savedVehicle)).thenReturn(expectedDTO);

        // Act & Assert
        VehicleDTO result = vehicleService.assignUser(vehicle, user);
        assertThat(result).isEqualTo(expectedDTO);
        verify(vehicleDTOMapper, times(1)).apply(any(Vehicle.class));
    }
}