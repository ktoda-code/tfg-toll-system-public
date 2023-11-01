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

package eus.ehu.tfg.ktoda.tollsystem.eagle;

import eus.ehu.tfg.ktoda.tollsystem.eagle.lprecognition.SnapshotCloudAPI;
import eus.ehu.tfg.ktoda.tollsystem.fee.Fee;
import eus.ehu.tfg.ktoda.tollsystem.fee.FeeRequest;
import eus.ehu.tfg.ktoda.tollsystem.fee.FeeService;
import eus.ehu.tfg.ktoda.tollsystem.fee.dto.FeeDTO;
import eus.ehu.tfg.ktoda.tollsystem.point.Point;
import eus.ehu.tfg.ktoda.tollsystem.point.PointRequest;
import eus.ehu.tfg.ktoda.tollsystem.point.PointService;
import eus.ehu.tfg.ktoda.tollsystem.point.PointType;
import eus.ehu.tfg.ktoda.tollsystem.point.dto.PointDTO;
import eus.ehu.tfg.ktoda.tollsystem.section.Section;
import eus.ehu.tfg.ktoda.tollsystem.section.SectionRequest;
import eus.ehu.tfg.ktoda.tollsystem.section.SectionService;
import eus.ehu.tfg.ktoda.tollsystem.section.dto.SectionSimpleDTO;
import eus.ehu.tfg.ktoda.tollsystem.user.User;
import eus.ehu.tfg.ktoda.tollsystem.user.UserRequest;
import eus.ehu.tfg.ktoda.tollsystem.user.UserService;
import eus.ehu.tfg.ktoda.tollsystem.user.dto.UserSimpleDTO;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.Vehicle;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.VehicleRequest;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.VehicleService;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.VehicleType;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.dto.VehicleDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@Slf4j
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EagleServiceIntegrationTest {
    private final EagleService eagleService;
    private final UserService userService;
    private final PointService pointService;
    private final FeeService feeService;
    private final VehicleService vehicleService;
    private final SectionService sectionService;

    @MockBean
    private SnapshotCloudAPI snapshotCloudAPI;
    private EagleRequest eagleRequest;

    @Autowired
    public EagleServiceIntegrationTest(EagleService eagleService, UserService userService, PointService pointService, FeeService feeService, VehicleService vehicleService, SectionService sectionService) {
        this.eagleService = eagleService;
        this.userService = userService;
        this.pointService = pointService;
        this.feeService = feeService;
        this.vehicleService = vehicleService;
        this.sectionService = sectionService;
    }

    @BeforeEach
    void setUp() {
        when(snapshotCloudAPI.recognizeVehicle(any(), any()))
                .thenReturn("5555ABC");

        // Add sections
        System.out.println("Saving sections");
        SectionRequest sectionRequest1 = new SectionRequest("AP-10", 80, true);
        SectionRequest sectionRequest2 = new SectionRequest("AP-80", 120, true);
        SectionRequest sectionRequest3 = new SectionRequest("AP-680", 300, true);

        SectionSimpleDTO sectionWithFeesDTO1 = sectionService.save(sectionRequest1);
        SectionSimpleDTO sectionWithFeesDTO2 = sectionService.save(sectionRequest2);
        SectionSimpleDTO sectionWithFeesDTO3 = sectionService.save(sectionRequest3);

        // Add points
        System.out.println("Saving points");
        PointRequest pointRequest1 = new PointRequest(PointType.ENTER, 0);
        PointRequest pointRequest2 = new PointRequest(PointType.EXIT, 0);
        PointRequest pointRequest3 = new PointRequest(PointType.ENTER, 10);
        PointRequest pointRequest4 = new PointRequest(PointType.EXIT, 10);
        PointRequest pointRequest5 = new PointRequest(PointType.ENTER, 55);
        PointRequest pointRequest6 = new PointRequest(PointType.EXIT, 55);
        PointRequest pointRequest7 = new PointRequest(PointType.ENTER, 70);
        PointRequest pointRequest8 = new PointRequest(PointType.EXIT, 70);

        PointDTO pointDTO1 = pointService.save(pointRequest1);
        PointDTO pointDTO2 = pointService.save(pointRequest2);
        PointDTO pointDTO3 = pointService.save(pointRequest3);
        PointDTO pointDTO4 = pointService.save(pointRequest4);
        PointDTO pointDTO5 = pointService.save(pointRequest5);
        PointDTO pointDTO6 = pointService.save(pointRequest6);
        PointDTO pointDTO7 = pointService.save(pointRequest7);
        PointDTO pointDTO8 = pointService.save(pointRequest8);

        // Add fees
        System.out.println("Adding fees");
        FeeRequest feeRequest1 = new FeeRequest(15, VehicleType.CATEGORY_I, Boolean.TRUE);
        FeeRequest feeRequest2 = new FeeRequest(25, VehicleType.CATEGORY_II, Boolean.TRUE);
        FeeRequest feeRequest3 = new FeeRequest(35, VehicleType.CATEGORY_III, Boolean.TRUE);
        FeeRequest feeRequest4 = new FeeRequest(10, VehicleType.CATEGORY_I, Boolean.TRUE);
        FeeRequest feeRequest5 = new FeeRequest(15, VehicleType.CATEGORY_II, Boolean.TRUE);
        FeeRequest feeRequest6 = new FeeRequest(20, VehicleType.CATEGORY_III, Boolean.TRUE);
        FeeRequest feeRequest7 = new FeeRequest(30, VehicleType.CATEGORY_I, Boolean.TRUE);
        FeeRequest feeRequest8 = new FeeRequest(35, VehicleType.CATEGORY_II, Boolean.TRUE);
        FeeRequest feeRequest9 = new FeeRequest(40, VehicleType.CATEGORY_III, Boolean.TRUE);
        FeeRequest feeRequest10 = new FeeRequest(5, VehicleType.CATEGORY_I, Boolean.FALSE);
        FeeRequest feeRequest11 = new FeeRequest(5, VehicleType.CATEGORY_I, Boolean.FALSE);

        FeeDTO feeDTO1 = feeService.save(feeRequest1);
        FeeDTO feeDTO2 = feeService.save(feeRequest2);
        FeeDTO feeDTO3 = feeService.save(feeRequest3);
        FeeDTO feeDTO4 = feeService.save(feeRequest4);
        FeeDTO feeDTO5 = feeService.save(feeRequest5);
        FeeDTO feeDTO6 = feeService.save(feeRequest6);
        FeeDTO feeDTO7 = feeService.save(feeRequest7);
        FeeDTO feeDTO8 = feeService.save(feeRequest8);
        FeeDTO feeDTO9 = feeService.save(feeRequest9);
        FeeDTO feeDTO10 = feeService.save(feeRequest10);
        FeeDTO feeDTO11 = feeService.save(feeRequest11);
        // Add users
        System.out.println("Adding Users: ");
        UserRequest userRequest1 = new UserRequest(
                "Jerramy",
                "Lopez",
                "jlopezTest!23",
                "jlopaez@gmail.es"
        );
        UserRequest userRequest2 = new UserRequest(
                "Andree",
                "Konsta",
                "!asD324ka",
                "akasd123@gmail.es"
        );


        // Add vehicles
        VehicleRequest vehicleRequest1 = new VehicleRequest(
                "5555ABC",
                "SEAT",
                "Leon",
                VehicleType.CATEGORY_I
        );
        VehicleRequest vehicleRequest2 = new VehicleRequest(
                "0000ABC",
                "RENO",
                "Columbus",
                VehicleType.CATEGORY_II
        );
        VehicleDTO vehicleDTO1 = vehicleService.save(vehicleRequest1);
        VehicleDTO vehicleDTO2 = vehicleService.save(vehicleRequest2);
        Vehicle vehicle1 = vehicleService.findByDTO(vehicleDTO1);
        Vehicle vehicle2 = vehicleService.findByDTO(vehicleDTO2);
        UserSimpleDTO userDTO1 = userService.save(userRequest1);
        UserSimpleDTO userDTO2 = userService.save(userRequest2);
        User user1 = userService.findByDTO(userDTO1);
        User user2 = userService.findByDTO(userDTO2);

        System.out.println("Relations");
        userService.addVehicle(user1, vehicle1);
        userService.addVehicle(user2, vehicle2);
        Fee fee1 = feeService.findByDTO(feeDTO1);
        Fee fee2 = feeService.findByDTO(feeDTO2);
        Fee fee3 = feeService.findByDTO(feeDTO3);
        Fee fee10 = feeService.findByDTO(feeDTO10);
        Fee fee11 = feeService.findByDTO(feeDTO11);
        Point point1 = pointService.findByDTO(pointDTO1);
        Point point2 = pointService.findByDTO(pointDTO2);
        Point point3 = pointService.findByDTO(pointDTO3);
        Point point4 = pointService.findByDTO(pointDTO4);
        Point point5 = pointService.findByDTO(pointDTO5);
        Point point6 = pointService.findByDTO(pointDTO6);
        Point point7 = pointService.findByDTO(pointDTO7);
        Point point8 = pointService.findByDTO(pointDTO8);
        Section section1 = sectionService.findByDTO(sectionWithFeesDTO1);
        sectionService.addFee(section1, fee1);
        sectionService.addFee(section1, fee2);
        sectionService.addFee(section1, fee3);
        sectionService.addFee(section1, fee10);
        sectionService.addFee(section1, fee11);
        sectionService.addPoint(section1, point1);
        sectionService.addPoint(section1, point2);
        sectionService.addPoint(section1, point3);
        sectionService.addPoint(section1, point4);
        sectionService.addPoint(section1, point5);
        sectionService.addPoint(section1, point6);
        Fee fee4 = feeService.findByDTO(feeDTO4);
        Fee fee5 = feeService.findByDTO(feeDTO5);
        Fee fee6 = feeService.findByDTO(feeDTO6);
        Section section2 = sectionService.findByDTO(sectionWithFeesDTO2);
        sectionService.addFee(section2, fee4);
        sectionService.addFee(section2, fee5);
        sectionService.addFee(section2, fee6);
        sectionService.addPoint(section2, point1);
        sectionService.addPoint(section2, point2);
        sectionService.addPoint(section2, point3);
        sectionService.addPoint(section2, point4);
        sectionService.addPoint(section2, point5);
        sectionService.addPoint(section2, point6);
        Fee fee7 = feeService.findByDTO(feeDTO7);
        Fee fee8 = feeService.findByDTO(feeDTO8);
        Fee fee9 = feeService.findByDTO(feeDTO9);
        Section section3 = sectionService.findByDTO(sectionWithFeesDTO3);
        sectionService.addFee(section3, fee7);
        sectionService.addFee(section3, fee8);
        sectionService.addFee(section3, fee9);
        sectionService.addPoint(section3, point1);
        sectionService.addPoint(section3, point2);
        sectionService.addPoint(section3, point3);
        sectionService.addPoint(section3, point4);
        sectionService.addPoint(section3, point5);
        sectionService.addPoint(section3, point6);
        sectionService.addPoint(section3, point7);
        sectionService.addPoint(section3, point8);
        System.out.println(sectionService.findAll());

    }

    @AfterEach
    void tearDown() {
        // Cleanup data
    }

    @Test
    void detectVehicleTest() {
//        pointService.findAll().forEach(pointDTO -> System.out.println(pointDTO.pointId()));

        System.out.println("============|> ENTER TEST");

        eagleRequest = new EagleRequest(2L, "SOME_FILE", new Date(System.currentTimeMillis()));
        System.out.println(eagleService.detectVehicle(eagleRequest));

        System.out.println("============|> EXIT TEST");

        eagleRequest = new EagleRequest(18L, "SOME_FILE", new Date(System.currentTimeMillis()));
        System.out.println(eagleService.detectVehicle(eagleRequest));
    }
}