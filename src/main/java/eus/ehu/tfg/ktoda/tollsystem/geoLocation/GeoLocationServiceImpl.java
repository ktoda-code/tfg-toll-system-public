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
import eus.ehu.tfg.ktoda.tollsystem.point.PointService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class GeoLocationServiceImpl implements GeoLocationService {
    private final GeoLocationRepository geoLocationRepository;
    private final GeoObjectDTOMapper geoObjectDTOMapper;

    @Override
    public List<GeoLocationDTO> findAll() {
        int pageInit = 0;
        int pageSize = 8;
        Pageable page = PageRequest.of(pageInit, pageSize);
        return geoLocationRepository
                .findAll(page)
                .stream()
                .map(geoObjectDTOMapper)
                .toList();
    }

    @Override
    public GeoLocation findByDTO(GeoLocationDTO geoLocationDTO) {
        return geoLocationRepository
                .findById(geoLocationDTO.id())
                .orElseThrow(() ->
                        new GeoLocationNotFoundException("GeoLocation object not found with id" + geoLocationDTO.id()));
    }

    @Override
    public GeoLocationDTO findById(long id) {
        Optional<GeoLocationDTO> geoLocationDTO = geoLocationRepository
                .findById(id)
                .map(geoObjectDTOMapper);
        return geoLocationDTO.orElseThrow(
                () -> new GeoLocationNotFoundException("GeoLocation object not found with id" + id));
    }

    @Override
    public boolean removeById(long id) {
        if (geoLocationRepository.existsById(id)) {
            geoLocationRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public void save(GeoLocation geoLocation) {
        geoLocationRepository.save(geoLocation);
    }

    @Override
    public GeoLocationDTO save(GeoLocationRequest geoLocationRequest) {
        GeoLocation geoLocation = new GeoLocation(
                geoLocationRequest.latitude(),
                geoLocationRequest.longitude());
        log.info("Creating object: " + geoLocation);
        return geoObjectDTOMapper.apply(
                geoLocationRepository.save(geoLocation)
        );
    }

    @Override
    public GeoLocationDTO update(long id, GeoLocationRequest geoLocationRequest) {
        log.info("Trying to modify: " + id);
        GeoLocation geoLocation = geoLocationRepository.findById(id)
                .orElseThrow(() ->
                        new GeoLocationNotFoundException("GeoLocation object not found with id" + id));
        geoLocation.setLatitude(geoLocationRequest.latitude());
        geoLocation.setLongitude(geoLocationRequest.longitude());
        log.info("Modifying object " + geoLocation);
        return geoObjectDTOMapper.apply(
                geoLocationRepository.save(geoLocation)
        );
    }

    @Override
    public GeoLocationDTO assignPoint(GeoLocation geoLocation, Point point) {
        geoLocation.setPoint(point);
        log.info("Saving object: " + geoLocation);
        return geoObjectDTOMapper.apply(
                geoLocationRepository.save(geoLocation)
        );
    }
}
