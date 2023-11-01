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

import eus.ehu.tfg.ktoda.tollsystem.fee.FeeService;
import eus.ehu.tfg.ktoda.tollsystem.section.dto.SectionDTO;
import eus.ehu.tfg.ktoda.tollsystem.section.dto.SectionWithPointsDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;


@Controller
@AllArgsConstructor
@Slf4j
public class SectionController {
    private final SectionService sectionService;
    private final FeeService feeService;
    private static final String[] colors = {
            "#FF0000", "#136e13", "#0000FF", "#FFFF00", "#FF00FF", "#00FFFF", "#000000"
    };

    @GetMapping("/sections")
    public String sectionsPage(Model model,
                               @RequestParam(name = "page", defaultValue = "0") int page,
                               @RequestParam(name = "size", defaultValue = "5") int size) {
//        List<SectionWithPointsDTO> sections = sectionService.findAllWithPoints();
//        List<?> fees = feeService.findFeesBySectionId()
        Pageable pageable = PageRequest.of(page, size); // 'page' is the current page, 'size' is the number of records per page
        Page<SectionDTO> sectionsPage = sectionService.findAllPage(pageable);
        model.addAttribute("sections", sectionsPage);
        model.addAttribute("colors", String.join(",", colors));
        return "sections";
    }
}
