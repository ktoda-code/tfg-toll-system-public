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
import eus.ehu.tfg.ktoda.tollsystem.user.UserService;
import eus.ehu.tfg.ktoda.tollsystem.user.exception.UserAlreadyExistsException;
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
public class TelephoneServiceImpl implements TelephoneService {
    private TelephoneRepository telephoneRepository;
    private TelephoneDTOMapper telephoneDTOMapper;

    @Override
    public List<TelephoneDTO> findAll() {
        int pageInit = 0;
        int pageSize = 8;
        Pageable page = PageRequest.of(pageInit, pageSize);
        return telephoneRepository
                .findAll(page)
                .stream()
                .map(telephoneDTOMapper)
                .toList();
    }

    @Override
    public Telephone findByDTO(TelephoneDTO telephoneDTO) {
        return telephoneRepository
                .findById(telephoneDTO.telephoneId())
                .orElseThrow(TelephoneNotFoundException::new);
    }

    @Override
    public TelephoneDTO findById(Long id) {
        Optional<TelephoneDTO> telephoneDTO = telephoneRepository
                .findById(id)
                .map(telephoneDTOMapper);
        return telephoneDTO.orElseThrow(
                () -> new TelephoneNotFoundException("Telephone object not found with id" + id));
    }

    @Override
    public boolean removeById(Long id) {
        if (telephoneRepository.existsById(id)) {
            telephoneRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public void save(Telephone telephone) {
        telephoneRepository.save(telephone);
    }

    @Override
    public TelephoneDTO save(TelephoneRequest telephoneRequest) {
        if (telephoneRepository.existsByNumber(telephoneRequest.number())) {
            throw new TelephoneAlreadyExistsException(
                    "Telephone already exists with number " + telephoneRequest.number());
        }
        Telephone telephone = new Telephone(telephoneRequest.number());
        log.info("Creating object: " + telephone);
        return telephoneDTOMapper.apply(
                telephoneRepository.save(telephone)
        );
    }

    @Override
    public TelephoneDTO update(Long id, TelephoneRequest telephoneRequest) {
        log.info("Trying to modify: " + id);
        Telephone telephone = telephoneRepository.findById(id)
                .orElseThrow(() ->
                        new TelephoneNotFoundException("Telephone object not found with id" + id));
        telephone.setNumber(telephoneRequest.number());
        log.info("Modifying object " + telephone);
        return telephoneDTOMapper.apply(
                telephoneRepository.save(telephone)
        );
    }

    @Override
    public TelephoneDTO assignUser(Telephone telephone, User user) {
        telephone.setUser(user);
        return telephoneDTOMapper.apply(
                telephoneRepository.save(telephone)
        );
    }
}
