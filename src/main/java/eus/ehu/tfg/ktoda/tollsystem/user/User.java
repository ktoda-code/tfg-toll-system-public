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

import com.fasterxml.jackson.annotation.JsonIgnore;
import eus.ehu.tfg.ktoda.tollsystem.address.Address;
import eus.ehu.tfg.ktoda.tollsystem.pointlog.PointLog;
import eus.ehu.tfg.ktoda.tollsystem.telephone.Telephone;
import eus.ehu.tfg.ktoda.tollsystem.transaction.Transaction;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.Vehicle;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User implements UserDetails {
    @Id
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "first_name", nullable = false)
    @NotNull(message = "First name cannot be null")
    @Size(min = 2, max = 50, message = "First name should be between 2 and 50 characters")
    private String firstName;
    @NotNull(message = "Last name cannot be null")
    @Size(min = 2, max = 50, message = "Last name should be between 2 and 50 characters")
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @NotNull(message = "Password cannot be null")
    @Column(nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private String password;
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid")
    @Column(unique = true, nullable = false)
    private String email;
    //@NotNull(message = "Registration date cannot be null")
    @Column(name = "registered_on", nullable = false)
    private Timestamp registeredOn;
    private Boolean enabled = Boolean.TRUE;
    @OneToMany(mappedBy = "user",
            cascade = CascadeType.REMOVE)
    @JsonIgnore
    @ToString.Exclude
    private List<Vehicle> vehicles = new ArrayList<>();
    @OneToMany(mappedBy = "user",
            cascade = CascadeType.REMOVE)
    @JsonIgnore
    @ToString.Exclude
    private List<Telephone> telephones = new ArrayList<>();
    @OneToMany(mappedBy = "user",
            cascade = CascadeType.REMOVE)
    @JsonIgnore
    @ToString.Exclude
    private List<Transaction> transactions = new ArrayList<>();
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "address")
    private Address address;
    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<PointLog> pointLogs = new ArrayList<>();
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    public User(String firstName,
                String lastName,
                String password,
                String email,
                Timestamp registeredOn) {
        this.userId = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        setPassword(password);
        this.email = email;
        this.registeredOn = registeredOn;
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    public void removeVehicle(Vehicle vehicle) {
        vehicles.remove(vehicle);
    }

    public void addTelephone(Telephone telephone) {
        telephones.add(telephone);
    }

    public void removeTelephone(Telephone telephone) {
        telephones.remove(telephone);
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);
    }

    public void addLog(PointLog log) {
        pointLogs.add(log);
    }

    public void removeLog(PointLog log) {
        pointLogs.remove(log);
    }

    public void addRole(String role) {
        roles.add(role);
    }

    public void removeRole(String role) {
        roles.remove(role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
