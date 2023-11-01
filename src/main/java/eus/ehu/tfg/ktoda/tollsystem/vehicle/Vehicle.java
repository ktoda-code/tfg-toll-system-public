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

package eus.ehu.tfg.ktoda.tollsystem.vehicle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eus.ehu.tfg.ktoda.tollsystem.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Vehicle {
    @Id
    @Column(name = "license_plate")
    @Pattern(regexp = "^[0-9]{4}[A-Z]{3}$")
    private String licensePlate;
    @Column(nullable = false)
    @NotNull(message = "Brand cannot be null")
    @Size(min = 2, max = 50, message = "Brand must be between 2 and 50 characters")
    private String brand;
    @NotNull(message = "Model cannot be null")
    @Column(nullable = false)
    @Size(min = 2, max = 50, message = "Model must be between 2 and 50 characters")
    private String model;
    @NotNull
    @Column(nullable = false)
    private VehicleType type;
    @ManyToOne
    @ToString.Exclude
    @JsonIgnore
    private User user;

    public Vehicle(String licensePlate, String brand, String model, VehicleType type) {
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.model = model;
        this.type = type;
    }
}
