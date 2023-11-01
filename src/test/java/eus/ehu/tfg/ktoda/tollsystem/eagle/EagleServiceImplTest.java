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
import eus.ehu.tfg.ktoda.tollsystem.fee.FeeService;
import eus.ehu.tfg.ktoda.tollsystem.fee.dto.FeeDTO;
import eus.ehu.tfg.ktoda.tollsystem.point.Point;
import eus.ehu.tfg.ktoda.tollsystem.point.PointService;
import eus.ehu.tfg.ktoda.tollsystem.point.PointType;
import eus.ehu.tfg.ktoda.tollsystem.point.dto.PointDTO;
import eus.ehu.tfg.ktoda.tollsystem.point.dto.PointDTOMapper;
import eus.ehu.tfg.ktoda.tollsystem.pointlog.PointLog;
import eus.ehu.tfg.ktoda.tollsystem.pointlog.PointLogService;
import eus.ehu.tfg.ktoda.tollsystem.section.Section;
import eus.ehu.tfg.ktoda.tollsystem.section.SectionService;
import eus.ehu.tfg.ktoda.tollsystem.section.dto.SectionSimpleDTO;
import eus.ehu.tfg.ktoda.tollsystem.transaction.Transaction;
import eus.ehu.tfg.ktoda.tollsystem.transaction.TransactionService;
import eus.ehu.tfg.ktoda.tollsystem.transaction.TransactionStatus;
import eus.ehu.tfg.ktoda.tollsystem.transaction.dto.TransactionDTO;
import eus.ehu.tfg.ktoda.tollsystem.user.User;
import eus.ehu.tfg.ktoda.tollsystem.user.UserService;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.Vehicle;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.VehicleService;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.VehicleType;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.dto.VehicleDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EagleServiceImplTest {
    @Mock
    private SnapshotCloudAPI snapshotCloudAPI;
    @Mock
    private UserService userService;
    @Mock
    private PointLogService logService;
    @Mock
    private PointService pointService;
    @Mock
    private FeeService feeService;
    @Mock
    private VehicleService vehicleService;
    @Mock
    private SectionService sectionService;
    @Mock
    private TransactionService transactionService;
    @Mock
    private PointDTOMapper pointDTOMapper;

    @InjectMocks
    private EagleServiceImpl eagleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void detectVehicleENTER() {
        // Arrange
        Fee fee1 = new Fee(1L, 15, VehicleType.CATEGORY_I, null, true);
        Fee fee2 = new Fee(2L, 25, VehicleType.CATEGORY_II, null, true);
        Fee fee3 = new Fee(3L, 35, VehicleType.CATEGORY_III, null, true);

        List<Fee> fees = List.of(
                fee1, fee2, fee3
        );

        PointDTO pointDTO = new PointDTO(
                1L,
                12,
                PointType.ENTER,
                null);
        Point point = new Point(
                1L, PointType.ENTER, 12, null, null, null);
        List<Point> points = List.of(
                point
        );
        Section section = new Section(
                "AP-8", 120, Boolean.TRUE, fees, points, null);
        point.setSection(section);
        fee1.setSection(section);
        fee2.setSection(section);
        fee3.setSection(section);
        User user = new User();
        user.addVehicle(
                new Vehicle("lp", "brand", "model", VehicleType.CATEGORY_II));

        String lp = "lp";
        when(pointService.findById(1L)).thenReturn(point);
        when(pointDTOMapper.apply(point)).thenReturn(pointDTO);
        when(snapshotCloudAPI.recognizeVehicle(any(), any())).thenReturn(lp);
        when(userService.findUserByLicensePlate(anyString())).thenReturn(user);

        // Act
        TransactionDTO transactionDTO = eagleService.detectVehicle(
                new EagleRequest(pointDTO.pointId(), "any", new Date(1L)));

        //Assert
        assertThat(transactionDTO).isNull();
    }

    @Test
    void detectVehicleEXIT() {
        // Arrange
        Fee fee1 = new Fee(1L, 15, VehicleType.CATEGORY_I, null, true);
        Fee fee2 = new Fee(2L, 25, VehicleType.CATEGORY_II, null, true);
        Fee fee3 = new Fee(3L, 35, VehicleType.CATEGORY_III, null, true);
        FeeDTO feeDTO1 = new FeeDTO(fee1.getFeeId(), fee1.getQuantity(), fee1.getVehicleType(), fee1.getActive());
        FeeDTO feeDTO2 = new FeeDTO(fee2.getFeeId(), fee2.getQuantity(), fee2.getVehicleType(), fee2.getActive());
        FeeDTO feeDTO3 = new FeeDTO(fee3.getFeeId(), fee3.getQuantity(), fee3.getVehicleType(), fee3.getActive());

        List<Fee> fees = List.of(
                fee1, fee2, fee3
        );
        List<FeeDTO> feeDTOS = List.of(
                feeDTO1, feeDTO2, feeDTO3
        );

        PointDTO pointDTOE = new PointDTO(
                6L,
                55,
                PointType.ENTER,
                null);
        Point pointE = new Point(
                6L, PointType.ENTER, 55, null, null, new ArrayList<>());

        PointLog pointLog = new PointLog(1L, new Timestamp(1L), null, pointE);

        PointDTO pointDTO = new PointDTO(
                1L,
                12,
                PointType.EXIT,
                null);
        Point point = new Point(
                1L, PointType.EXIT, 12, null, null, List.of(pointLog));
        List<Point> points = List.of(
                point, pointE
        );
        List<PointDTO> pointDTOS = List.of(
                pointDTO, pointDTOE
        );
        Section section = new Section(
                "AP-8", 120, Boolean.TRUE, fees, points, null);
        SectionSimpleDTO sectionWithFeesDTO = new SectionSimpleDTO(
                section.getSectionId(),
                section.getLongKm(),
                section.getActive()
        );

        point.setSection(section);
        pointE.setSection(section);
        fee1.setSection(section);
        fee2.setSection(section);
        fee3.setSection(section);
        User user = new User();

        user.addVehicle(
                new Vehicle("lp", "brand", "model", VehicleType.CATEGORY_II));
        pointLog.setUser(user);
        user.addLog(pointLog);
        pointLog.setUser(user);
        pointE.addLog(pointLog);

        String lp = "lp";
        VehicleDTO vehicleDTO = new VehicleDTO(lp, "brand", "model", VehicleType.CATEGORY_II);
        Vehicle vehicle = new Vehicle(lp, vehicleDTO.brand(), vehicleDTO.model(), vehicleDTO.vehicleType());
        vehicle.setUser(user);
        TransactionDTO expectedTransaction =
                new TransactionDTO(1L, 1075, new Timestamp(1L), TransactionStatus.PENDING, sectionWithFeesDTO);

        when(pointService.findById(1L)).thenReturn(point);
        when(pointDTOMapper.apply(point)).thenReturn(pointDTO);
        when(snapshotCloudAPI.recognizeVehicle(any(), any())).thenReturn(lp);
        when(userService.findUserByLicensePlate(anyString())).thenReturn(user);
        when(logService.findLatestENTERPointOfUser(user.getUserId())).thenReturn(pointLog);
        when(feeService.findFeesBySectionId(section.getSectionId())).thenReturn(fees);
        when(vehicleService.findById(lp)).thenReturn(vehicleDTO);
        when(vehicleService.findByDTO(vehicleDTO)).thenReturn(vehicle);
        when(sectionService.findById(section.getSectionId()))
                .thenReturn(
                        new SectionSimpleDTO(
                                "AP-8",
                                120,
                                true));

        doAnswer(invocation -> {
            Transaction transactionArg = invocation.getArgument(1);
            transactionArg.setTransactionId(1L);
            return null;
        }).when(userService).addTransaction(any(User.class), any(Transaction.class));
        doAnswer(invocation -> {
            Transaction transactionArg = invocation.getArgument(1);
            transactionArg.setTransactionId(1L);
            return null;
        }).when(sectionService).addTransaction(any(Section.class), any(Transaction.class));
        when(transactionService.findById(expectedTransaction.transactionId())).thenReturn(expectedTransaction);

        // Act
        TransactionDTO transactionDTO = eagleService.detectVehicle(
                new EagleRequest(pointDTO.pointId(),
                        "any",
                        new Date(1L)));

        //Assert
        double fee = (double) fee2.getQuantity() / 100;
        int diff = Math.abs(pointE.getKm() - point.getKm());
        int expectedAmount = (int) ((diff * fee) * 100);
        assertThat(transactionDTO).isNotNull();
        assertThat(transactionDTO.amount()).isEqualTo(expectedAmount);
    }
}