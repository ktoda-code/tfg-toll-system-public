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
import eus.ehu.tfg.ktoda.tollsystem.fee.dto.FeeDTO;
import eus.ehu.tfg.ktoda.tollsystem.point.Point;
import eus.ehu.tfg.ktoda.tollsystem.point.PointService;
import eus.ehu.tfg.ktoda.tollsystem.point.PointType;
import eus.ehu.tfg.ktoda.tollsystem.point.dto.PointDTO;
import eus.ehu.tfg.ktoda.tollsystem.section.dto.SectionSimpleDTO;
import eus.ehu.tfg.ktoda.tollsystem.section.dto.SectionSimpleDTOMapper;
import eus.ehu.tfg.ktoda.tollsystem.section.dto.SectionWithFeesDTO;
import eus.ehu.tfg.ktoda.tollsystem.section.exception.SectionAlreadyExistsException;
import eus.ehu.tfg.ktoda.tollsystem.section.exception.SectionNotFoundException;
import eus.ehu.tfg.ktoda.tollsystem.transaction.Transaction;
import eus.ehu.tfg.ktoda.tollsystem.transaction.TransactionService;
import eus.ehu.tfg.ktoda.tollsystem.transaction.TransactionStatus;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class SectionServiceImplTest {
    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private SectionSimpleDTOMapper sectionSimpleDTOMapper;

    @Mock
    private TransactionService transactionService;

    @Mock
    private PointService pointService;

    @Mock
    private FeeService feeService;

    @InjectMocks
    private SectionServiceImpl sectionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_ReturnsListOfSectionDTO() {
        // Arrange
        List<Section> sections = Arrays.asList(
                new Section("AP-8", 80),
                new Section("E25", 100)
        );
        when(sectionRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(sections));
        when(sectionSimpleDTOMapper.apply(any(Section.class))).thenAnswer(invocation -> {
            Section section = invocation.getArgument(0);
            return new SectionSimpleDTO(
                    section.getSectionId(),
                    section.getLongKm(),
                    section.getActive());
        });

        // Act
        List<SectionSimpleDTO> result = sectionService.findAll();

        // Assert
        verify(sectionRepository).findAll(any(Pageable.class));
        verify(sectionSimpleDTOMapper, times(2)).apply(any(Section.class));
        assertThat(result).hasSize(2);
        System.out.println(result);
        assertThat(result.get(0).km()).isEqualTo(80);
        assertThat(result.get(1).km()).isEqualTo(100);
    }

    @Test
    void findById_existingId_shouldReturnSectionDTO() {
        // Arrange
        String existingId = "existingId";
        Section existingPoint = new Section(existingId, 150);
        when(sectionRepository.findById(existingId)).thenReturn(Optional.of(existingPoint));
        when(sectionSimpleDTOMapper.apply(existingPoint)).thenAnswer(invocation -> {
            Section section = invocation.getArgument(0);
            return new SectionSimpleDTO(
                    section.getSectionId(),
                    section.getLongKm(),
                    section.getActive());
        });

        // Act
        SectionSimpleDTO result = sectionService.findById(existingId);

        // Assert
        verify(sectionRepository).findById(existingId);
        assertThat(result.km()).isEqualTo(150);
    }

    @Test
    void findById_nonExistingId_shouldThrowException() {
        // Arrange
        String nonExistingId = "noNexistingId";
        when(sectionRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        SectionNotFoundException exception = assertThrows(SectionNotFoundException.class,
                () -> sectionService.findById(nonExistingId));
        assertEquals("Section not found with id " + nonExistingId, exception.getMessage());
        verify(sectionRepository).findById(nonExistingId);
    }

    @Test
    void removeById_ExistingId_ReturnsTrue() {
        // Arrange
        String existingId = "existingId";
        when(sectionRepository.existsById(existingId)).thenReturn(true);

        // Act
        boolean result = sectionService.removeById(existingId);

        // Assert
        assertTrue(result);
        verify(sectionRepository).deleteById(existingId);
    }

    @Test
    void removeById_NonExistingId_ReturnsFalse() {
        // Arrange
        String nonExistingId = "noNexistingId";
        when(sectionRepository.existsById(nonExistingId)).thenReturn(false);

        // Act
        boolean result = sectionService.removeById(nonExistingId);

        // Assert
        assertFalse(result);
        verify(sectionRepository, never()).deleteById(anyString());
    }

    @Test
    void saveSectionEntity() {
        // Arrange
        Section section = new Section("TestId", 0);
        when(sectionRepository.save(section)).thenReturn(section);

        // Act
        sectionService.save(section);

        // Assert
        verify(sectionRepository).save(section);
    }

    @Test
    void saveSectionRequest() {
        // Arrange
        SectionRequest sectionRequest = new SectionRequest("Test", 0, true);
        Section savedSection = new Section(
                sectionRequest.sectionId(),
                sectionRequest.longKm()
        );
        SectionSimpleDTO expectedSectionWithFeesDTO = new SectionSimpleDTO(
                "Test",
                0,
                true
        );

        when(sectionRepository.save(any(Section.class))).thenReturn(savedSection);
        when(sectionSimpleDTOMapper.apply(savedSection)).thenReturn(expectedSectionWithFeesDTO);

        // Act
        SectionSimpleDTO result = sectionService.save(sectionRequest);

        // Assert
        verify(sectionRepository).save(any(Section.class));
        assertThat(result.km()).isEqualTo(expectedSectionWithFeesDTO.km());
        assertThat(result).isInstanceOf(SectionSimpleDTO.class).isEqualTo(expectedSectionWithFeesDTO);
    }

    @Test
    void saveSectionRequest_shouldThrowException() {
        // Arrange
        SectionRequest sectionRequest = new SectionRequest("Test", 0, true);

        when(sectionRepository.existsById(sectionRequest.sectionId()))
                .thenReturn(true);

        // Act & Assert
        assertThrows(SectionAlreadyExistsException.class, () -> sectionService.save(sectionRequest));
    }

    @Test
    void update_ExistingId_ReturnsUpdatedSectionDTO() {
        // Arrange
        String existingId = "existingId";
        SectionRequest sectionRequest = new SectionRequest("R1", 10, true);
        Section existingSection = new Section(existingId, 8);
        Section updatedSection = new Section(sectionRequest.sectionId(), sectionRequest.longKm());
        SectionSimpleDTO expectedSectionWithFeesDTO = new SectionSimpleDTO(
                updatedSection.getSectionId(),
                updatedSection.getLongKm(),
                updatedSection.getActive()
        );

        when(sectionRepository.findById(existingId)).thenReturn(Optional.of(existingSection));
        when(sectionRepository.save(any(Section.class))).thenReturn(updatedSection);
        when(sectionSimpleDTOMapper.apply(updatedSection)).thenReturn(expectedSectionWithFeesDTO);

        // Act
        SectionSimpleDTO result = sectionService.update(existingId, sectionRequest);

        // Assert
        verify(sectionRepository, times(1)).findById(existingId);
        verify(sectionRepository, times(1)).save(any(Section.class));
        assertThat(result.km()).isEqualTo(expectedSectionWithFeesDTO.km());
    }

    @Test
    void update_NonExistingId_ThrowsException() {
        // Arrange
        String nonExistingId = "nonExistingId";
        SectionRequest sectionRequest = new SectionRequest("R1", 10, true);
        when(sectionRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SectionNotFoundException.class,
                () -> sectionService.update(nonExistingId, sectionRequest));
        verify(sectionRepository).findById(nonExistingId);
        verify(sectionRepository, never()).save(any(Section.class));
    }

    @Test
    void addFee() {
        // Arrange
        String sectionId = "validSectionId";
        Section section = new Section(sectionId, 10);
        Fee fee = new Fee(0L, 1, VehicleType.CATEGORY_I, section, true);
        FeeDTO feeDTO = new FeeDTO(0L, 1, VehicleType.CATEGORY_I, true);
        PointDTO point = new PointDTO(0L, 0, PointType.ENTER, null);
        List<FeeDTO> fees = List.of(feeDTO);
        List<PointDTO> points = List.of(point);
        SectionSimpleDTO expectedSectionWithFeesDTO = new SectionSimpleDTO(
                section.getSectionId(),
                section.getLongKm(),
                section.getActive()
        );

        when(sectionRepository.findById(section.getSectionId())).thenReturn(Optional.of(section));
        when(sectionRepository.save(section)).thenReturn(section);
        when(sectionSimpleDTOMapper.apply(section)).thenReturn(expectedSectionWithFeesDTO);

        // Act
        SectionSimpleDTO result = sectionService.addFee(section, fee);

        System.out.println(result);
        // Assert
        assertThat(result).isEqualTo(expectedSectionWithFeesDTO);
        verify(feeService, times(1)).save(any(Fee.class));
    }

    @Test
    void removeFee() {
        // Arrange
        String sectionId = "validSectionId";
        Section section = new Section(sectionId, 10);
        Fee fee = new Fee(0L, 1, VehicleType.CATEGORY_I, section, true);
        List<FeeDTO> fees = List.of();
        SectionSimpleDTO expectedSectionWithFeesDTO = new SectionSimpleDTO(
                section.getSectionId(),
                section.getLongKm(),
                section.getActive()
        );

        when(sectionRepository.findById(section.getSectionId())).thenReturn(Optional.of(section));
        when(sectionRepository.save(section)).thenReturn(section);
        when(sectionSimpleDTOMapper.apply(section)).thenReturn(expectedSectionWithFeesDTO);

        // Act
        SectionSimpleDTO result = sectionService.removeFee(section, fee);

        // Assert
        assertThat(result).isEqualTo(expectedSectionWithFeesDTO);
        verify(feeService, times(1)).save(any(Fee.class));
    }

    @Test
    void addPoint() {
        // Arrange
        String sectionId = "validSectionId";
        Section section = new Section(sectionId, 10);
        Point point = new Point(0L, PointType.ENTER, 0, null, section, null);
        PointDTO pointDTO = new PointDTO(0L, 0, PointType.ENTER, null);
        List<PointDTO> points = List.of(pointDTO);
        SectionSimpleDTO expectedSectionWithFeesDTO = new SectionSimpleDTO(
                section.getSectionId(),
                section.getLongKm(),
                section.getActive()
        );

        when(sectionRepository.findById(section.getSectionId())).thenReturn(Optional.of(section));
        when(sectionRepository.save(section)).thenReturn(section);
        when(sectionSimpleDTOMapper.apply(section)).thenReturn(expectedSectionWithFeesDTO);

        // Act
        SectionSimpleDTO result = sectionService.addPoint(section, point);

        System.out.println(result);
        // Assert
        assertThat(result).isEqualTo(expectedSectionWithFeesDTO);
        verify(pointService, times(1)).save(any(Point.class));
    }

    @Test
    void removePoint() {
        // Arrange
        String sectionId = "validSectionId";
        Section section = new Section(sectionId, 10);
        Point point = new Point(0L, PointType.ENTER, 0, null, section, null);
        List<PointDTO> points = List.of();
        SectionSimpleDTO expectedSectionWithFeesDTO = new SectionSimpleDTO(
                section.getSectionId(),
                section.getLongKm(),
                section.getActive()
        );

        when(sectionRepository.findById(section.getSectionId())).thenReturn(Optional.of(section));
        when(sectionRepository.save(section)).thenReturn(section);
        when(sectionSimpleDTOMapper.apply(section)).thenReturn(expectedSectionWithFeesDTO);

        // Act
        SectionSimpleDTO result = sectionService.removePoint(section, point);

        System.out.println(result);
        // Assert
        assertThat(result).isEqualTo(expectedSectionWithFeesDTO);
        verify(pointService, times(1)).save(any(Point.class));
    }

    @Test
    void addTransaction() {
        // Arrange
        String sectionId = "validSectionId";
        Section section = new Section(sectionId, 10);
        Transaction transaction = new Transaction(
                0L,
                null,
                section,
                2,
                new Timestamp(System.currentTimeMillis()),
                TransactionStatus.PENDING
        );
        SectionSimpleDTO expectedSectionWithFeesDTO = new SectionSimpleDTO(
                section.getSectionId(),
                section.getLongKm(),
                section.getActive()
        );

        when(sectionRepository.findById(section.getSectionId())).thenReturn(Optional.of(section));
        when(sectionRepository.save(section)).thenReturn(section);
        when(sectionSimpleDTOMapper.apply(section)).thenReturn(expectedSectionWithFeesDTO);

        // Act
        SectionSimpleDTO result = sectionService.addTransaction(section, transaction);

        // Assert
        assertThat(result).isEqualTo(expectedSectionWithFeesDTO);
        verify(transactionService, times(1)).save(any(Transaction.class));
    }

    @Test
    void removeTransaction() {
        // Arrange
        String sectionId = "validSectionId";
        Section section = new Section(sectionId, 10);
        Transaction transaction = new Transaction(
                0L,
                null,
                section,
                2,
                new Timestamp(System.currentTimeMillis()),
                TransactionStatus.PENDING
        );
        SectionSimpleDTO expectedSectionWithFeesDTO = new SectionSimpleDTO(
                section.getSectionId(),
                section.getLongKm(),
                section.getActive()
        );

        when(sectionRepository.findById(section.getSectionId())).thenReturn(Optional.of(section));
        when(sectionRepository.save(section)).thenReturn(section);
        when(sectionSimpleDTOMapper.apply(section)).thenReturn(expectedSectionWithFeesDTO);

        // Act
        SectionSimpleDTO result = sectionService.removeTransaction(section, transaction);

        // Assert
        assertThat(result).isEqualTo(expectedSectionWithFeesDTO);
        verify(transactionService, times(1)).save(any(Transaction.class));
    }
}