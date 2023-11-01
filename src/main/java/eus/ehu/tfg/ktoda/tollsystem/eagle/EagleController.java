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

package eus.ehu.tfg.ktoda.tollsystem.eagle;

import eus.ehu.tfg.ktoda.tollsystem.point.PointType;
import eus.ehu.tfg.ktoda.tollsystem.point.dto.PointSimpleDTO;
import eus.ehu.tfg.ktoda.tollsystem.point.dto.PointSimpleDTOMapper;
import eus.ehu.tfg.ktoda.tollsystem.pointlog.PointLogService;
import eus.ehu.tfg.ktoda.tollsystem.section.Section;
import eus.ehu.tfg.ktoda.tollsystem.section.SectionService;
import eus.ehu.tfg.ktoda.tollsystem.transaction.TransactionService;
import eus.ehu.tfg.ktoda.tollsystem.user.exception.UserNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("eagle")
@Slf4j
@AllArgsConstructor
public class EagleController {
    private final EagleService eagleService;
    private final SectionService sectionService;
    private final PointSimpleDTOMapper pointSimpleDTOMapper;

    @GetMapping
    public String listSections(Model model) {
        model.addAttribute("sections", sectionService.findAll());
        log.info("Eagle page gen");
        return "eagle/eagle"; // This refers to eagle.html in the Thymeleaf templates directory.
    }

    @GetMapping("/{sectionId}/enter")
    public String getEnterPoints(@PathVariable String sectionId, Model model) {
        Section section = sectionService.findSectionById(sectionId);

        List<PointSimpleDTO> enterPoints = section.getPoints()
                .stream()
                .filter(point -> point.getPointType().equals(PointType.ENTER))
                .map(pointSimpleDTOMapper)
                .toList();

        log.info("Selecting enter points {}", enterPoints.size());
        model.addAttribute("enterPoints", enterPoints);
        return "eagle/enterPointsFragment";
    }

    @GetMapping("/{sectionId}/exit")
    public String getExitPoints(@PathVariable String sectionId, Model model) {
        Section section = sectionService.findSectionById(sectionId);

        List<PointSimpleDTO> exitPoints = section.getPoints()
                .stream()
                .filter(point -> point.getPointType().equals(PointType.EXIT))
                .map(pointSimpleDTOMapper)
                .toList();


        log.info("Selecting exit points {}", exitPoints.size());
        model.addAttribute("exitPoints", exitPoints);
        return "eagle/exitPointsFragment";
    }

    @PostMapping("/v2/enter")
    @ResponseBody
    public ResponseEntity<?> detectAtEnter(@Valid @RequestBody EagleRequestV2 requestV2) {
        log.info("Request enter: {}", requestV2);
        try {
            return ResponseEntity.ok(eagleService.detectVehicleV2(requestV2));
        } catch (UserNotFoundException e) {
            log.info("User Not Found! ");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            log.info("An error has ocurred");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/v2/exit")
    @ResponseBody
    public ResponseEntity<?> detectAtExit(@Valid @RequestBody EagleRequestV2 requestV2) {
        log.info("Request exit: {}", requestV2);
        try {
            return ResponseEntity.ok(eagleService.detectVehicleV2(requestV2));
        } catch (UserNotFoundException e) {
            log.info("User Not Found! ");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            log.info("An error has ocurred");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/v3/enter")
    @ResponseBody
    public ResponseEntity<?> detectAtEnterV3(@Valid @RequestBody EagleRequestV2 requestV2) {
        log.info("In V3");
        log.info("Request enter: {}", requestV2);
        try {
            return ResponseEntity.ok(eagleService.detectVehicleV3(requestV2));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/v3/exit")
    @ResponseBody
    public ResponseEntity<?> detectAtExitV3(@Valid @RequestBody EagleRequestV2 requestV2) {
        log.info("In V3");
        log.info("Request exit: {}", requestV2);
        try {
            return ResponseEntity.ok(eagleService.detectVehicleV3(requestV2));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
