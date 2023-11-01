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

package eus.ehu.tfg.ktoda.tollsystem.address;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eus.ehu.tfg.ktoda.tollsystem.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;
    @OneToOne
    @JoinColumn(name = "user")
    @ToString.Exclude
    @JsonIgnore
    private User user;
    @NotNull(message = "Province cannot be null")
    @Size(min = 2, max = 50, message = "Province must be at least 2 characters and at most 50")
    @Column(nullable = false)
    private String province;
    @Column(nullable = false)
    @NotNull(message = "City cannot be null")
    @Size(min = 2, max = 50, message = "City must be at least 2 characters and at most 50")
    private String city;
    @Column(nullable = false)
    @NotNull(message = "Street cannot be null")
    @Size(min = 2, max = 50, message = "Street must be at least 2 characters and at most 50")
    private String street;
    @Column(nullable = false)
    @NotNull
    @Min(value = 1, message = "The minimum value must be 1")
    private Integer number;

    public Address(String province, String city, String street, Integer number) {
        this.province = province;
        this.city = city;
        this.street = street;
        this.number = number;
    }
}
