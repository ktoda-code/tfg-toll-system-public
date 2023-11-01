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

package eus.ehu.tfg.ktoda.tollsystem.sign;

import eus.ehu.tfg.ktoda.tollsystem.address.Address;
import eus.ehu.tfg.ktoda.tollsystem.address.AddressRequest;
import eus.ehu.tfg.ktoda.tollsystem.telephone.Telephone;
import eus.ehu.tfg.ktoda.tollsystem.telephone.TelephoneRequest;
import eus.ehu.tfg.ktoda.tollsystem.user.User;
import eus.ehu.tfg.ktoda.tollsystem.user.UserRequest;
import eus.ehu.tfg.ktoda.tollsystem.user.UserRequestObj;
import eus.ehu.tfg.ktoda.tollsystem.user.dto.UserSimpleDTO;
import eus.ehu.tfg.ktoda.tollsystem.user.exception.UserAlreadyExistsException;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.Vehicle;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.VehicleRequest;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.exception.VehicleAlreadyExistsException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
@Slf4j
public class RegisterController {
    private final RegistrationService registrationService;

    @GetMapping("/sign")
    public String sign(Model model) {
        model.addAttribute("registerRequest", new RegistrationRequest());
        model.addAttribute("loginRequest", new LoginRequest());
        return "sign";
    }

    @PostMapping("/sign")
    public String newSigning(
            Model model,
            @Valid @ModelAttribute RegistrationRequest request,
            BindingResult bindingResult) {
        //log.info(String.valueOf(bindingResult));
        if (handleErrors(model, bindingResult)) return "sign"; // Name of the template for the form

        UserRequestObj userRequest = request.getUserRequest();
        User user = new User(
                userRequest.getFirstName(),
                userRequest.getLastName(),
                userRequest.getPassword(),
                userRequest.getEmail(),
                new Timestamp(System.currentTimeMillis()));
        Address address = request.getAddressRequest();
        Vehicle vehicle = request.getVehicleRequest();
        Telephone telephone = null;

        if (!request.getTelephoneRequest().getNumber().isEmpty()) {
            telephone = request.getTelephoneRequest();
        }

        try {
            registrationService.registerUser(user, address, vehicle, telephone);
        } catch (UserAlreadyExistsException | VehicleAlreadyExistsException e) {
            model.addAttribute("loginError", e.getMessage());
            model.addAttribute("registerRequest", new RegistrationRequest());
            model.addAttribute("loginRequest", new LoginRequest());
            return "sign";  // redirect back to the registration view
        }
        // log.info("Log user: " + userSimpleDTO); // Use in dashboard
        return "redirect:/login?success=true";
    }

    static boolean handleErrors(Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorList = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            model.addAttribute("errors", errorList);
            model.addAttribute("registerRequest", new RegistrationRequest());
            model.addAttribute("loginRequest", new LoginRequest());
            return true;
        }
        return false;
    }
}
