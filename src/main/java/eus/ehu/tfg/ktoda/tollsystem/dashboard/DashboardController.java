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

package eus.ehu.tfg.ktoda.tollsystem.dashboard;

import eus.ehu.tfg.ktoda.tollsystem.user.UserService;
import eus.ehu.tfg.ktoda.tollsystem.user.dto.UserSimpleDTO;
import eus.ehu.tfg.ktoda.tollsystem.user.exception.UserAlreadyExistsException;
import eus.ehu.tfg.ktoda.tollsystem.user.exception.UserNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class DashboardController {
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
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

        model.addAttribute("username", userSimpleDTO.firstName());
        return "dashboard";
    }
}
