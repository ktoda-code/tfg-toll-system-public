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
import eus.ehu.tfg.ktoda.tollsystem.pointlog.PointLog;
import eus.ehu.tfg.ktoda.tollsystem.telephone.Telephone;
import eus.ehu.tfg.ktoda.tollsystem.telephone.TelephoneService;
import eus.ehu.tfg.ktoda.tollsystem.transaction.Transaction;
import eus.ehu.tfg.ktoda.tollsystem.transaction.TransactionService;
import eus.ehu.tfg.ktoda.tollsystem.user.dto.UserProfileUpdateDTO;
import eus.ehu.tfg.ktoda.tollsystem.user.dto.UserSimpleDTO;
import eus.ehu.tfg.ktoda.tollsystem.user.dto.UserSimpleDTOMapper;
import eus.ehu.tfg.ktoda.tollsystem.user.exception.UserAlreadyExistsException;
import eus.ehu.tfg.ktoda.tollsystem.user.exception.UserNotFoundException;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.Vehicle;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.VehicleRequest;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.VehicleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private UserSimpleDTOMapper userSimpleDTOMapper;
    private TransactionService transactionService;
    private VehicleService vehicleService;
    private TelephoneService telephoneService;
    private AddressService addressService;
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public List<UserSimpleDTO> findAll() {
        int pageInit = 0;
        int pageSize = 18;
        Pageable page = PageRequest.of(pageInit, pageSize);
        return userRepository
                .findAll(page)
                .stream()
                .map(userSimpleDTOMapper)
                .toList();
    }

    @Override
    public User findByDTO(UserSimpleDTO userDTO) {
        return userRepository
                .findById(userDTO.userId())
                .orElseThrow(
                        () -> new UserNotFoundException("User not found with id " + userDTO.userId()));
    }

    @Override
    @Transactional
    public UserSimpleDTO findById(UUID id) {
        Optional<UserSimpleDTO> userDTO = userRepository
                .findById(id)
                .map(userSimpleDTOMapper);
        return userDTO.orElseThrow(
                () -> new UserNotFoundException("User not found with id " + id));
    }

    @Override
    @Transactional
    public UserSimpleDTO findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userSimpleDTOMapper)
                .orElseThrow(
                        () -> new UserNotFoundException("User not found with email " + email));
    }

    @Override
    @Transactional
    public boolean removeById(UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public void register(User user) {
        String email = user.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User already exists with this email ");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserSimpleDTO save(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.email())) {
            throw new UserAlreadyExistsException("User already exists with email " + userRequest.email());
        }
        User user = new User(
                userRequest.firstName(),
                userRequest.lastName(),
                userRequest.password(),
                userRequest.email(),
                new Timestamp(System.currentTimeMillis())
        );
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.info("Creating object: " + user);
        return userSimpleDTOMapper.apply(
                userRepository.save(user)
        );
    }

    @Override
    @Transactional
    public UserSimpleDTO update(UUID id, UserRequest userRequest) {
        log.info("Trying to modify: " + id);
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id " + id));
        user.setEmail(userRequest.email());
        user.setFirstName(userRequest.firstName());
        user.setLastName(userRequest.lastName());
        user.setPassword(passwordEncoder.encode(userRequest.password()));
        log.info("Modifying object " + user);
        return userSimpleDTOMapper.apply(
                userRepository.save(user)
        );
    }

    @Override
    @Transactional
    public UserSimpleDTO assignAddress(User user, Address address) {
        // Fetch the section again within this transactional boundary
        User managedUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + user.getUserId()));

        managedUser.setAddress(address);
        address.setUser(managedUser);
        addressService.save(address);
        return userSimpleDTOMapper.apply(
                userRepository.save(managedUser)
        );
    }

    @Override
    @Transactional
    public UserSimpleDTO addVehicle(User user, Vehicle vehicle) {
        // Fetch the section again within this transactional boundary
        User managedUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + user.getUserId()));

        managedUser.addVehicle(vehicle);
        vehicle.setUser(managedUser);
        vehicleService.update(vehicle.getLicensePlate(),
                new VehicleRequest(vehicle.getLicensePlate(), vehicle.getBrand(), vehicle.getModel(), vehicle.getType()));
        return userSimpleDTOMapper.apply(
                userRepository.save(managedUser)
        );
    }

    @Override
    @Transactional
    public UserSimpleDTO removeVehicle(User user, Vehicle vehicle) {
        // Fetch the section again within this transactional boundary
        User managedUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + user.getUserId()));

        managedUser.removeVehicle(vehicle);
        vehicle.setUser(null);
        vehicleService.save(vehicle);
        return userSimpleDTOMapper.apply(
                userRepository.save(managedUser)
        );
    }

    @Override
    @Transactional
    public UserSimpleDTO addTelephone(User user, Telephone telephone) {
        // Fetch the section again within this transactional boundary
        User managedUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + user.getUserId()));

        managedUser.addTelephone(telephone);
        telephone.setUser(managedUser);
        telephoneService.save(telephone);
        return userSimpleDTOMapper.apply(
                userRepository.save(managedUser)
        );
    }

    @Override
    @Transactional
    public UserSimpleDTO removeTelephone(User user, Telephone telephone) {
        // Fetch the section again within this transactional boundary
        User managedUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + user.getUserId()));

        managedUser.removeTelephone(telephone);
        telephone.setUser(null);
        telephoneService.save(telephone);
        return userSimpleDTOMapper.apply(
                userRepository.save(managedUser)
        );
    }

    @Override
    @Transactional
    public UserSimpleDTO addTransaction(User user, Transaction transaction) {
        // Fetch the section again within this transactional boundary
        User managedUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + user.getUserId()));

        managedUser.addTransaction(transaction);
        transaction.setUser(managedUser);
        transactionService.save(transaction);
        return userSimpleDTOMapper.apply(
                userRepository.save(managedUser)
        );
    }

    @Override
    @Transactional
    public UserSimpleDTO removeTransaction(User user, Transaction transaction) {
        // Fetch the section again within this transactional boundary
        User managedUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + user.getUserId()));

        managedUser.removeTransaction(transaction);
        transaction.setUser(null);
        transactionService.save(transaction);
        return userSimpleDTOMapper.apply(
                userRepository.save(managedUser)
        );
    }

    @Override
    @Transactional
    public UserSimpleDTO addLog(User user, PointLog log) {
        // Fetch the section again within this transactional boundary
        User managedUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + user.getUserId()));

        managedUser.addLog(log);
        return userSimpleDTOMapper.apply(
                userRepository.save(managedUser)
        );
    }

    @Override
    @Transactional
    public UserSimpleDTO removeLog(User user, PointLog log) {
        // Fetch the section again within this transactional boundary
        User managedUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + user.getUserId()));

        managedUser.removeLog(log);
        return userSimpleDTOMapper.apply(
                userRepository.save(managedUser)
        );
    }

    @Override
    @Transactional
    public User findUserByLicensePlate(String licencePlate) {
        return userRepository
                .findUserByLicensePlate(licencePlate)
                .orElseThrow(() -> new UserNotFoundException("User not found with license plate: " + licencePlate));
    }

    @Override
    public User findByUUID(UUID userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    @Override
    @Transactional
    public void update(String email, UserProfileUpdateDTO userProfileUpdateDTO) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User not found with email"));

        log.info("User: {}", user);
        log.info("User: {}", userProfileUpdateDTO);
        user.setFirstName(userProfileUpdateDTO.getFirstName());
        user.setLastName(userProfileUpdateDTO.getLastName());
        user.setEmail(userProfileUpdateDTO.getEmail());
        log.info("Password: {}", userProfileUpdateDTO.getPassword());
        log.info("Password bcrypt: {}", passwordEncoder.encode(userProfileUpdateDTO.getPassword()));
        user.setPassword(passwordEncoder.encode(userProfileUpdateDTO.getPassword()));
        log.info("Update User: {}", user);

        Address address = user.getAddress();
        log.info("Address: {}", address);

        address.setCity(userProfileUpdateDTO.getCity());
        address.setStreet(userProfileUpdateDTO.getStreet());
        address.setProvince(userProfileUpdateDTO.getProvince());
        address.setNumber(userProfileUpdateDTO.getAddressNumber());
        log.info("Update Address: {}", address);

        Vehicle vehicle = user.getVehicles().get(0);
        log.info("Vehicle: {}", vehicle);
        if (!userProfileUpdateDTO.getLicensePlate().equals(vehicle.getLicensePlate())) {
            user.removeVehicle(vehicle);
            vehicleService.removeById(vehicle.getLicensePlate());
            vehicle.setLicensePlate(userProfileUpdateDTO.getLicensePlate());
            vehicle.setModel(userProfileUpdateDTO.getModel());
            vehicle.setBrand(userProfileUpdateDTO.getBrand());
            vehicle.setUser(user);
            vehicle.setType(userProfileUpdateDTO.getVehicleType());
            log.info("Update Vehicle: {}", vehicle);
            vehicleService.save(vehicle);
        }


        if (!user.getTelephones().isEmpty()) {
            Telephone telephone = user.getTelephones().get(0);
            telephone.setNumber(userProfileUpdateDTO.getTelephoneNumber());
        }

        if (userProfileUpdateDTO.getTelephoneNumber() != null) {
            Telephone newTel = new Telephone(userProfileUpdateDTO.getTelephoneNumber());
            newTel.setUser(user);
            user.addTelephone(newTel);
            telephoneService.save(newTel);
        }

        userRepository.save(user);
        addressService.save(address);

    }
}
