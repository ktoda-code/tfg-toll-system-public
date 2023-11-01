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
import eus.ehu.tfg.ktoda.tollsystem.point.dto.PointDTO;
import eus.ehu.tfg.ktoda.tollsystem.point.dto.PointDTOMapper;
import eus.ehu.tfg.ktoda.tollsystem.point.exception.PointNotFoundException;
import eus.ehu.tfg.ktoda.tollsystem.pointlog.PointLog;
import eus.ehu.tfg.ktoda.tollsystem.section.Section;
import eus.ehu.tfg.ktoda.tollsystem.section.SectionService;
import eus.ehu.tfg.ktoda.tollsystem.section.exception.SectionNotFoundException;
import eus.ehu.tfg.ktoda.tollsystem.user.User;
import eus.ehu.tfg.ktoda.tollsystem.user.dto.UserDTO;
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
public class PointServiceImpl implements PointService {
    private PointRepository pointRepository;
    private PointDTOMapper pointDTOMapper;
    private GeoLocationService geoLocationService;

    @Override
    public List<PointDTO> findAll() {
        int pageInit = 0;
        int pageSize = 100;
        Pageable page = PageRequest.of(pageInit, pageSize);
        return pointRepository
                .findAll(page)
                .stream()
                .map(pointDTOMapper)
                .toList();
    }

    @Override
    public Point findByDTO(PointDTO pointDTO) {
        return pointRepository
                .findById(pointDTO.pointId())
                .orElseThrow(
                        () -> new PointNotFoundException("Point object not found with id" + pointDTO.pointId()));
    }

    @Override
    public PointDTO findByIdDTO(Long id) {
        Optional<PointDTO> pointDTO = pointRepository
                .findById(id)
                .map(pointDTOMapper);
        return pointDTO.orElseThrow(
                () -> new PointNotFoundException("Point object not found with id" + id));
    }

    @Override
    public Point findById(long id) {
        return pointRepository.findById(id)
                .orElseThrow(
                        () -> new PointNotFoundException("Point object not found with id" + id));
    }

    @Override
    public boolean removeById(long id) {
        if (pointRepository.existsById(id)) {
            pointRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public void save(Point point) {
        pointRepository.save(point);
    }

    @Override
    public PointDTO save(PointRequest pointRequest) {
        Point point = new Point(pointRequest.type(), pointRequest.km());
        log.info("Creating object: " + point);
        return pointDTOMapper.apply(
                pointRepository.save(point)
        );
    }

    @Override
    public PointDTO update(long id, PointRequest pointRequest) {
        log.info("Trying to modify: " + id);
        Point point = pointRepository.findById(id)
                .orElseThrow(() ->
                        new PointNotFoundException("Point object not found with id" + id));
        point.setPointType(pointRequest.type());
        point.setKm(pointRequest.km());
        log.info("Modifying object " + point);
        return pointDTOMapper.apply(
                pointRepository.save(point)
        );
    }

    @Override
    @Transactional
    public PointDTO assignGeoLocation(Point point, GeoLocation geoLocation) {
        point.setGeoLocation(geoLocation);
        geoLocation.setPoint(point);
        geoLocationService.save(geoLocation);
        log.info("Saving object: " + point);
        return pointDTOMapper.apply(
                pointRepository.save(point)
        );
    }

    @Override
    public PointDTO assignSection(Point point, Section section) {
        // Fetch the section again within this transactional boundary
        Point managedPoint = pointRepository.findById(point.getPointId())
                .orElseThrow(() -> new PointNotFoundException("Section not found with id: " + point.getPointId()));

        managedPoint.setSection(section);
        log.info("Saving object: " + point);
        return pointDTOMapper.apply(
                pointRepository.save(managedPoint)
        );
    }

    @Override
    public PointDTO addLog(Point point, PointLog log) {
        // Fetch the section again within this transactional boundary
        Point managedPoint = pointRepository.findById(point.getPointId())
                .orElseThrow(() -> new PointNotFoundException("Section not found with id: " + point.getPointId()));

        managedPoint.addLog(log);
        return pointDTOMapper.apply(
                pointRepository.save(managedPoint)
        );
    }

    @Override
    public PointDTO removeLog(Point point, PointLog log) {
        // Fetch the section again within this transactional boundary
        Point managedPoint = pointRepository.findById(point.getPointId())
                .orElseThrow(() -> new PointNotFoundException("Section not found with id: " + point.getPointId()));

        managedPoint.removeLog(log);
        return pointDTOMapper.apply(
                pointRepository.save(managedPoint)
        );
    }
}
