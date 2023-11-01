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
import eus.ehu.tfg.ktoda.tollsystem.user.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class AddressServiceImpl implements AddressService {
    private AddressRepository addressRepository;
    private AddressDTOMapper addressDTOMapper;

    @Override
    public List<AddressDTO> findAll() {
        int pageInit = 0;
        int pageSize = 8;
        Pageable page = PageRequest.of(pageInit, pageSize);
        return addressRepository
                .findAll(page)
                .stream()
                .map(addressDTOMapper)
                .toList();
    }

    @Override
    public Address findByDTO(AddressDTO addressDTO) {
        return addressRepository
                .findById(addressDTO.addressId())
                .orElseThrow(AddressNotFoundException::new);
    }

    @Override
    public AddressDTO findById(Long id) {
        Optional<AddressDTO> addressDTO = addressRepository
                .findById(id)
                .map(addressDTOMapper);
        return addressDTO.orElseThrow(
                () -> new AddressNotFoundException("Address not found with id " + id));
    }

    @Override
    public boolean removeById(Long id) {
        if (addressRepository.existsById(id)) {
            addressRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public void save(Address address) {
        addressRepository.save(address);
    }

    @Override
    public AddressDTO save(AddressRequest addressRequest) {
        Address address = new Address(
                addressRequest.province(),
                addressRequest.city(),
                addressRequest.street(),
                addressRequest.number()
        );
        log.info("Creating object: " + address);
        return addressDTOMapper.apply(
                addressRepository.save(address)
        );
    }

    @Override
    public AddressDTO update(Long id, AddressRequest addressRequest) {
        log.info("Trying to modify: " + id);
        Address address = addressRepository.findById(id)
                .orElseThrow(() ->
                        new AddressNotFoundException("Address not found with id " + id));
        address.setProvince(addressRequest.province());
        address.setCity(addressRequest.city());
        address.setStreet(addressRequest.street());
        address.setNumber(addressRequest.number());
        log.info("Modifying object " + address);
        return addressDTOMapper.apply(
                addressRepository.save(address)
        );
    }

    @Override
    public AddressDTO assignUser(Address address, User user) {
        address.setUser(user);
        return addressDTOMapper.apply(
                addressRepository.save(address)
        );
    }
}
