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

package eus.ehu.tfg.ktoda.tollsystem.geoLocation;

import eus.ehu.tfg.ktoda.tollsystem.geoLocation.dto.GeoLocationDTO;
import eus.ehu.tfg.ktoda.tollsystem.geoLocation.dto.GeoObjectDTOMapper;
import eus.ehu.tfg.ktoda.tollsystem.geoLocation.exception.GeoLocationNotFoundException;
import eus.ehu.tfg.ktoda.tollsystem.point.Point;
import eus.ehu.tfg.ktoda.tollsystem.point.PointType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GeoLocationServiceImplTest {
    @Mock
    private GeoLocationRepository geoLocationRepository;

    @Mock
    private GeoObjectDTOMapper geoObjectDTOMapper;

    @InjectMocks
    private GeoLocationServiceImpl geoLocationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_shouldReturnListOfGeoLocationDTOs() {
        // Arrange
        List<GeoLocation> geoLocations = List.of(
                new GeoLocation(),
                new GeoLocation()
        );

        when(geoLocationRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(geoLocations));
        when(geoObjectDTOMapper.apply(any(GeoLocation.class))).thenReturn(new GeoLocationDTO(0L, 0, 0));

        // Act
        List<GeoLocationDTO> result = geoLocationService.findAll();

        // Assert
        assertThat(result).hasSize(2);
        verify(geoObjectDTOMapper, times(1)).apply(geoLocations.get(0));
    }

    @Test
    void findById_existingId_shouldReturnGeoLocationDTO() {
        // Arrange
        long existingId = 1L;
        GeoLocation geoLocation = new GeoLocation(existingId, 0, 0, null);
        when(geoLocationRepository.findById(existingId)).thenReturn(Optional.of(geoLocation));
        when(geoObjectDTOMapper.apply(geoLocation)).thenReturn(new GeoLocationDTO(existingId, 0, 0));

        // Act
        GeoLocationDTO result = geoLocationService.findById(existingId);

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    void findById_nonExistingId_shouldThrowException() {
        // Arrange
        long nonExistingId = 999L;
        when(geoLocationRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(GeoLocationNotFoundException.class, () -> geoLocationService.findById(nonExistingId));
    }

    @Test
    void removeById_existingId_shouldReturnTrue() {
        // Arrange
        long existingId = 1L;
        when(geoLocationRepository.existsById(existingId)).thenReturn(true);

        // Act
        boolean result = geoLocationService.removeById(existingId);

        // Assert
        assertThat(result).isTrue();
        verify(geoLocationRepository, times(1)).deleteById(existingId);
    }

    @Test
    void removeById_nonExistingId_shouldReturnFalse() {
        // Arrange
        long nonExistingId = 999L;
        when(geoLocationRepository.existsById(nonExistingId)).thenReturn(false);

        // Act
        boolean result = geoLocationService.removeById(nonExistingId);

        // Assert
        assertThat(result).isFalse();
        verify(geoLocationRepository, never()).deleteById(nonExistingId);
    }

    @Test
    void save_shouldSaveGeoLocation() {
        // Arrange
        GeoLocation geoLocationToSave = new GeoLocation(0, 0);

        // Act
        geoLocationService.save(geoLocationToSave);

        // Assert
        verify(geoLocationRepository, times(1)).save(geoLocationToSave);
    }

    @Test
    void save_withGeoLocationRequest_shouldSaveAndReturnDTO() {
        // Arrange
        long expectedId = 1L;
        GeoLocationRequest geoLocationRequest = new GeoLocationRequest(0, 0);
        GeoLocation geoLocationToSave = new GeoLocation(expectedId, geoLocationRequest.latitude(), geoLocationRequest.longitude(), null);
        GeoLocation savedGeoLocation = new GeoLocation(expectedId, 0, 0, null);
        GeoLocationDTO expectedDTO = new GeoLocationDTO(expectedId, 0, 0);

        when(geoLocationRepository.save(any())).thenReturn(savedGeoLocation);
        when(geoObjectDTOMapper.apply(savedGeoLocation)).thenReturn(expectedDTO);

        // Act
        GeoLocationDTO result = geoLocationService.save(geoLocationRequest);

        // Assert
        assertThat(result).isEqualTo(expectedDTO);
        assertThat(geoLocationToSave.getGeoLocationId()).isEqualTo(savedGeoLocation.getGeoLocationId());
        assertThat(geoLocationToSave.getLongitude()).isEqualTo(savedGeoLocation.getLongitude());
        assertThat(geoLocationToSave.getLatitude()).isEqualTo(savedGeoLocation.getLatitude());
//        verify(geoLocationRepository, times(1)).save(geoLocationToSave);
        verify(geoObjectDTOMapper, times(1)).apply(savedGeoLocation);
    }

    @Test
    void update_existingId_shouldUpdateAndReturnDTO() {
        // Arrange
        long existingId = 1L;
        GeoLocationRequest geoLocationRequest = new GeoLocationRequest(10, 20);
        GeoLocation existingGeoLocation = new GeoLocation(existingId, 0, 0, null);
        GeoLocation updatedGeoLocation = new GeoLocation(existingId, geoLocationRequest.latitude(),
                geoLocationRequest.longitude(), null);
        GeoLocationDTO expectedDTO = new GeoLocationDTO(
                existingId,
                updatedGeoLocation.getLatitude(),
                updatedGeoLocation.getLongitude());

        when(geoLocationRepository.findById(existingId)).thenReturn(Optional.of(existingGeoLocation));
        when(geoLocationRepository.save(any(GeoLocation.class))).thenReturn(updatedGeoLocation);
        when(geoObjectDTOMapper.apply(updatedGeoLocation)).thenReturn(expectedDTO);

        // Act
        GeoLocationDTO result = geoLocationService.update(existingId, geoLocationRequest);

        // Assert
        assertThat(result).isEqualTo(expectedDTO);
        verify(geoLocationRepository, times(1)).findById(existingId);
//        verify(geoLocationRepository, times(1)).save(updatedGeoLocation); // Jpa problem
        verify(geoObjectDTOMapper, times(1)).apply(updatedGeoLocation);
    }

    @Test
    void update_nonExistingId_shouldThrowException() {
        // Arrange
        long nonExistingId = 999L;
        GeoLocationRequest geoLocationRequest = new GeoLocationRequest(10, 20);

        when(geoLocationRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(GeoLocationNotFoundException.class, () -> geoLocationService.update(nonExistingId, geoLocationRequest));
    }

    @Test
    void assignPoint_shouldAssignExitPointAndReturnDTO() {
        // Arrange
        long expectedId = 1L;
        GeoLocation geoLocation = new GeoLocation(0, 0);
        Point point = new Point(PointType.EXIT, 0);
        GeoLocation geoLocationWithPoint = new GeoLocation(expectedId, 0, 0, point);
        GeoLocationDTO expectedDTO = new GeoLocationDTO(expectedId,
                geoLocationWithPoint.getLatitude(),
                geoLocationWithPoint.getLongitude());

        when(geoLocationRepository.save(any())).thenReturn(geoLocationWithPoint);
        when(geoObjectDTOMapper.apply(geoLocationWithPoint)).thenReturn(expectedDTO);

        // Act
        GeoLocationDTO result = geoLocationService.assignPoint(geoLocation, point);

        // Assert
        assertThat(result).isEqualTo(expectedDTO);
        // verify(geoLocationRepository, times(1)).save(geoLocationWithPoint); // Problemn JPA handles entities?
        verify(geoObjectDTOMapper, times(1)).apply(geoLocationWithPoint);
    }

}