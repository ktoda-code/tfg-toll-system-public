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

import eus.ehu.tfg.ktoda.tollsystem.section.dto.SectionSimpleDTO;
import eus.ehu.tfg.ktoda.tollsystem.section.dto.SectionWithFeesDTO;
import eus.ehu.tfg.ktoda.tollsystem.section.dto.SectionWithPointsDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/sections")
@AllArgsConstructor
public class SectionRestController {
    private final SectionService sectionService;

    @GetMapping
    public ResponseEntity<List<SectionSimpleDTO>> getAllSections() {
        List<SectionSimpleDTO> users = sectionService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/points")
    public ResponseEntity<List<SectionWithPointsDTO>> getSectionsWithPoints() {
        return ResponseEntity.ok(sectionService.findAllWithPoints());
    }

    @GetMapping("/fees")
    public ResponseEntity<List<SectionWithFeesDTO>> getSectionsWithFees() {
        return ResponseEntity.ok(sectionService.findAllWithFees());
    }

    @GetMapping("/{sectionId}")
    public ResponseEntity<SectionSimpleDTO> getSectionById(@PathVariable("sectionId") String sectionId) {
        return ResponseEntity.ok(sectionService.findById(sectionId));
    }

}
