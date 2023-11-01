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

package eus.ehu.tfg.ktoda.tollsystem.user;

import eus.ehu.tfg.ktoda.tollsystem.address.Address;
import eus.ehu.tfg.ktoda.tollsystem.address.AddressService;
import eus.ehu.tfg.ktoda.tollsystem.address.dto.AddressDTO;
import eus.ehu.tfg.ktoda.tollsystem.telephone.Telephone;
import eus.ehu.tfg.ktoda.tollsystem.telephone.TelephoneService;
import eus.ehu.tfg.ktoda.tollsystem.telephone.dto.TelephoneDTO;
import eus.ehu.tfg.ktoda.tollsystem.transaction.Transaction;
import eus.ehu.tfg.ktoda.tollsystem.transaction.TransactionService;
import eus.ehu.tfg.ktoda.tollsystem.transaction.TransactionStatus;
import eus.ehu.tfg.ktoda.tollsystem.transaction.dto.TransactionDTO;
import eus.ehu.tfg.ktoda.tollsystem.user.dto.UserSimpleDTO;
import eus.ehu.tfg.ktoda.tollsystem.user.dto.UserSimpleDTOMapper;
import eus.ehu.tfg.ktoda.tollsystem.user.exception.UserAlreadyExistsException;
import eus.ehu.tfg.ktoda.tollsystem.user.exception.UserNotFoundException;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.Vehicle;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.VehicleService;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.dto.VehicleDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSimpleDTOMapper userDTOMapper;

    @Mock
    private TransactionService transactionService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private VehicleService vehicleService;

    @Mock
    private TelephoneService telephoneService;

    @Mock
    private AddressService addressService;

    @InjectMocks
    private UserServiceImpl userService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_ReturnsListOfUserDTO() {
        // Arrange
        User u1 = new User();
        User u2 = new User();
        u1.setFirstName("Test1");
        u2.setFirstName("Test2");
        List<User> users = Arrays.asList(
                u1,
                u2
        );
        when(userRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(users));
        when(userDTOMapper.apply(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return new UserSimpleDTO(
                    user.getUserId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    null,
                    null);
        });

        // Act
        List<UserSimpleDTO> result = userService.findAll();

        // Assert
        verify(userRepository).findAll(any(Pageable.class));
        verify(userDTOMapper, times(2)).apply(any(User.class));
        assertThat(result).hasSize(2);
        assertThat(result.get(0).firstName()).isEqualTo("Test1");
        assertThat(result.get(1).firstName()).isEqualTo("Test2");
    }

    @Test
    void findById_existingId_shouldReturnUserDTO() {
        // Arrange
        UUID existingId = UUID.randomUUID();
        User u1 = new User();
        u1.setUserId(existingId);
        u1.setFirstName("Test1");
        when(userRepository.findById(existingId)).thenReturn(Optional.of(u1));
        when(userDTOMapper.apply(u1)).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return new UserSimpleDTO(
                    user.getUserId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    null,
                    null);
        });

        // Act
        UserSimpleDTO result = userService.findById(existingId);

        // Assert
        verify(userRepository).findById(existingId);
        assertThat(result.firstName()).isEqualTo("Test1");
    }

    @Test
    void findById_nonExistingId_shouldThrowException() {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> userService.findById(nonExistingId));
        verify(userRepository).findById(nonExistingId);
    }

    @Test
    void removeById_ExistingId_ReturnsTrue() {
        // Arrange
        UUID existingId = UUID.randomUUID();
        when(userRepository.existsById(existingId)).thenReturn(true);

        // Act
        boolean result = userService.removeById(existingId);

        // Assert
        assertTrue(result);
        verify(userRepository).deleteById(existingId);
    }

    @Test
    void removeById_NonExistingId_ReturnsFalse() {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        when(userRepository.existsById(nonExistingId)).thenReturn(false);

        // Act
        boolean result = userService.removeById(nonExistingId);

        // Assert
        assertFalse(result);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void saveUserEntity() {
        // Arrange
        User user = new User();
        when(userRepository.save(user)).thenReturn(user);

        // Act
        userService.save(user);

        // Assert
        verify(userRepository).save(user);
    }

    @Test
    void saveUserRequest() {
        // Arrange
        UserRequest userRequest = new UserRequest(
                "T1",
                null,
                null,
                null);
        User savedUser = new User(
                userRequest.firstName(),
                userRequest.lastName(),
                userRequest.password(),
                userRequest.email(),
                null
        );
        UserSimpleDTO expectedUserDTO = new UserSimpleDTO(
                savedUser.getUserId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                null,
                null
        );

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userDTOMapper.apply(savedUser)).thenReturn(expectedUserDTO);

        // Act
        UserSimpleDTO result = userService.save(userRequest);

        // Assert
        verify(userRepository).save(any(User.class));
        assertThat(result.firstName()).isEqualTo(expectedUserDTO.firstName());
        assertThat(result).isInstanceOf(UserSimpleDTO.class).isEqualTo(expectedUserDTO);
    }

    @Test
    void saveUserRequest_shouldThrowException() {
        // Arrange
        UserRequest userRequest = new UserRequest(
                "T1",
                null,
                null,
                null);

        when(userRepository.existsByEmail(userRequest.email()))
                .thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> userService.save(userRequest));
    }

    @Test
    void update_ExistingId_ReturnsUpdatedUserDTO() {
        // Arrange
        UUID existingId = UUID.randomUUID();
        UserRequest userRequest = new UserRequest(
                "T1",
                null,
                null,
                null);
        User existingUser = new User(
                existingId, "T2", null,
                null, null,
                null, null, null,
                null, null, null, null, null);
        User updatedUser = new User(
                userRequest.firstName(), userRequest.lastName(),
                userRequest.password(), userRequest.email(), null);
        UserSimpleDTO expectedUserDTO = new UserSimpleDTO(
                updatedUser.getUserId(),
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getEmail(),
                null,
                null
        );

        when(userRepository.findById(existingId)).thenReturn(Optional.of(existingUser));
//        when(passwordEncoder.encode(any()));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userDTOMapper.apply(updatedUser)).thenReturn(expectedUserDTO);

        // Act
        UserSimpleDTO result = userService.update(existingId, userRequest);

        // Assert
        verify(userRepository, times(1)).findById(existingId);
        verify(userRepository, times(1)).save(any(User.class));
        assertThat(result.firstName()).isEqualTo(expectedUserDTO.firstName());
    }

    @Test
    void update_NonExistingId_ThrowsException() {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        UserRequest userRequest = new UserRequest(
                "T1",
                null,
                null,
                null);
        when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> userService.update(nonExistingId, userRequest));
        verify(userRepository).findById(nonExistingId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void assignAddress() {
        // Arrange
        User user = new User();
        Address address = new Address();
        AddressDTO addressDTO = new AddressDTO(
                address.getAddressId(),
                address.getProvince(),
                address.getCity(),
                address.getStreet(),
                address.getNumber()
        );
        UserSimpleDTO expectedDTO = new UserSimpleDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                null,
                null
        );

        // Mock the behavior of the repositories and services
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userDTOMapper.apply(user)).thenReturn(expectedDTO);
        doNothing().when(addressService).save(address);

        // Act
        UserSimpleDTO result = userService.assignAddress(user, address);

        // Assert
        verify(userRepository).save(user);
        verify(userDTOMapper).apply(user);
        verify(addressService).save(address);
        assertThat(result).isEqualTo(expectedDTO);
    }

    @Test
    void addTransaction() {
        // Arrange
        User user = new User();
        Transaction transaction = new Transaction(
                0L,
                null,
                null,
                2,
                new Timestamp(System.currentTimeMillis()),
                TransactionStatus.PENDING
        );
        TransactionDTO transactionDTO = new TransactionDTO(
                transaction.getTransactionId(),
                transaction.getAmount(),
                transaction.getTimestamp(),
                transaction.getStatus(),
                null
        );
        List<TransactionDTO> transactionDTOS = List.of(transactionDTO);
        UserSimpleDTO expectedDTO = new UserSimpleDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                null,
                null
        );

        // Mock the behavior of the repositories and services
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userDTOMapper.apply(user)).thenReturn(expectedDTO);
        doNothing().when(transactionService).save(transaction);

        // Act
        UserSimpleDTO result = userService.addTransaction(user, transaction);

        // Assert
        verify(userRepository).save(user);
        verify(userDTOMapper).apply(user);
        verify(transactionService).save(transaction);
        assertThat(result).isEqualTo(expectedDTO);
    }

    @Test
    void removeTransaction() {
        // Arrange
        User user = new User();
        Transaction transaction = new Transaction();
        UserSimpleDTO expectedDTO = new UserSimpleDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                null,
                null
        );

        // Mock the behavior of the repositories and services
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userDTOMapper.apply(user)).thenReturn(expectedDTO);
        doNothing().when(transactionService).save(transaction);

        // Act
        UserSimpleDTO result = userService.removeTransaction(user, transaction);

        // Assert
        verify(userRepository).save(user);
        verify(userDTOMapper).apply(user);
        verify(transactionService).save(transaction);
        assertThat(result).isEqualTo(expectedDTO);
    }

    @Test
    void addTelephone() {
        // Arrange
        User user = new User();
        Telephone telephone = new Telephone();
        TelephoneDTO telephoneDTO = new TelephoneDTO(
                telephone.getTelephoneId(),
                telephone.getNumber()
        );
        List<TelephoneDTO> telephoneDTOS = List.of(telephoneDTO);
        UserSimpleDTO expectedDTO = new UserSimpleDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                null,
                null
        );

        // Mock the behavior of the repositories and services

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userDTOMapper.apply(user)).thenReturn(expectedDTO);
        doNothing().when(telephoneService).save(telephone);

        // Act
        UserSimpleDTO result = userService.addTelephone(user, telephone);

        // Assert
        verify(userRepository).save(user);
        verify(userDTOMapper).apply(user);
        verify(telephoneService).save(telephone);
        assertThat(result).isEqualTo(expectedDTO);
    }

    @Test
    void removeTelephone() {
        // Arrange
        User user = new User();
        Telephone telephone = new Telephone();
        UserSimpleDTO expectedDTO = new UserSimpleDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                null,
                List.of()
        );

        // Mock the behavior of the repositories and services

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userDTOMapper.apply(user)).thenReturn(expectedDTO);
        doNothing().when(telephoneService).save(telephone);

        // Act
        UserSimpleDTO result = userService.removeTelephone(user, telephone);

        // Assert
        verify(userRepository).save(user);
        verify(userDTOMapper).apply(user);
        verify(telephoneService).save(telephone);
        assertThat(result).isEqualTo(expectedDTO);
        assertThat(result.telephones()).isEmpty();
    }

    @Test
    void addVehicle() {
        // Arrange
        User user = new User();
        Vehicle vehicle = new Vehicle();
        VehicleDTO telephoneDTO = new VehicleDTO(
                vehicle.getLicensePlate(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getType()
        );
        List<VehicleDTO> vehicleDTOS = List.of(telephoneDTO);
        UserSimpleDTO expectedDTO = new UserSimpleDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                null,
                null
        );

        // Mock the behavior of the repositories and services
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userDTOMapper.apply(user)).thenReturn(expectedDTO);
        doNothing().when(vehicleService).save(vehicle);

        // Act
        UserSimpleDTO result = userService.addVehicle(user, vehicle);

        // Assert
        verify(userRepository).save(user);
        verify(userDTOMapper).apply(user);
        assertThat(result).isEqualTo(expectedDTO);
    }

    @Test
    void removeVehicle() {
        // Arrange
        User user = new User();
        Vehicle vehicle = new Vehicle();
        UserSimpleDTO expectedDTO = new UserSimpleDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                null,
                null
        );

        // Mock the behavior of the repositories and services
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userDTOMapper.apply(user)).thenReturn(expectedDTO);
        doNothing().when(vehicleService).save(vehicle);

        // Act
        UserSimpleDTO result = userService.removeVehicle(user, vehicle);

        // Assert
        verify(userRepository).save(user);
        verify(userDTOMapper).apply(user);
        verify(vehicleService).save(vehicle);
        assertThat(result).isEqualTo(expectedDTO);
    }
}