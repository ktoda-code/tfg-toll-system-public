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

import eus.ehu.tfg.ktoda.tollsystem.eagle.lprecognition.LicencePlateRecognitionWrapperAPI;
import eus.ehu.tfg.ktoda.tollsystem.eagle.lprecognition.SnapshotCloudAPI;
import eus.ehu.tfg.ktoda.tollsystem.eagle.sticker.VehicleEnvironmentStickerAPI;
import eus.ehu.tfg.ktoda.tollsystem.fee.Fee;
import eus.ehu.tfg.ktoda.tollsystem.fee.FeeService;
import eus.ehu.tfg.ktoda.tollsystem.point.Point;
import eus.ehu.tfg.ktoda.tollsystem.point.PointService;
import eus.ehu.tfg.ktoda.tollsystem.point.PointType;
import eus.ehu.tfg.ktoda.tollsystem.point.dto.PointDTO;
import eus.ehu.tfg.ktoda.tollsystem.point.dto.PointDTOMapper;
import eus.ehu.tfg.ktoda.tollsystem.pointlog.PointLog;
import eus.ehu.tfg.ktoda.tollsystem.pointlog.PointLogRequest;
import eus.ehu.tfg.ktoda.tollsystem.pointlog.PointLogService;
import eus.ehu.tfg.ktoda.tollsystem.pointlog.dto.PointLogDTO;
import eus.ehu.tfg.ktoda.tollsystem.section.Section;
import eus.ehu.tfg.ktoda.tollsystem.section.SectionService;
import eus.ehu.tfg.ktoda.tollsystem.transaction.Transaction;
import eus.ehu.tfg.ktoda.tollsystem.transaction.TransactionService;
import eus.ehu.tfg.ktoda.tollsystem.transaction.TransactionStatus;
import eus.ehu.tfg.ktoda.tollsystem.transaction.dto.TransactionDTO;
import eus.ehu.tfg.ktoda.tollsystem.user.User;
import eus.ehu.tfg.ktoda.tollsystem.user.UserService;
import eus.ehu.tfg.ktoda.tollsystem.user.exception.UserNotFoundException;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.Vehicle;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.VehicleService;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.VehicleType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides an interface to interact with the IPD.
 * This service processes vehicle detection events, logging the event and potentially generating a transaction.
 */
@Service
@Slf4j
@AllArgsConstructor
public class EagleServiceImpl implements EagleService {
    private final SnapshotCloudAPI snapshotCloudAPI;
    private final LicencePlateRecognitionWrapperAPI licencePlateRecognitionWrapperAPI;
    private final VehicleEnvironmentStickerAPI environmentStickerAPI;
    private final UserService userService;
    private final PointLogService logService;
    private final PointService pointService;
    private final FeeService feeService;
    private final SectionService sectionService;
    private final TransactionService transactionService;
    private final PointDTOMapper pointDTOMapper;

    @Override
    @Transactional
    public TransactionDTO detectVehicle(EagleRequest request) {
        log.debug("Processing vehicle detection at point: {}", request.pointId());

        Point point = pointService.findById(request.pointId());
        PointDTO pointDTO = pointDTOMapper.apply(point);

        String licensePlate = snapshotCloudAPI.recognizeVehicle(
                new File(request.file_uri().replaceAll("\"", "")),
                Optional.of(request.timestamp())
        );

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String formattedTimestamp = dateFormat.format(request.timestamp());

        if (licensePlate.equals("null")) {
            log.info("Processed vehicle at point " + point +
                    " produced anomaly, footage of detection needs revision. Time of anomaly: " +
                    Timestamp.valueOf(formattedTimestamp));
            return null;
        }
        User user;
        try {
            user = userService.findUserByLicensePlate(licensePlate);
        } catch (UserNotFoundException e) {
            log.info(" Contact the owner of the vehicle to register and pay for the usage");
            return null;
        }


        PointLogDTO logDTO = logService.save(new PointLogRequest(Timestamp.valueOf(formattedTimestamp), point, user));

        PointLog pointLog = logService.findByDTO(logDTO);
        userService.addLog(user, pointLog);
        pointService.addLog(point, pointLog);

        if (pointDTO.pointType().equals(PointType.ENTER)) {
            log.info("Detection at ENTER point. No transaction required.");
            return null;
        }

        log.info("Detection at EXIT point. Transaction required.");

        // user.getPointLogs().forEach(log1 -> System.out.println(log1.getPoint().getPointType()));
        System.out.println(user.getVehicles());
        System.out.println(user.getUserId());
        PointLog logPoint = logService.findLatestENTERPointOfUser(user.getUserId());

        int difference = Math.abs(point.getKm() - logPoint.getPoint().getKm());

        log.debug("The user has traveled " + difference + " km");

        List<Fee> fees = feeService.findFeesBySectionId(point.getSection().getSectionId());
        Map<VehicleType, Integer> vehicleTypeFeeMap = fees
                .stream()
                .collect(Collectors.toMap(
                        Fee::getVehicleType,
                        Fee::getQuantity)
                );

        Vehicle vehicle = null;
        for (Vehicle uv : user.getVehicles()) {
            if (uv.getLicensePlate().equals(licensePlate)) {
                vehicle = uv;
                break;
            }
        }

        double fee = Optional.ofNullable(vehicleTypeFeeMap.get(vehicle.getType()))
                .orElse(10);
        fee /= 100;

        log.debug("Transaction is calculated as " + fee + " * " + difference);

        int transactionAmount = (int) ((difference * fee) * 100);

        Section section = point.getSection();
        Transaction transaction = new Transaction(
                transactionAmount,
                Timestamp.valueOf(formattedTimestamp),
                TransactionStatus.PENDING);

        userService.addTransaction(user, transaction);
        sectionService.addTransaction(section, transaction);

        log.info("Transaction created with amount: {}", transactionAmount);
        return transactionService.findById(transaction.getTransactionId());
    }

