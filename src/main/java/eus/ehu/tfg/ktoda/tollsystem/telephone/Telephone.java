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

package eus.ehu.tfg.ktoda.tollsystem.telephone;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eus.ehu.tfg.ktoda.tollsystem.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "telephones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Telephone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "telephone_id")
    private Long telephoneId;
    @NotNull
    @Size(max = 12, message = "Telephone must be 12 characters long (county code included)")
    @Column(nullable = false)
    private String number;
    @ManyToOne
    @ToString.Exclude
    @JsonIgnore
    private User user;

    public Telephone(String number) {
        this.number = number;
    }
}
