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

package eus.ehu.tfg.ktoda.tollsystem.pointlog;

import eus.ehu.tfg.ktoda.tollsystem.transaction.Transaction;
import eus.ehu.tfg.ktoda.tollsystem.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
interface PointLogRepository extends JpaRepository<PointLog, Long> {
    @Query("from PointLog pl where pl.point.pointType = 'ENTER' and pl.user.userId = :userId " +
            "order by pl.timestamp desc")
    Page<PointLog> findLatestENTERPointOfUser(@Param("userId") UUID userId, Pageable pageable);

    @Query("from PointLog pl where pl.point.section.sectionId = :sectionId")
    List<PointLog> findAllBySectionId(@Param("sectionId") String sectionId);

    Page<PointLog> findByUser(User user, Pageable pageable);
}
