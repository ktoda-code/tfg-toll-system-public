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

package eus.ehu.tfg.ktoda.tollsystem.user.dto;

import eus.ehu.tfg.ktoda.tollsystem.vehicle.VehicleType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;

@Data
public class UserProfileUpdateDTO {

    @NotNull(message = "First name cannot be null.")
    @NotEmpty(message = "First name is required.")
    private String firstName;

    @NotNull(message = "Last name cannot be null.")
    @NotEmpty(message = "Last name is required.")
    private String lastName;

    @NotNull(message = "Email cannot be null.")
    @NotEmpty(message = "Email is required.")
    @Email(message = "Invalid email format.")
    private String email;

    @NotEmpty(message = "Password is required.")
    @NotNull(message = "Password cannot be null.")
    @Size(min = 8, max = 18, message = "Password should be between 8 to 18 characters.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[!@#$%^&*])(?=.*[A-Z]).{8,18}$",
            message = "Password must have at least 1 uppercase letter, 1 symbol, and 1 number.")
    private String password;

    // Address Info
    @NotNull(message = "Province cannot be null.")
    @NotEmpty(message = "Province is required.")
    private String province;

    @NotNull(message = "City cannot be null.")
    @NotEmpty(message = "City is required.")
    private String city;

    @NotNull(message = "Street cannot be null.")
    @NotEmpty(message = "Street is required.")
    private String street;

    private Integer addressNumber;

    // Vehicle Info
    @NotNull(message = "License plate cannot be null.")
    @NotEmpty(message = "License plate is required.")
    @Pattern(regexp = "^[0-9]{4}[A-Z]{3}$", message = "License plate should be in format '1234ABC'.")
    private String licensePlate;

    @NotNull(message = "Brand cannot be null.")
    @NotEmpty(message = "Brand is required.")
    private String brand;

    @NotNull(message = "Model cannot be null.")
    @NotEmpty(message = "Model is required.")
    private String model;

    private VehicleType vehicleType;

    // Telephone Info
    // Note: Since there's no validation on the telephone number, no messages are added.
    private String telephoneNumber;

    // getters and setters
}
