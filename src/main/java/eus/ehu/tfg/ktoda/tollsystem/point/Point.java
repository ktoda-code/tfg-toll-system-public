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

package eus.ehu.tfg.ktoda.tollsystem.point;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eus.ehu.tfg.ktoda.tollsystem.geoLocation.GeoLocation;
import eus.ehu.tfg.ktoda.tollsystem.pointlog.PointLog;
import eus.ehu.tfg.ktoda.tollsystem.section.Section;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "points")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Long pointId;
    @Enumerated(EnumType.STRING)
    @Column(name = "point_type")
    private PointType pointType;
    @Basic
    private Integer km;
    @OneToOne
    @JoinColumn(name = "geo_location")
    private GeoLocation geoLocation;
    @ManyToOne
    @JoinColumn(name = "section")
    @JsonIgnore
    private Section section;
    @OneToMany(
            mappedBy = "point",
            fetch = FetchType.LAZY
    )
    @JsonIgnore
    @ToString.Exclude
    private List<PointLog> pointLogs = new ArrayList<>();

    public Point(PointType pointType, int km) {
        this.pointType = pointType;
        this.km = km;
    }

    public void addLog(PointLog log) {
        pointLogs.add(log);
    }

    public void removeLog(PointLog log) {
        pointLogs.remove(log);
    }
}
