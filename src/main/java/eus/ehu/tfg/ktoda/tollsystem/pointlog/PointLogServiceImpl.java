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

package eus.ehu.tfg.ktoda.tollsystem.pointlog;

import eus.ehu.tfg.ktoda.tollsystem.point.Point;
import eus.ehu.tfg.ktoda.tollsystem.point.PointType;
import eus.ehu.tfg.ktoda.tollsystem.point.dto.PointDTO;
import eus.ehu.tfg.ktoda.tollsystem.point.exception.PointNotFoundException;
import eus.ehu.tfg.ktoda.tollsystem.pointlog.dto.PointLogDTO;
import eus.ehu.tfg.ktoda.tollsystem.pointlog.dto.PointLogDTOAnonymity;
import eus.ehu.tfg.ktoda.tollsystem.pointlog.dto.PointLogDTOAnonymityMapper;
import eus.ehu.tfg.ktoda.tollsystem.pointlog.dto.PointLogDTOMapper;
import eus.ehu.tfg.ktoda.tollsystem.user.User;
import eus.ehu.tfg.ktoda.tollsystem.user.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class PointLogServiceImpl implements PointLogService {
    private PointLogRepository pointLogRepository;
    private PointLogDTOMapper pointLogDTOMapper;
    private PointLogDTOAnonymityMapper anonymityMapper;
    private UserService userService;

    @Override
    public List<PointLogDTO> findAll() {
        return pointLogRepository.findAll()
                .stream()
                .map(pointLogDTOMapper)
                .toList();
    }

    @Override
    public List<PointLogDTOAnonymity> findAllAnonymity() {
        return pointLogRepository.findAll()
                .stream()
                .map(anonymityMapper)
                .toList();
    }

    @Override
    public List<PointLogDTOAnonymity> findAllBySection(String id) {
        return pointLogRepository.findAllBySectionId(id)
                .stream()
                .map(anonymityMapper)
                .toList();
    }

    @Override
    public PointLog findByDTO(PointLogDTO pointDTO) {
        return pointLogRepository
                .findById(pointDTO.id())
                .orElseThrow(
                        () -> new PointNotFoundException("Point object not found with id" + pointDTO.id()));

    }

    @Override
    public PointLogDTO findById(long id) {
        Optional<PointLogDTO> pointDTO = pointLogRepository
                .findById(id)
                .map(pointLogDTOMapper);
        return pointDTO.orElseThrow(
                () -> new PointNotFoundException("Log not found with id" + id));
    }

    @Override
    public boolean removeById(long id) {
        if (pointLogRepository.existsById(id)) {
            pointLogRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public void save(PointLog pointLog) {
        pointLogRepository.save(pointLog);
    }

    @Override
    public PointLogDTO save(PointLogRequest pointLogRequest) {
        PointLog pointLog = new PointLog(
                pointLogRequest.timestamp(),
                pointLogRequest.point(),
                pointLogRequest.user());
        log.info("Creating object: " + pointLog);
        return pointLogDTOMapper.apply(
                pointLogRepository.save(pointLog)
        );
    }

    @Override
    public PointLogDTO update(long id, PointLogRequest pointLogRequest) {
        log.info("Trying to modify: " + id);
        PointLog point = pointLogRepository.findById(id)
                .orElseThrow(() ->
                        new PointNotFoundException("Point object not found with id" + id));
        point.setPoint(pointLogRequest.point());
        point.setUser(pointLogRequest.user());
        point.setTimestamp(pointLogRequest.timestamp());
        log.info("Modifying object " + point);
        return pointLogDTOMapper.apply(
                pointLogRepository.save(point)
        );
    }

    @Override
    public PointLog findLatestENTERPointOfUser(UUID userId) {
        // Check user existence
        User managedUser = userService.findByDTO(userService.findById(userId));

        PageRequest pageRequest = PageRequest.of(0, 1);  // Page number: 0, Page size: 1
        return pointLogRepository
                .findLatestENTERPointOfUser(managedUser.getUserId(), pageRequest).getContent().get(0);
    }

    @Override
    public Page<PointLog> findByUser(User user, Pageable pageable) {
        Pageable orderedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("timestamp").descending());
        return pointLogRepository.findByUser(user, orderedPageable);
    }
}
