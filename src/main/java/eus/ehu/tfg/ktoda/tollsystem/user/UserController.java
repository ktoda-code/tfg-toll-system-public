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

package eus.ehu.tfg.ktoda.tollsystem.user;

import eus.ehu.tfg.ktoda.tollsystem.telephone.Telephone;
import eus.ehu.tfg.ktoda.tollsystem.user.dto.UserProfileUpdateDTO;
import eus.ehu.tfg.ktoda.tollsystem.user.dto.UserSimpleDTO;
import eus.ehu.tfg.ktoda.tollsystem.user.exception.UserAlreadyExistsException;
import eus.ehu.tfg.ktoda.tollsystem.user.exception.UserNotFoundException;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.Vehicle;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.exception.VehicleAlreadyExistsException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
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

// Create a new UserProfileUpdateDTO object to be populated
        UserProfileUpdateDTO userProfileUpdateDTO = new UserProfileUpdateDTO();

// Personal Information
        userProfileUpdateDTO.setFirstName(user.getFirstName());
        userProfileUpdateDTO.setLastName(user.getLastName());
        userProfileUpdateDTO.setEmail(user.getEmail());

// If the user entity has the password, set it. Otherwise, skip or handle accordingly.
// userProfileUpdateDTO.setPassword(user.getPassword());

// Address Information
        if (user.getAddress() != null) {
            userProfileUpdateDTO.setProvince(user.getAddress().getProvince());
            userProfileUpdateDTO.setCity(user.getAddress().getCity());
            userProfileUpdateDTO.setStreet(user.getAddress().getStreet());
            userProfileUpdateDTO.setAddressNumber(user.getAddress().getNumber());
        }

// Vehicle Information
        if (user.getVehicles() != null && !user.getVehicles().isEmpty()) {
            Vehicle vehicle = user.getVehicles().get(0); // Assuming a user has one vehicle. Adjust if not.
            userProfileUpdateDTO.setLicensePlate(vehicle.getLicensePlate());
            userProfileUpdateDTO.setBrand(vehicle.getBrand());
            userProfileUpdateDTO.setModel(vehicle.getModel());
            userProfileUpdateDTO.setVehicleType(vehicle.getType());
        }

// Telephone Information
        if (user.getTelephones() != null && !user.getTelephones().isEmpty()) {
            Telephone telephone = user.getTelephones().get(0); // Assuming a user has one telephone. Adjust if not.
            userProfileUpdateDTO.setTelephoneNumber(telephone.getNumber());
        }

// Add the populated DTO to the model
        model.addAttribute("userProfileUpdate", userProfileUpdateDTO);

// Add the original entities to the model if necessary for other purposes
        model.addAttribute("userInfo", user);
        model.addAttribute("addressInfo", user.getAddress());
        if (user.getVehicles() != null && !user.getVehicles().isEmpty()) {
            model.addAttribute("vehicleInfo", user.getVehicles().get(0));
        }
        if (!user.getTelephones().isEmpty()) {
            model.addAttribute("telephoneInfo", user.getTelephones().get(0));
        } else {
            model.addAttribute("telephoneInfo", null);
        }
        return "profile";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(
            @Valid @ModelAttribute UserProfileUpdateDTO userProfile,
            BindingResult bindingResult,
            Model model,
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

// Create a new UserProfileUpdateDTO object to be populated
        UserProfileUpdateDTO userProfileUpdateDTO = new UserProfileUpdateDTO();

// Personal Information
        userProfileUpdateDTO.setFirstName(user.getFirstName());
        userProfileUpdateDTO.setLastName(user.getLastName());
        userProfileUpdateDTO.setEmail(user.getEmail());

// If the user entity has the password, set it. Otherwise, skip or handle accordingly.
// userProfileUpdateDTO.setPassword(user.getPassword());

// Address Information
        if (user.getAddress() != null) {
            userProfileUpdateDTO.setProvince(user.getAddress().getProvince());
            userProfileUpdateDTO.setCity(user.getAddress().getCity());
            userProfileUpdateDTO.setStreet(user.getAddress().getStreet());
            userProfileUpdateDTO.setAddressNumber(user.getAddress().getNumber());
        }

// Vehicle Information
        if (user.getVehicles() != null && !user.getVehicles().isEmpty()) {
            Vehicle vehicle = user.getVehicles().get(0); // Assuming a user has one vehicle. Adjust if not.
            userProfileUpdateDTO.setLicensePlate(vehicle.getLicensePlate());
            userProfileUpdateDTO.setBrand(vehicle.getBrand());
            userProfileUpdateDTO.setModel(vehicle.getModel());
            userProfileUpdateDTO.setVehicleType(vehicle.getType());
        }

// Telephone Information
        if (user.getTelephones() != null && !user.getTelephones().isEmpty()) {
            Telephone telephone = user.getTelephones().get(0); // Assuming a user has one telephone. Adjust if not.
            userProfileUpdateDTO.setTelephoneNumber(telephone.getNumber());
        }

// Add the populated DTO to the model
        model.addAttribute("userProfileUpdate", userProfileUpdateDTO);

// Add the original entities to the model if necessary for other purposes
        model.addAttribute("userInfo", user);
        model.addAttribute("addressInfo", user.getAddress());
        if (user.getVehicles() != null && !user.getVehicles().isEmpty()) {
            model.addAttribute("vehicleInfo", user.getVehicles().get(0));
        }
        if (!user.getTelephones().isEmpty()) {
            model.addAttribute("telephoneInfo", user.getTelephones().get(0));
        } else {
            model.addAttribute("telephoneInfo", null);
        }

        if (bindingResult.hasErrors()) {
            // handle validation errors here
            List<String> errorList = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();
            model.addAttribute("errors", errorList);
            model.addAttribute("userProfileUpdate", new UserProfileUpdateDTO());
            return "profile";
        }

        // Update logic here
        log.info("User update request: {}", userProfile);
        model.addAttribute("userProfileUpdate", userProfileUpdateDTO);
        try {
            userService.update(user.getEmail(), userProfile);
        } catch (VehicleAlreadyExistsException | UserAlreadyExistsException | UserNotFoundException e) {
            log.info("Vehicle already exists with the lp");
        }
        return "redirect:profile";
    }

    @PostMapping("/deleteProfile")
    public String deleteProfile(Model model, HttpSession session) {
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

        userService.removeById(userSimpleDTO.userId());
        SecurityContextHolder.clearContext();
        session.invalidate();
        return "redirect:/login";
    }

}