    @Override
    @Transactional
    public TransactionDTO detectVehicleV2(EagleRequestV2 requestV2) {
        log.info("Processing vehicle detection at point: {}", requestV2.pointId());

        Point point = pointService.findById(requestV2.pointId());
        PointDTO pointDTO = pointDTOMapper.apply(point);

        String licensePlate;
        if (requestV2.file_uri() != null && requestV2.file_uri().length > 0) {
            String base64File = Base64.getEncoder().encodeToString(requestV2.file_uri());
            licensePlate = snapshotCloudAPI.recognizeVehicleV2(
                    base64File,
                    Optional.of(requestV2.timestamp())
            );
        } else {
            log.info("Problem with URI of the img data");
            throw new RuntimeException("Problem with URI of the img data");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String formattedTimestamp = dateFormat.format(requestV2.timestamp());

        if (licensePlate.equals("null")) {
            log.info("Processed vehicle at point " + point +
                    " produced anomaly, footage of detection needs revision. Time of anomaly: " +
                    Timestamp.valueOf(formattedTimestamp));
            throw new RuntimeException("Anomaly, revise the img data");
        }
        User user;
        try {
            user = userService.findUserByLicensePlate(licensePlate);
        } catch (UserNotFoundException e) {
            log.info("Contact the owner of the vehicle to register and pay for the usage");
            throw new UserNotFoundException("User not found with licence plate");
        }


        PointLogDTO logDTO = logService.save(new PointLogRequest(Timestamp.valueOf(formattedTimestamp), point, user));

        PointLog pointLog = logService.findByDTO(logDTO);
        userService.addLog(user, pointLog);
        pointService.addLog(point, pointLog);

        if (pointDTO.pointType().equals(PointType.ENTER)) {
            log.info("Detection at ENTER point. No transaction required.");
            return null;
        }

        log.info("Detection at EXIT point. Transaction required.");

        String sticker = null;
        try {
            log.info("Trying to detect sticker for " + licensePlate);
            sticker = environmentStickerAPI.detectSticker(licensePlate);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't detected sticker");
        }

        PointLog logPoint = logService.findLatestENTERPointOfUser(user.getUserId());

        int difference = Math.abs(point.getKm() - logPoint.getPoint().getKm());

        log.debug("The user has traveled " + difference + " km");

        List<Fee> fees = feeService.findFeesBySectionId(point.getSection().getSectionId());
        Map<VehicleType, Integer> vehicleTypeFeeMap = fees
                .stream()
                .collect(Collectors.toMap(
                        Fee::getVehicleType,
                        Fee::getQuantity)
                );

        Vehicle vehicle = null;
        for (Vehicle uv : user.getVehicles()) {
            if (uv.getLicensePlate().equals(licensePlate)) {
                vehicle = uv;
                break;
            }
        }

        double fee = Optional.ofNullable(vehicleTypeFeeMap.get(vehicle.getType()))
                .orElse(10);
        fee /= 100;

        log.info("Sticker: " + sticker);
        double sticker_type = 1.2;
        if (sticker.contains("B")) {
            log.info("Sticker Amarillo. Vehículo con emisiones moderadas.");
            sticker_type = 1.15;
        } else if (sticker.contains("C")) {
            log.info("Sticker Verde. Vehículo con emisiones reducidas.");
            sticker_type = 1.1;
        } else if (sticker.contains("ECO")) {
            log.info("Sticker Verde y Azul. Vehículo híbrido o con emisiones muy bajas.");
            sticker_type = 1.05;
        } else if (sticker.contains("0")) {
            log.info("Sticker Azul. Vehículo eléctrico o de cero emisiones.");
            sticker_type = 1;
        }

        log.info("Transaction is calculated as " + fee + " * " + difference + " * " + sticker_type);

        int transactionAmount = (int) ((difference * fee * sticker_type) * 100);

        Section section = point.getSection();
        Transaction transaction = new Transaction(
                transactionAmount,
                Timestamp.valueOf(formattedTimestamp),
                TransactionStatus.PENDING);

        userService.addTransaction(user, transaction);
        sectionService.addTransaction(section, transaction);

        log.info("Transaction created with amount: {}", transactionAmount);
        return transactionService.findById(transaction.getTransactionId());
    }

    @Override
    @Transactional
    public TransactionDTO detectVehicleV3(EagleRequestV2 requestV2) {
        log.info("Processing vehicle detection at point: {}", requestV2.pointId());

        Point point = pointService.findById(requestV2.pointId());
        PointDTO pointDTO = pointDTOMapper.apply(point);

        String licensePlate;
        if (requestV2.file_uri() != null && requestV2.file_uri().length > 0) {
            String base64File = Base64.getEncoder().encodeToString(requestV2.file_uri());
//            String base64File = new String(requestV2.file_uri(), StandardCharsets.UTF_8);
//            log.info("File: " + base64File);
            licensePlate = licencePlateRecognitionWrapperAPI
                    .licensePlateRecognizer(base64File);
        } else {
            log.info("Problem with URI of the img data");
            throw new RuntimeException("Problem with URI of the img data");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String formattedTimestamp = dateFormat.format(requestV2.timestamp());

        if (licensePlate.equals("null")) {
            log.info("Processed vehicle at point " + point +
                    " produced anomaly, footage of detection needs revision. Time of anomaly: " +
                    Timestamp.valueOf(formattedTimestamp));
            throw new RuntimeException("Anomaly, revise the img data");
        }
        User user;
        try {
            user = userService.findUserByLicensePlate(licensePlate);
        } catch (UserNotFoundException e) {
            log.info("Contact the owner of the vehicle to register and pay for the usage");
            throw new UserNotFoundException("User not found with licence plate");
        }

        PointLogDTO logDTO = logService.save(new PointLogRequest(Timestamp.valueOf(formattedTimestamp), point, user));

        PointLog pointLog = logService.findByDTO(logDTO);
        userService.addLog(user, pointLog);
        pointService.addLog(point, pointLog);

        if (pointDTO.pointType().equals(PointType.ENTER)) {
            log.info("Detection at ENTER point. No transaction required.");
            return null;
        }

        log.info("Detection at EXIT point. Transaction required.");

        String sticker = null;
        try {
            log.info("Trying to detect sticker for " + licensePlate);
            sticker = environmentStickerAPI.detectSticker(licensePlate);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't detected sticker");
        }

        PointLog logPoint = logService.findLatestENTERPointOfUser(user.getUserId());

        int difference = Math.abs(point.getKm() - logPoint.getPoint().getKm());

        log.debug("The user has traveled " + difference + " km");

        List<Fee> fees = feeService.findFeesBySectionId(point.getSection().getSectionId());
        Map<VehicleType, Integer> vehicleTypeFeeMap = fees
                .stream()
                .collect(Collectors.toMap(
                        Fee::getVehicleType,
                        Fee::getQuantity)
                );

        Vehicle vehicle = null;
        for (Vehicle uv : user.getVehicles()) {
            if (uv.getLicensePlate().equals(licensePlate)) {
                vehicle = uv;
                break;
            }
        }

        double fee = Optional.ofNullable(vehicleTypeFeeMap.get(vehicle.getType()))
                .orElse(10);
        fee /= 100;

        log.info("Sticker: " + sticker);
        double sticker_type = 1.2;
        if (sticker.contains("B")) {
            log.info("Sticker Amarillo. Vehículo con emisiones moderadas.");
            sticker_type = 1.15;
        } else if (sticker.contains("C")) {
            log.info("Sticker Verde. Vehículo con emisiones reducidas.");
            sticker_type = 1.1;
        } else if (sticker.contains("ECO")) {
            log.info("Sticker Verde y Azul. Vehículo híbrido o con emisiones muy bajas.");
            sticker_type = 1.05;
        } else if (sticker.contains("0")) {
            log.info("Sticker Azul. Vehículo eléctrico o de cero emisiones.");
            sticker_type = 1;
        }

        log.info("Transaction is calculated as " + fee + " * " + difference + " * " + sticker_type);

        int transactionAmount = (int) ((difference * fee * sticker_type) * 100);

        Section section = point.getSection();
        Transaction transaction = new Transaction(
                transactionAmount,
                Timestamp.valueOf(formattedTimestamp),
                TransactionStatus.PENDING);

        userService.addTransaction(user, transaction);
        sectionService.addTransaction(section, transaction);

        log.info("Transaction created with amount: {}", transactionAmount);
        return transactionService.findById(transaction.getTransactionId());
    }
}
