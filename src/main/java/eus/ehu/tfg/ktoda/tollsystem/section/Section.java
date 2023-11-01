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

package eus.ehu.tfg.ktoda.tollsystem.section;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eus.ehu.tfg.ktoda.tollsystem.fee.Fee;
import eus.ehu.tfg.ktoda.tollsystem.point.Point;
import eus.ehu.tfg.ktoda.tollsystem.transaction.Transaction;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Section {
    @Id
    @Column(name = "section_id")
    private String sectionId;
    @Column(name = "long_km")
    private Integer longKm;
    private Boolean active = Boolean.TRUE;
    @OneToMany(mappedBy = "section",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private List<Fee> fees = new ArrayList<>();
    @OneToMany(mappedBy = "section",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private List<Point> points = new ArrayList<>();
    @OneToMany(mappedBy = "section",
            cascade = CascadeType.REMOVE,
            fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonIgnore
    private List<Transaction> transactions = new ArrayList<>();

    public Section(String sectionId, Integer longKm) {
        this.sectionId = sectionId;
        this.longKm = longKm;
    }

    public void addFee(Fee fee) {
        fees.add(fee);
    }

    public void removeFee(Fee fee) {
        fees.remove(fee);
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public void removePoint(Point point) {
        points.remove(point);
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);
    }
}
