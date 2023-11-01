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
import eus.ehu.tfg.ktoda.tollsystem.point.Point;
import eus.ehu.tfg.ktoda.tollsystem.point.dto.PointDTO;
import eus.ehu.tfg.ktoda.tollsystem.point.exception.PointNotFoundException;
import eus.ehu.tfg.ktoda.tollsystem.section.Section;
import eus.ehu.tfg.ktoda.tollsystem.section.SectionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class FeeServiceImpl implements FeeService {
    private FeeRepository feeRepository;
    private FeeDTOMapper feeDTOMapper;

    @Override
    public List<FeeDTO> findAll() {
        int pageInit = 0;
        int pageSize = 25;
        Pageable page = PageRequest.of(pageInit, pageSize);
        return feeRepository.findAll(page).stream().map(feeDTOMapper).toList();
    }

    @Override
    public Fee findFeeById(Long feeId) {
        return feeRepository.findById(feeId).orElseThrow(
                () -> new FeeNotFoundException("Fee not found with id: " + feeId));
    }

    @Override
    public Fee findByDTO(FeeDTO feeDTO) {
        return feeRepository.findById(feeDTO.feeId()).orElseThrow(FeeNotFoundException::new);
    }

    @Override
    public FeeDTO findById(long id) {
        Optional<FeeDTO> feeDTO = feeRepository.findById(id).map(feeDTOMapper);
        return feeDTO.orElseThrow(() -> new FeeNotFoundException("Fee object not found with id" + id));
    }

    @Override
    public boolean removeById(long id) {
        if (feeRepository.existsById(id)) {
            feeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public void save(Fee fee) {
        feeRepository.save(fee);
    }

    @Override
    public FeeDTO save(FeeRequest feeRequest) {
        Fee fee = new Fee(feeRequest.quantity(), feeRequest.vehicleType());
        log.info("Creating object: " + fee);
        return feeDTOMapper.apply(feeRepository.save(fee));
    }

    @Override
    public FeeDTO update(long id, FeeRequest feeRequest) {
        log.info("Trying to modify: " + id);
        Fee fee = feeRepository.findById(id).orElseThrow(() -> new FeeNotFoundException("Fee object not found with id" + id));
        fee.setActive(feeRequest.active());
        fee.setVehicleType(feeRequest.vehicleType());
        fee.setQuantity(feeRequest.quantity());
        log.info("Modifying object " + fee);
        return feeDTOMapper.apply(feeRepository.save(fee));
    }

    @Override
    public FeeDTO assignSection(Fee fee, Section section) {
        Fee managedFee = feeRepository.findById(fee.getFeeId())
                .orElseThrow(() -> new FeeNotFoundException("Fee not found with id: " + fee.getFeeId()));

        managedFee.setSection(section);
        return feeDTOMapper.apply(feeRepository.save(managedFee));
    }

    @Override
    public List<Fee> findFeesBySectionId(String sectionId) {
        return feeRepository.findFeesBySectionId(sectionId);
    }
}
