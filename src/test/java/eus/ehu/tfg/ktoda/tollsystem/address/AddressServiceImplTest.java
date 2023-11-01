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

package eus.ehu.tfg.ktoda.tollsystem.address;

import eus.ehu.tfg.ktoda.tollsystem.address.dto.AddressDTO;
import eus.ehu.tfg.ktoda.tollsystem.address.dto.AddressDTOMapper;
import eus.ehu.tfg.ktoda.tollsystem.address.exception.AddressNotFoundException;
import eus.ehu.tfg.ktoda.tollsystem.point.PointRequest;
import eus.ehu.tfg.ktoda.tollsystem.point.PointType;
import eus.ehu.tfg.ktoda.tollsystem.point.dto.PointDTO;
import eus.ehu.tfg.ktoda.tollsystem.user.User;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.Vehicle;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.VehicleType;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.dto.VehicleDTO;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AddressServiceImplTest {
    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressDTOMapper addressDTOMapper;

    @InjectMocks
    private AddressServiceImpl addressService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_ReturnsListOfAddressDTO() {
        // Arrange
        List<Address> addresses = Arrays.asList(
                new Address(null, "San Sebastian", null, null),
                new Address(null, "Lasarte-Oria", null, null)
        );
        when(addressRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(addresses));
        when(addressDTOMapper.apply(any(Address.class))).thenAnswer(invocation -> {
            Address address = invocation.getArgument(0);
            return new AddressDTO(
                    address.getAddressId(),
                    address.getProvince(),
                    address.getCity(),
                    address.getStreet(),
                    address.getNumber());
        });

        // Act
        List<AddressDTO> result = addressService.findAll();

        // Assert
        verify(addressRepository).findAll(any(Pageable.class));
        assertThat(result).hasSize(2);
        System.out.println(result);
        assertThat(result.get(0).city()).isEqualTo("San Sebastian");
        assertThat(result.get(1).city()).isEqualTo("Lasarte-Oria");
    }

    @Test
    void findById_existingId_shouldReturnAddressDTO() {
        // Arrange
        long existingId = 1L;
        Address existingAddress = new Address(null, "Donostia", null, null);
        when(addressRepository.findById(existingId)).thenReturn(Optional.of(existingAddress));
        when(addressDTOMapper.apply(existingAddress)).thenReturn(
                new AddressDTO(
                        existingAddress.getAddressId(),
                        existingAddress.getProvince(),
                        existingAddress.getCity(),
                        existingAddress.getStreet(),
                        existingAddress.getNumber())
        );

        // Act
        AddressDTO result = addressService.findById(existingId);

        // Assert
        verify(addressRepository).findById(existingId);
        assertThat(result.city()).isEqualTo("Donostia");
    }

    @Test
    void findById_nonExistingId_shouldThrowException() {
        // Arrange
        long nonExistingId = 2L;
        when(addressRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AddressNotFoundException.class,
                () -> addressService.findById(nonExistingId));
        verify(addressRepository).findById(nonExistingId);
    }

    @Test
    void removeById_ExistingId_ReturnsTrue() {
        // Arrange
        long existingId = 1L;
        when(addressRepository.existsById(existingId)).thenReturn(true);

        // Act
        boolean result = addressService.removeById(existingId);

        // Assert
        assertTrue(result);
        verify(addressRepository).deleteById(existingId);
    }

    @Test
    void removeById_NonExistingId_ReturnsFalse() {
        // Arrange
        long nonExistingId = 2L;
        when(addressRepository.existsById(nonExistingId)).thenReturn(false);

        // Act
        boolean result = addressService.removeById(nonExistingId);

        // Assert
        assertFalse(result);
        verify(addressRepository, never()).deleteById(anyLong());
    }

    @Test
    void saveAddressEntity() {
        // Arrange
        Address address = new Address();
        when(addressRepository.save(address)).thenReturn(address);

        // Act
        addressService.save(address);

        // Assert
        verify(addressRepository).save(address);
    }

    @Test
    void savePointRequest() {
        // Arrange
        AddressRequest addressRequest = new AddressRequest(
                null, "Donostia", null, null);
        Address address = new Address(
                addressRequest.province(), addressRequest.city(), addressRequest.street(), addressRequest.number());
        AddressDTO expectedAddressDTO = new AddressDTO(
                address.getAddressId(),
                address.getProvince(),
                address.getCity(),
                address.getStreet(),
                address.getNumber()
        );

        when(addressRepository.save(any(Address.class))).thenReturn(address);
        when(addressDTOMapper.apply(address)).thenReturn(expectedAddressDTO);

        // Act
        AddressDTO result = addressService.save(addressRequest);

        // Assert
        verify(addressRepository).save(any(Address.class));
        assertThat(result.city()).isEqualTo(expectedAddressDTO.city());
        assertThat(result).isInstanceOf(AddressDTO.class).isEqualTo(expectedAddressDTO);
    }

    @Test
    void update_ExistingId_ReturnsUpdatedAddressDTO() {
        // Arrange
        long existingId = 1L;
        AddressRequest addressRequest = new AddressRequest(
                null, "Donostia", null, null);
        Address existingAddress = new Address(
                existingId, null, null, "Donostia", null, null);
        Address updatedAddress = new Address(
                existingId, null, addressRequest.province(),
                addressRequest.city(), addressRequest.street(), addressRequest.number());
        AddressDTO expectedAddressDTO = new AddressDTO(
                updatedAddress.getAddressId(),
                updatedAddress.getProvince(),
                updatedAddress.getCity(),
                updatedAddress.getStreet(),
                updatedAddress.getNumber());

        when(addressRepository.findById(existingId)).thenReturn(Optional.of(existingAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(updatedAddress);
        when(addressDTOMapper.apply(updatedAddress)).thenReturn(expectedAddressDTO);

        // Act
        AddressDTO result = addressService.update(existingId, addressRequest);

        // Assert
        verify(addressRepository, times(1)).findById(existingId);
        verify(addressRepository, times(1)).save(any(Address.class));
        assertThat(result.city()).isEqualTo(expectedAddressDTO.city());
    }

    @Test
    void update_NonExistingId_ThrowsException() {
        // Arrange
        long nonExistingId = 2L;
        AddressRequest addressRequest = new AddressRequest(
                null, "Donostia", null, null);
        when(addressRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AddressNotFoundException.class, () -> addressService.update(nonExistingId, addressRequest));
        verify(addressRepository).findById(nonExistingId);
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void assignUser() {
        // Arrange
        Address address = new Address(
                null,
                null,
                null,
                null
        );
        User user = new User(
                new UUID(2, 1),
                "Test",
                "Transaction"
                , "12a@A!asd",
                "test@test.com",
                null, true,null
                , null,
                null, null, null,null);
        Address savedAddress = new Address(
                0L,
                user,
                address.getProvince(),
                address.getCity(),
                address.getStreet(),
                address.getNumber()
        );
        AddressDTO expectedDTO = new AddressDTO(
                savedAddress.getAddressId(),
                savedAddress.getProvince(),
                savedAddress.getCity(),
                savedAddress.getStreet(),
                savedAddress.getNumber()
        );

        when(addressRepository.save(address)).thenReturn(savedAddress);
        when(addressDTOMapper.apply(savedAddress)).thenReturn(expectedDTO);

        // Act & Assert
        AddressDTO result = addressService.assignUser(address, user);
        assertThat(result).isEqualTo(expectedDTO);
        verify(addressDTOMapper, times(1)).apply(any(Address.class));
    }
}