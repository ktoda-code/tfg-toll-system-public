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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class VehicleServiceImpl implements VehicleService {
    private VehicleRepository vehicleRepository;
    private VehicleDTOMapper vehicleDTOMapper;

    @Override
    public List<VehicleDTO> findAll() {
        int pageInit = 0;
        int pageSize = 8;
        Pageable page = PageRequest.of(pageInit, pageSize);
        return vehicleRepository
                .findAll(page)
                .stream()
                .map(vehicleDTOMapper)
                .toList();
    }

    @Override
    public Vehicle findByDTO(VehicleDTO vehicleDTO) {
        return vehicleRepository
                .findById(vehicleDTO.licensePlate())
                .orElseThrow(
                        () -> new VehicleNotFoundException("Vehicle not found with id " + vehicleDTO.licensePlate()));
    }

    @Override
    public VehicleDTO findById(String id) {
        Optional<VehicleDTO> vehicleDTO = vehicleRepository
                .findById(id)
                .map(vehicleDTOMapper);
        return vehicleDTO.orElseThrow(
                () -> new VehicleNotFoundException("Vehicle not found with id " + id));
    }

    @Override
    public boolean removeById(String id) {
        if (vehicleRepository.existsById(id)) {
            vehicleRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public void save(Vehicle vehicle) {
        String lp = vehicle.getLicensePlate();
        if (vehicleRepository.existsById(lp)) {
            throw new VehicleAlreadyExistsException("Vehicle already exists with this LP " + lp);
        }
        vehicleRepository.save(vehicle);
    }

    @Override
    public VehicleDTO save(VehicleRequest vehicleRequest) {
        if (vehicleRepository.existsById(vehicleRequest.licensePlate())) {
            throw new VehicleAlreadyExistsException("Vehicle already exists with this license plate " + vehicleRequest.licensePlate());
        }
        Vehicle vehicle = new Vehicle(
                vehicleRequest.licensePlate(),
                vehicleRequest.brand(),
                vehicleRequest.model(),
                vehicleRequest.vehicleType()
        );
        log.info("Creating object: " + vehicle);
        return vehicleDTOMapper.apply(
                vehicleRepository.save(vehicle)
        );
    }

    @Override
    public VehicleDTO update(String id, VehicleRequest vehicleRequest) {
        log.info("Trying to modify: " + id);
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() ->
                        new VehicleNotFoundException("Vehicle not found with id " + id));
        vehicle.setLicensePlate(vehicleRequest.licensePlate());
        vehicle.setBrand(vehicleRequest.brand());
        vehicle.setModel(vehicleRequest.model());
        vehicle.setType(vehicleRequest.vehicleType());
        log.info("Modifying object " + vehicle);
        return vehicleDTOMapper.apply(
                vehicleRepository.save(vehicle)
        );
    }

    @Override
    public VehicleDTO assignUser(Vehicle vehicle, User user) {
        vehicle.setUser(user);
        return vehicleDTOMapper.apply(
                vehicleRepository.save(vehicle)
        );
    }

    @Override
    public Vehicle findByLP(String licensePlate) {
        return vehicleRepository.findById(licensePlate).orElseThrow(VehicleNotFoundException::new);
    }
}
