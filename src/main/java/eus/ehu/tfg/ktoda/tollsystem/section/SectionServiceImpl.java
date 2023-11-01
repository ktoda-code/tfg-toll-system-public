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

package eus.ehu.tfg.ktoda.tollsystem.section;

import eus.ehu.tfg.ktoda.tollsystem.fee.Fee;
import eus.ehu.tfg.ktoda.tollsystem.fee.FeeService;
import eus.ehu.tfg.ktoda.tollsystem.point.Point;
import eus.ehu.tfg.ktoda.tollsystem.point.PointService;
import eus.ehu.tfg.ktoda.tollsystem.section.dto.*;
import eus.ehu.tfg.ktoda.tollsystem.section.exception.SectionAlreadyExistsException;
import eus.ehu.tfg.ktoda.tollsystem.section.exception.SectionNotFoundException;
import eus.ehu.tfg.ktoda.tollsystem.transaction.Transaction;
import eus.ehu.tfg.ktoda.tollsystem.transaction.TransactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class SectionServiceImpl implements SectionService {
    private SectionRepository sectionRepository;
    private SectionSimpleDTOMapper sectionSimpleDTOMapper;
    private SectionDTOMapper sectionDTOMapper;
    private SectionWithPointsDTOMapper sectionWithPointsDTOMapper;
    private SectionWithFeesDTOMapper sectionWithFeesDTOMapper;
    private TransactionService transactionService;
    private PointService pointService;
    private FeeService feeService;

    @Override
    public List<SectionSimpleDTO> findAll() {
        int pageInit = 0;
        int pageSize = 25;
        Pageable page = PageRequest.of(pageInit, pageSize);
        return sectionRepository
                .findAll(page)
                .stream()
                .map(sectionSimpleDTOMapper)
                .toList();
    }

    @Override
    @Transactional
    public List<SectionWithPointsDTO> findAllWithPoints() {
        return sectionRepository.findAll()
                .stream()
                .map(sectionWithPointsDTOMapper)
                .toList();
    }

    @Override
    public List<SectionWithFeesDTO> findAllWithFees() {
        return sectionRepository.findAll()
                .stream()
                .map(sectionWithFeesDTOMapper)
                .toList();
    }

    @Override
    public Section findSectionById(String sectionId) {
        return sectionRepository
                .findById(sectionId)
                .orElseThrow(
                        () -> new SectionNotFoundException("Section not found with id " + sectionId));
    }

    @Override
    public Section findByDTO(SectionSimpleDTO sectionWithFeesDTO) {
        return sectionRepository
                .findById(sectionWithFeesDTO.sectionId())
                .orElseThrow(
                        () -> new SectionNotFoundException("Section not found with id " + sectionWithFeesDTO.sectionId()));
    }

    @Override
    public SectionSimpleDTO findById(String id) {
        Optional<SectionSimpleDTO> sectionDTO = sectionRepository
                .findById(id)
                .map(sectionSimpleDTOMapper);
        return sectionDTO.orElseThrow(
                () -> new SectionNotFoundException("Section not found with id " + id));
    }

    @Override
    @Transactional
    public boolean removeById(String id) {
        if (sectionRepository.existsById(id)) {
            sectionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void save(Section section) {
        sectionRepository.save(section);
    }

    @Override
    @Transactional
    public SectionSimpleDTO save(SectionRequest sectionRequest) {
        if (sectionRepository.existsById(sectionRequest.sectionId())) {
            throw new SectionAlreadyExistsException("Section already exists with id " + sectionRequest.sectionId());
        }
        Section section = new Section(sectionRequest.sectionId(), sectionRequest.longKm());
        log.info("Creating object: " + section);
        return sectionSimpleDTOMapper.apply(
                sectionRepository.save(section)
        );
    }

    @Override
    @Transactional
    public SectionSimpleDTO update(String id, SectionRequest sectionRequest) {
        log.info("Trying to modify: " + id);
        Section section = sectionRepository.findById(id)
                .orElseThrow(() ->
                        new SectionNotFoundException("Section not found with id " + id));
        //section.setSectionId(sectionRequest.sectionId());
        section.setLongKm(sectionRequest.longKm());
        section.setActive(sectionRequest.active());
        log.info("Modifying object " + section);
        return sectionSimpleDTOMapper.apply(
                sectionRepository.save(section)
        );
    }

    @Override
    @Transactional
    public SectionSimpleDTO addFee(Section section, Fee fee) {
        // Fetch the section again within this transactional boundary
        Section managedSection = sectionRepository.findById(section.getSectionId())
                .orElseThrow(() -> new SectionNotFoundException("Section not found with id: " + section.getSectionId()));

        managedSection.addFee(fee);
        fee.setSection(managedSection);
        feeService.save(fee);
        return sectionSimpleDTOMapper.apply(
                sectionRepository.save(managedSection)
        );
    }

    @Override
    @Transactional
    public SectionSimpleDTO removeFee(Section section, Fee fee) {
        // Fetch the section again within this transactional boundary
        Section managedSection = sectionRepository.findById(section.getSectionId())
                .orElseThrow(() -> new SectionNotFoundException("Section not found with id: " + section.getSectionId()));

        managedSection.removeFee(fee);
        fee.setSection(null);
        feeService.save(fee);
        return sectionSimpleDTOMapper.apply(
                sectionRepository.save(managedSection)
        );
    }

    @Override
    @Transactional
    public SectionSimpleDTO addPoint(Section section, Point point) {
        // Fetch the section again within this transactional boundary
        Section managedSection = sectionRepository.findById(section.getSectionId())
                .orElseThrow(() -> new SectionNotFoundException("Section not found with id: " + section.getSectionId()));

        managedSection.addPoint(point);
        point.setSection(managedSection);
        pointService.save(point);
        return sectionSimpleDTOMapper.apply(
                sectionRepository.save(managedSection)
        );
    }

    @Override
    @Transactional
    public SectionSimpleDTO removePoint(Section section, Point point) {
        // Fetch the section again within this transactional boundary
        Section managedSection = sectionRepository.findById(section.getSectionId())
                .orElseThrow(() -> new SectionNotFoundException("Section not found with id: " + section.getSectionId()));

        managedSection.removePoint(point);
        point.setSection(null);
        pointService.save(point);
        return sectionSimpleDTOMapper.apply(
                sectionRepository.save(managedSection)
        );
    }

    @Override
    @Transactional
    public SectionSimpleDTO addTransaction(Section section, Transaction transaction) {
        // Fetch the section again within this transactional boundary
        Section managedSection = sectionRepository.findById(section.getSectionId())
                .orElseThrow(() -> new SectionNotFoundException("Section not found with id: " + section.getSectionId()));

        managedSection.addTransaction(transaction);
        transaction.setSection(managedSection);
        transactionService.save(transaction);
        return sectionSimpleDTOMapper.apply(
                sectionRepository.save(managedSection)
        );
    }

    @Override
    @Transactional
    public SectionSimpleDTO removeTransaction(Section section, Transaction transaction) {
        // Fetch the section again within this transactional boundary
        Section managedSection = sectionRepository.findById(section.getSectionId())
                .orElseThrow(() -> new SectionNotFoundException("Section not found with id: " + section.getSectionId()));

        managedSection.removeTransaction(transaction);
        transaction.setSection(null);
        transactionService.save(transaction);
        return sectionSimpleDTOMapper.apply(
                sectionRepository.save(managedSection)
        );
    }

    @Override
    public Page<SectionWithPointsDTO> findAllWithPointsPage(Pageable pageable) {
        return sectionRepository
                .findAll(pageable)
                .map(sectionWithPointsDTOMapper);
    }

    @Override
    public Page<SectionWithFeesDTO> findAllWithFeesPage(Pageable pageable) {
        return sectionRepository.findAll(pageable).map(sectionWithFeesDTOMapper);
    }

    @Override
    public Page<SectionDTO> findAllPage(Pageable pageable) {
        return sectionRepository.findAll(pageable).map(sectionDTOMapper);
    }

}
