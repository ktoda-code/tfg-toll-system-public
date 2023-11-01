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

import eus.ehu.tfg.ktoda.tollsystem.pointlog.dto.PointLogDTOAnonymity;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/logs")
@AllArgsConstructor
public class PointLogRestController {
    private final PointLogService pointLogService;

    @GetMapping
    public ResponseEntity<List<PointLogDTOAnonymity>> findAll() {
        return ResponseEntity.ok(pointLogService.findAllAnonymity());
    }

    @GetMapping("/point/{id}")
    public ResponseEntity<List<PointLogDTOAnonymity>> findAllByPoint(@PathVariable String id) {
        return ResponseEntity.ok(pointLogService.findAllBySection(id));
    }


}
