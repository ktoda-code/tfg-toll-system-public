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

import eus.ehu.tfg.ktoda.tollsystem.pointlog.dto.JourneyDTO;
import eus.ehu.tfg.ktoda.tollsystem.user.User;
import eus.ehu.tfg.ktoda.tollsystem.user.UserService;
import eus.ehu.tfg.ktoda.tollsystem.user.dto.UserSimpleDTO;
import eus.ehu.tfg.ktoda.tollsystem.user.exception.UserNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
@Slf4j
public class JourneyController {
    private final UserService userService;
    private final PointLogService pointLogService;

    @GetMapping("/journeys")
    public String journeys(Model model,
                           @RequestParam(name = "page", defaultValue = "0") int page,
                           @RequestParam(name = "size", defaultValue = "3") int size,
                           HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserSimpleDTO userSimpleDTO;
        try {
            userSimpleDTO = userService.findByEmail(currentPrincipalName);
        } catch (UserNotFoundException e) {
            SecurityContextHolder.clearContext();
            session.invalidate();
            return "redirect:/login";
        }

        User user = userService.findByUUID(userSimpleDTO.userId());
        Pageable pageable = PageRequest.of(page, size); // 'page' is the current page, 'size' is the number of records per page
        Page<PointLog> journeys = pointLogService.findByUser(user, pageable);
//        Page<JourneyDTO> pairs = getJourneysByUser(user, pageable);

        model.addAttribute("journeys", journeys);
        //log.info(pairs.toString());
//        model.addAttribute("pairs", pairs);
        return "journeys";
    }

    public Page<JourneyDTO> getJourneysByUser(User user, Pageable pageable) {
        Page<PointLog> pointLogs = pointLogService.findByUser(user, pageable);

        List<JourneyDTO> journeys = new ArrayList<>();

        int i;
        for (i = 0; i < pointLogs.getContent().size() - 1; i += 2) {
            JourneyDTO journey = new JourneyDTO(
                    pointLogs.getContent().get(i),
                    pointLogs.getContent().get(i + 1));

            journeys.add(journey);
        }

        // Check for an unpaired journey
        if (i == pointLogs.getContent().size() - 1) {
            PointLog lastLog = pointLogs.getContent().get(i);
            JourneyDTO ongoingJourney = new JourneyDTO(lastLog, null);
            journeys.add(ongoingJourney);
        }

        // Convert the List to a Page
        return new PageImpl<>(journeys, pageable, pointLogs.getTotalElements());
    }


}
