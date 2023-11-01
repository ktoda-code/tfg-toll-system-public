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

package eus.ehu.tfg.ktoda.tollsystem.point;

import eus.ehu.tfg.ktoda.tollsystem.geoLocation.GeoLocation;
import eus.ehu.tfg.ktoda.tollsystem.geoLocation.GeoLocationService;
import eus.ehu.tfg.ktoda.tollsystem.geoLocation.dto.GeoLocationDTO;
import eus.ehu.tfg.ktoda.tollsystem.geoLocation.dto.GeoObjectDTOMapper;
import eus.ehu.tfg.ktoda.tollsystem.geoLocation.exception.GeoLocationNotFoundException;
import eus.ehu.tfg.ktoda.tollsystem.point.dto.PointDTO;
import eus.ehu.tfg.ktoda.tollsystem.point.dto.PointDTOMapper;
import eus.ehu.tfg.ktoda.tollsystem.point.exception.PointNotFoundException;
import eus.ehu.tfg.ktoda.tollsystem.section.Section;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PointServiceImplTest {
    @Mock
    private PointRepository pointRepository;

    @Mock
    private PointDTOMapper pointDTOMapper;

    @Mock
    private GeoObjectDTOMapper geoObjectDTOMapper;

    @Mock
    private GeoLocationService geoLocationService;

    @InjectMocks
    private PointServiceImpl pointService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_ReturnsListOfPointsDTO() {
        // Arrange
        List<Point> points = Arrays.asList(
                new Point(PointType.EXIT, 0),
                new Point(PointType.ENTER, 0)
        );
        GeoLocationDTO geoLocationDTO = new GeoLocationDTO(0L, 10, 10);

        when(pointRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(points));
        when(pointDTOMapper.apply(any(Point.class))).thenAnswer(invocation -> {
            Point inputPoint = invocation.getArgument(0);
            return new PointDTO(inputPoint.getPointId(), inputPoint.getKm(), inputPoint.getPointType(), geoLocationDTO);
        });

        // Act
        List<PointDTO> result = pointService.findAll();

        // Assert
        verify(pointRepository).findAll(any(Pageable.class));
        assertThat(result).hasSize(2);
        System.out.println(result);
        assertThat(result.get(0).pointType()).isEqualTo(PointType.EXIT);
        assertThat(result.get(1).pointType()).isEqualTo(PointType.ENTER);
    }


    @Test
    void findById_existingId_shouldReturnPointDTO() {
        // Arrange
        long existingId = 1L;
        Point existingPoint = new Point(PointType.EXIT, 0);
        GeoLocationDTO geoLocationDTO = new GeoLocationDTO(0L, 10, 10);
        when(pointRepository.findById(existingId)).thenReturn(Optional.of(existingPoint));
        when(pointDTOMapper.apply(existingPoint)).thenReturn(
                new PointDTO(
                        existingPoint.getPointId(),
                        existingPoint.getKm(),
                        existingPoint.getPointType(),
                        geoLocationDTO)
        );

        // Act
        PointDTO result = pointService.findByIdDTO(existingId);

        // Assert
        verify(pointRepository).findById(existingId);
        assertThat(result.pointType()).isEqualTo(PointType.EXIT);
    }

    @Test
    void findById_nonExistingId_shouldThrowException() {
        // Arrange
        long nonExistingId = 2L;
        when(pointRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        PointNotFoundException exception = assertThrows(PointNotFoundException.class,
                () -> pointService.findById(nonExistingId));
        assertEquals("Point object not found with id" + nonExistingId, exception.getMessage());
        verify(pointRepository).findById(nonExistingId);
    }

    @Test
    void removeById_ExistingId_ReturnsTrue() {
        // Arrange
        long existingId = 1L;
        when(pointRepository.existsById(existingId)).thenReturn(true);

        // Act
        boolean result = pointService.removeById(existingId);

        // Assert
        assertTrue(result);
        verify(pointRepository).deleteById(existingId);
    }

    @Test
    void removeById_NonExistingId_ReturnsFalse() {
        // Arrange
        long nonExistingId = 2L;
        when(pointRepository.existsById(nonExistingId)).thenReturn(false);

        // Act
        boolean result = pointService.removeById(nonExistingId);

        // Assert
        assertFalse(result);
        verify(pointRepository, never()).deleteById(anyLong());
    }

    @Test
    void savePointEntity() {
        // Arrange
        Point point = new Point(PointType.ENTER, 0);
        when(pointRepository.save(point)).thenReturn(point);

        // Act
        pointService.save(point);

        // Assert
        verify(pointRepository).save(point);
    }

    @Test
    void savePointRequest() {
        // Arrange
        PointRequest pointRequest = new PointRequest(PointType.EXIT, 0);
        Point savedPoint = new Point(pointRequest.type(), pointRequest.km());
        PointDTO expectedPointDTO = new PointDTO(
                null,
                0,
                PointType.EXIT,
                null
        );

        when(pointRepository.save(any(Point.class))).thenReturn(savedPoint);
        when(pointDTOMapper.apply(savedPoint)).thenReturn(expectedPointDTO);

        // Act
        PointDTO result = pointService.save(pointRequest);

        // Assert
        verify(pointRepository).save(any(Point.class));
        assertThat(result.pointType()).isEqualTo(expectedPointDTO.pointType());
        assertThat(result).isInstanceOf(PointDTO.class).isEqualTo(expectedPointDTO);
    }

    @Test
    void update_ExistingId_ReturnsUpdatedPointDTO() {
        // Arrange
        long existingId = 1L;
        PointRequest pointRequest = new PointRequest(PointType.EXIT, 0);
        Point existingPoint = new Point(existingId, PointType.ENTER, 0, null, null, null);
        Point updatedPoint = new Point(
                existingId, pointRequest.type(), pointRequest.km(), null, null, null);
        PointDTO expectedPointDTO = new PointDTO(
                updatedPoint.getPointId(),
                updatedPoint.getKm(),
                updatedPoint.getPointType(),
                null);

        when(pointRepository.findById(existingId)).thenReturn(Optional.of(existingPoint));
        when(pointRepository.save(any(Point.class))).thenReturn(updatedPoint);
        when(pointDTOMapper.apply(updatedPoint)).thenReturn(expectedPointDTO);

        // Act
        PointDTO result = pointService.update(existingId, pointRequest);

        // Assert
        verify(pointRepository, times(1)).findById(existingId);
        verify(pointRepository, times(1)).save(any(Point.class));
        assertThat(result.pointType()).isEqualTo(expectedPointDTO.pointType());
    }

    @Test
    void update_NonExistingId_ThrowsException() {
        // Arrange
        long nonExistingId = 2L;
        PointRequest pointRequest = new PointRequest(PointType.ENTER, 0);
        when(pointRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PointNotFoundException.class, () -> pointService.update(nonExistingId, pointRequest));
        verify(pointRepository).findById(nonExistingId);
        verify(pointRepository, never()).save(any(Point.class));
    }

    @Test
    void assignGeoLocation_GeoLocationFound_ReturnsAssignedPointDTO() {
        // Arrange
        Point point = new Point(0L, PointType.ENTER, 0,
                new GeoLocation(1L, 10, 0, null), null, null);
        GeoLocation geoLocation = new GeoLocation(1L, 10, 0, null);
        GeoLocationDTO expectedGeoLocationDTO = new GeoLocationDTO(1L, 10, 0);
        Point expectedPoint = new Point(0L, PointType.ENTER, 0, geoLocation, null, null);
        PointDTO expectedPointDTO = new PointDTO(0L, 0, PointType.ENTER, expectedGeoLocationDTO);

        doNothing().when(geoLocationService).save(geoLocation);
        when(pointRepository.save(point)).thenReturn(expectedPoint);
        when(geoObjectDTOMapper.apply(expectedPoint.getGeoLocation())).thenReturn(expectedGeoLocationDTO);
        when(pointDTOMapper.apply(expectedPoint)).thenReturn(expectedPointDTO);

        // Act
        PointDTO pointDTO = pointService.assignGeoLocation(point, geoLocation);
//        System.out.println("Saved point is: " + pointDTO);

        // Assert
        verify(pointRepository).save(point);
        verify(geoLocationService).save(geoLocation);
        assertEquals(expectedGeoLocationDTO, pointDTO.geoLocationDTO());
        assertThat(pointDTO.pointType()).isEqualTo(expectedPoint.getPointType());
        assertThat(pointDTO.geoLocationDTO()).isEqualTo(expectedGeoLocationDTO);
    }

    @Test
    void assignGeoLocation_GeoLocationNotFound_ThrowsException() {
        // Arrange
        Point point = new Point(PointType.EXIT, 0);
        GeoLocation geoLocation = new GeoLocation();
        doThrow(GeoLocationNotFoundException.class).when(geoLocationService).save(geoLocation);

        // Act & Assert
        assertThrows(GeoLocationNotFoundException.class,
                () -> pointService.assignGeoLocation(point, geoLocation));
        verify(pointRepository, never()).save(any(Point.class));
    }

    @Test
    void assignSection() {
        // Arrange
        Section section = new Section("AP-8",
                100,
                true,
                null,
                null,
                null);
        Point newPoint = new Point(0L, PointType.ENTER, 0, null, null, null);
        Point savedPoint = new Point(0L, PointType.ENTER, 0, null, section, null);
        PointDTO expectedPointDTO = new PointDTO(
                savedPoint.getPointId(),
                savedPoint.getKm(),
                savedPoint.getPointType(),
                null
        );

        when(pointRepository.findById(newPoint.getPointId())).thenReturn(Optional.of(newPoint));
        when(pointRepository.save(newPoint)).thenReturn(savedPoint);
        when(pointDTOMapper.apply(savedPoint)).thenReturn(expectedPointDTO);

        // Act
        PointDTO result = pointService.assignSection(newPoint, section);

        // Assert
        verify(pointRepository, times(1)).save(any(Point.class));
        verify(pointDTOMapper, times(1)).apply(any(Point.class));
        assertThat(result.pointType()).isEqualTo(expectedPointDTO.pointType());
    }
}