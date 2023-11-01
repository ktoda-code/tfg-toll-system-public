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

package eus.ehu.tfg.ktoda.tollinit;

import eus.ehu.tfg.ktoda.tollsystem.address.Address;
import eus.ehu.tfg.ktoda.tollsystem.address.AddressService;
import eus.ehu.tfg.ktoda.tollsystem.eagle.EagleRequestV2;
import eus.ehu.tfg.ktoda.tollsystem.eagle.EagleService;
import eus.ehu.tfg.ktoda.tollsystem.fee.Fee;
import eus.ehu.tfg.ktoda.tollsystem.fee.FeeService;
import eus.ehu.tfg.ktoda.tollsystem.geoLocation.GeoLocation;
import eus.ehu.tfg.ktoda.tollsystem.geoLocation.GeoLocationService;
import eus.ehu.tfg.ktoda.tollsystem.point.Point;
import eus.ehu.tfg.ktoda.tollsystem.point.PointService;
import eus.ehu.tfg.ktoda.tollsystem.point.PointType;
import eus.ehu.tfg.ktoda.tollsystem.point.dto.PointDTO;
import eus.ehu.tfg.ktoda.tollsystem.pointlog.PointLog;
import eus.ehu.tfg.ktoda.tollsystem.pointlog.PointLogService;
import eus.ehu.tfg.ktoda.tollsystem.section.Section;
import eus.ehu.tfg.ktoda.tollsystem.section.SectionRequest;
import eus.ehu.tfg.ktoda.tollsystem.section.SectionService;
import eus.ehu.tfg.ktoda.tollsystem.section.dto.SectionSimpleDTO;
import eus.ehu.tfg.ktoda.tollsystem.telephone.Telephone;
import eus.ehu.tfg.ktoda.tollsystem.telephone.TelephoneService;
import eus.ehu.tfg.ktoda.tollsystem.transaction.Transaction;
import eus.ehu.tfg.ktoda.tollsystem.transaction.TransactionService;
import eus.ehu.tfg.ktoda.tollsystem.transaction.TransactionStatus;
import eus.ehu.tfg.ktoda.tollsystem.user.User;
import eus.ehu.tfg.ktoda.tollsystem.user.UserService;
import eus.ehu.tfg.ktoda.tollsystem.user.dto.UserSimpleDTO;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.Vehicle;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.VehicleService;
import eus.ehu.tfg.ktoda.tollsystem.vehicle.VehicleType;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.List;

/**
 * DatabaseInitializer: Initializes development/testing data for the database.
 * <p>
 * This class is specific to the development environment and should be used
 * solely for testing purposes.
 */
@Component
@Profile("dev")
@PropertySource("classpath:application-dev.properties")
@AllArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {
    private final FeeService feeService;
    private final GeoLocationService geoLocationService;
    private final PointService pointService;
    private final SectionService sectionService;
    private final VehicleService vehicleService;
    private final AddressService addressService;
    private final TelephoneService telephoneService;
    private final UserService userService;
    private final TransactionService transactionService;
    private final PointLogService logService;
    private final EagleService eagleService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(
                """
                        This is a command line runner (clr) for testing initialization data
                        use only in development/testing environments.
                            
                        ===================================================================
                            
                        Starting ....
                        """
        );
        settingUpFees();
        settingUpGeoLocations();
        settingUpPoints();
        settingUpSections();
        settingUpAddresses();
        settingUpTelephones();
        settingUpUsers();
        settingUpVehicles();
        settingUpTransactions();
        settingUpPointLogs();

        System.out.println("""
                ====================================================================================                
                ====================================================================================                
                ====================================================================================                
                                
                         TOLL SYSTEM DEVELOPMENT DATABASE INITIALIZATION FINISHED SUCCESSFULLY
                                                
                ====================================================================================
                ====================================================================================
                ====================================================================================
                """);

        //testV3LPR();
    }

    // Methods for setting up specific data in the database

    public void settingUpFees() {
        // Implementation for setting up fees
        log.info("Setting up fees ... ");

        List<Fee> fees = List.of(
                new Fee(100, VehicleType.CATEGORY_I),
                new Fee(180, VehicleType.CATEGORY_I),
                new Fee(232, VehicleType.CATEGORY_II),
                new Fee(360, VehicleType.CATEGORY_II),
                new Fee(350, VehicleType.CATEGORY_III),
                new Fee(465, VehicleType.CATEGORY_III)
        );

        fees.forEach(feeService::save);

        log.info("Fees setup completed.");

        log.info("Retrieving fees: ");
        feeService.findAll()
                .forEach(System.out::println);
    }

    public void settingUpGeoLocations() {
        // Implementation for setting up geo-locations
        log.info("Setting up locations ... ");
        double scalingFactor = 1e6;

        List<GeoLocation> geoLocations = List.of(
                new GeoLocation(
                        (int) ((int) 37.7749 * scalingFactor),
                        (int) ((int) -122.4194 * scalingFactor)),
                new GeoLocation(
                        (int) ((int) 42.7779 * scalingFactor),
                        (int) ((int) 24.0013 * scalingFactor)),
                new GeoLocation(
                        (int) ((int) 155.1049 * scalingFactor),
                        (int) ((int) -22.43094 * scalingFactor)),
                new GeoLocation(
                        (int) ((int) 7.7090 * scalingFactor),
                        (int) ((int) -22.4014 * scalingFactor))
        );

        geoLocations.forEach(geoLocationService::save);

        log.info("GeoLocation setup completed.");

        log.info("Retrieving locations: ");
        geoLocationService.findAll()
                .forEach(System.out::println);
    }

    public void settingUpPoints() {
        // Implementation for setting up points
        log.info("Setting up points ... ");

        List<Point> points = List.of(
                new Point(PointType.EXIT, 0),
                new Point(PointType.ENTER, 0),
                new Point(PointType.EXIT, 0),
                new Point(PointType.ENTER, 0),
                new Point(PointType.EXIT, 3),
                new Point(PointType.ENTER, 3),
                new Point(PointType.EXIT, 34),
                new Point(PointType.ENTER, 34),
                new Point(PointType.EXIT, 10),
                new Point(PointType.ENTER, 10)
        );

        points.forEach(pointService::save);

        log.info("Points setup completed.");

        log.info("Assign Geo locations to Points");
        GeoLocation geoLocation1 = geoLocationService.findByDTO(geoLocationService.findById(1L));
        GeoLocation geoLocation2 = geoLocationService.findByDTO(geoLocationService.findById(2L));
        GeoLocation geoLocation3 = geoLocationService.findByDTO(geoLocationService.findById(3L));
        GeoLocation geoLocation4 = geoLocationService.findByDTO(geoLocationService.findById(4L));
        // Should throw exception
//        GeoLocationDTO g5 = geoLocationService.findById(5L);
//        GeoLocation geoLocation5 = geoLocationService.findByDTO(g5);

        PointDTO pointDTO1 = pointService.findByIdDTO(1L);
        PointDTO pointDTO2 = pointService.findByIdDTO(2L);
        PointDTO pointDTO3 = pointService.findByIdDTO(3L);
        PointDTO pointDTO4 = pointService.findByIdDTO(4L);

        pointService.assignGeoLocation(pointService.findByDTO(pointDTO1), geoLocation1);
        pointService.assignGeoLocation(pointService.findByDTO(pointDTO2), geoLocation2);
        pointService.assignGeoLocation(pointService.findByDTO(pointDTO3), geoLocation3);
        pointService.assignGeoLocation(pointService.findByDTO(pointDTO4), geoLocation4);
        // Should throw exception
//        pointService.assignGeoLocation(pointService.findByDTO(pointDTO4), geoLocation5);

        log.info("Retrieving points: ");
        pointService.findAll()
                .forEach(System.out::println);
    }

    public void settingUpSections() {
        // Implementation for setting up sections
        log.info("Setting up sections ... ");

        List<Section> sections = List.of(
                new Section("AP-8", 80),
                new Section("E512", 120)
        );

        sections.forEach(sectionService::save);

        log.info("Sections setup completed.");
        log.info("Assign Points to Sections");
        Point point1 = pointService.findByDTO(pointService.findByIdDTO(1L));
        Point point2 = pointService.findByDTO(pointService.findByIdDTO(2L));
        Point point3 = pointService.findByDTO(pointService.findByIdDTO(3L));
        Point point4 = pointService.findByDTO(pointService.findByIdDTO(4L));
        Point point5 = pointService.findByDTO(pointService.findByIdDTO(5L));
        Point point6 = pointService.findByDTO(pointService.findByIdDTO(6L));
        Point point7 = pointService.findByDTO(pointService.findByIdDTO(7L));
        Point point8 = pointService.findByDTO(pointService.findByIdDTO(8L));
        Point point9 = pointService.findByDTO(pointService.findByIdDTO(9L));
        Point point10 = pointService.findByDTO(pointService.findByIdDTO(10L));

        SectionSimpleDTO sectionWithFeesDTO1 = sectionService.findById("AP-8");
        SectionSimpleDTO sectionWithFeesDTO2 = sectionService.findById("E512");

        sectionService.addPoint(sectionService.findByDTO(sectionWithFeesDTO1), point1);
        sectionService.addPoint(sectionService.findByDTO(sectionWithFeesDTO1), point2);
        sectionService.addPoint(sectionService.findByDTO(sectionWithFeesDTO1), point5);
        sectionService.addPoint(sectionService.findByDTO(sectionWithFeesDTO1), point6);
        sectionService.addPoint(sectionService.findByDTO(sectionWithFeesDTO1), point7);
        sectionService.addPoint(sectionService.findByDTO(sectionWithFeesDTO1), point8);
        sectionService.addPoint(sectionService.findByDTO(sectionWithFeesDTO2), point3);
        sectionService.addPoint(sectionService.findByDTO(sectionWithFeesDTO2), point4);
        sectionService.addPoint(sectionService.findByDTO(sectionWithFeesDTO2), point9);
        sectionService.addPoint(sectionService.findByDTO(sectionWithFeesDTO2), point10);

        log.info("Assign Fee to Sections");
        Fee fee1 = feeService.findByDTO(feeService.findById(1L));
        Fee fee2 = feeService.findByDTO(feeService.findById(2L));
        Fee fee3 = feeService.findByDTO(feeService.findById(3L));
        Fee fee4 = feeService.findByDTO(feeService.findById(4L));
        Fee fee5 = feeService.findByDTO(feeService.findById(5L));
        Fee fee6 = feeService.findByDTO(feeService.findById(6L));

        sectionService.addFee(sectionService.findByDTO(sectionWithFeesDTO1), fee5);
        sectionService.addFee(sectionService.findByDTO(sectionWithFeesDTO1), fee6);
        sectionService.addFee(sectionService.findByDTO(sectionWithFeesDTO1), fee3);
        sectionService.addFee(sectionService.findByDTO(sectionWithFeesDTO1), fee4);
        sectionService.addFee(sectionService.findByDTO(sectionWithFeesDTO2), fee1);
        sectionService.addFee(sectionService.findByDTO(sectionWithFeesDTO2), fee2);
        sectionService.addFee(sectionService.findByDTO(sectionWithFeesDTO2), fee3);
        sectionService.addFee(sectionService.findByDTO(sectionWithFeesDTO2), fee4);

        log.info("Retrieving sections: ");
        sectionService.findAll()
                .forEach(System.out::println);

        log.info("Removing fees");
        sectionService.removeFee(sectionService.findByDTO(sectionWithFeesDTO2), fee3);
        sectionService.removeFee(sectionService.findByDTO(sectionWithFeesDTO2), fee4);

        System.out.println(sectionService.update("AP-8", new SectionRequest("AP-8", 150, true)));

        log.info("Retrieving sections: ");
        sectionService.findAll()
                .forEach(System.out::println);

    }

    public void settingUpVehicles() {
        // Implementation for setting up vehicles
        log.info("Setting up vehicles ... ");

        Vehicle e1 = new Vehicle("9034FLK", "VW", "Passat", VehicleType.CATEGORY_I);
        Vehicle e2 = new Vehicle("5133XBL", "Audi", "RS6", VehicleType.CATEGORY_I);
        Vehicle e3 = new Vehicle("5570JXM", "Seat", "Leaon", VehicleType.CATEGORY_I);

        List<Vehicle> vehicles = List.of(
                e1,
                e2,
                e3
        );
        log.info("Adding vehicles to users");
        User u1 = userService.findByUUID(userService.findByEmail("kandreev001@ikalse.ehu.eus").userId());
        User u2 = userService.findByUUID(userService.findByEmail("jdoe012@ikalse.ehu.eus").userId());

        e1.setUser(u1);
        e2.setUser(u1);
        e3.setUser(u2);
        vehicles.forEach(vehicleService::save);

        log.info("Vehicle setup completed.");
        
        log.info("Retrieving vehicles: ");
        vehicleService.findAll()
                .forEach(System.out::println);

    }

    public void settingUpAddresses() {
        // Implementation for setting up addresses
        log.info("Setting up addresses ... ");

        List<Address> addresses = List.of(
                new Address("Navarra", "Vitoria", "San Jose", 1),
                new Address("Madrid", "Madrid", "Pedro ", 4),
                new Address("Gipuzkoa", "San Sebastian/Donostia", "Zumaburu", 2)
        );

        addresses.forEach(addressService::save);

        log.info("Address setup completed.");

        log.info("Retrieving addresses: ");
        addressService.findAll()
                .forEach(System.out::println);
    }

    public void settingUpTelephones() {
        // Implementation for setting up telephones
        log.info("Setting up pay telephones ... ");

        List<Telephone> telephones = List.of(
                new Telephone("+34621397517"),
                new Telephone("+34602052122")
        );

        telephones.forEach(telephoneService::save);

        log.info("Telephone setup completed.");

        log.info("Retrieving telephones: ");
        telephoneService.findAll()
                .forEach(System.out::println);
    }


    public void settingUpUsers() {
        // Implementation for setting up users
        log.info("Setting up pay users ... ");
        User us1 = new User("Konstantin",
                "Andreev",
                "123456ktA!",
                "kandreev001@ikalse.ehu.eus",
                new Timestamp(System.currentTimeMillis()));
        User us2 = new User("John",
                "Doe",
                "123456ktA!",
                "jdoe012@ikalse.ehu.eus",
                new Timestamp(System.currentTimeMillis()));

        us1.addRole("USER");
        us2.addRole("ADMIN");

        List<User> users = List.of(
                us1, us2
        );

        users.forEach(userService::register);

        log.info("User setup completed.");

        log.info("Retrieving users: ");
        userService.findAll()
                .forEach(System.out::println);

//        log.info("Add vehicle to User");
//        Vehicle vehicle1 = vehicleService.findByDTO(vehicleService.findById("9034FLK"));
//        Vehicle vehicle2 = vehicleService.findByDTO(vehicleService.findById("5133XBL"));
//        Vehicle vehicle3 = vehicleService.findByDTO(vehicleService.findById("5570JXM"));

        UserSimpleDTO userDTO1 = userService.findByEmail("kandreev001@ikalse.ehu.eus");
        UserSimpleDTO userDTO2 = userService.findByEmail("jdoe012@ikalse.ehu.eus");

        User u1 = userService.findByDTO(userDTO1);
        User u2 = userService.findByDTO(userDTO2);

        // Uses vehicle service
//        u1.addVehicle(vehicle1);
//        u1.addVehicle(vehicle2);
//        u2.addVehicle(vehicle3);
//        log.info("Adding vehicles: ");
//        u1.addVehicle(vehicle1);
//        u1.addVehicle(vehicle2);
//        u2.addVehicle(vehicle3);

        log.info("Add telephones to User");
        Telephone telephone1 = telephoneService.findByDTO(telephoneService.findById(1L));
        Telephone telephone2 = telephoneService.findByDTO(telephoneService.findById(2L));

        userService.addTelephone(u1, telephone1);
        userService.addTelephone(u2, telephone2);

        log.info("Add address to User");
        Address address1 = addressService.findByDTO(addressService.findById(1L));
        Address address2 = addressService.findByDTO(addressService.findById(2L));

        userService.assignAddress(u1, address1);
        userService.assignAddress(u2, address2);

        log.info("Retrieving users: ");
        userService.findAll()
                .forEach(System.out::println);
    }

    public void settingUpTransactions() {
        // Implementation for setting up transactions
        log.info("Setting up pay transactions ... ");

        List<Transaction> transactions = List.of(
                new Transaction(233, new Timestamp(System.currentTimeMillis()), TransactionStatus.PENDING),
                new Transaction(44, new Timestamp(System.currentTimeMillis()), TransactionStatus.PENDING),
                new Transaction(533, new Timestamp(System.currentTimeMillis()), TransactionStatus.APPROVED),
                new Transaction(463, new Timestamp(System.currentTimeMillis()), TransactionStatus.REFUSED),
                new Transaction(700, new Timestamp(System.currentTimeMillis()), TransactionStatus.PENDING)
        );

        transactions.forEach(transactionService::save);

        log.info("Transaction setup completed.");

        log.info("Retrieving transactions: ");
        transactionService.findAll()
                .forEach(System.out::println);

        Transaction transaction1 = transactionService.findByDTO(transactionService.findById(1L));
        Transaction transaction2 = transactionService.findByDTO(transactionService.findById(2L));
        Transaction transaction3 = transactionService.findByDTO(transactionService.findById(3L));
        Transaction transaction4 = transactionService.findByDTO(transactionService.findById(4L));
        Transaction transaction5 = transactionService.findByDTO(transactionService.findById(5L));

        Section section1 = sectionService.findByDTO(sectionService.findById("AP-8"));
        Section section2 = sectionService.findByDTO(sectionService.findById("E512"));

        UserSimpleDTO userDTO1 = userService.findByEmail("kandreev001@ikalse.ehu.eus");
        UserSimpleDTO userDTO2 = userService.findByEmail("jdoe012@ikalse.ehu.eus");

        User u1 = userService.findByDTO(userDTO1);
        User u2 = userService.findByDTO(userDTO2);

        sectionService.addTransaction(section1, transaction1);
        sectionService.addTransaction(section1, transaction2);
        sectionService.addTransaction(section2, transaction3);
        sectionService.addTransaction(section1, transaction4);
        sectionService.addTransaction(section2, transaction5);
        userService.addTransaction(u1, transaction2);
        userService.addTransaction(u1, transaction3);
        userService.addTransaction(u2, transaction1);
        userService.addTransaction(u2, transaction4);
        userService.addTransaction(u2, transaction5);


        log.info("Retrieving transactions: ");
        transactionService.findAll()
                .forEach(System.out::println);

        log.info("Retrieving users: ");
        userService.findAll()
                .forEach(System.out::println);
    }

    public void settingUpPointLogs() {
        // Implementation for setting up fees
        log.info("Setting up logs ... ");

        Point point1 = pointService.findByDTO(pointService.findByIdDTO(1L));
        Point point2 = pointService.findByDTO(pointService.findByIdDTO(2L));
        Point point3 = pointService.findByDTO(pointService.findByIdDTO(3L));
        Point point4 = pointService.findByDTO(pointService.findByIdDTO(4L));

        UserSimpleDTO userDTO1 = userService.findByEmail("kandreev001@ikalse.ehu.eus");
        UserSimpleDTO userDTO2 = userService.findByEmail("jdoe012@ikalse.ehu.eus");

        User u1 = userService.findByDTO(userDTO1);
        User u2 = userService.findByDTO(userDTO2);

        List<PointLog> logs = List.of(
                new PointLog(
                        new Timestamp(System.currentTimeMillis()),
                        point1,
                        u1),
                new PointLog(
                        new Timestamp(System.currentTimeMillis()),
                        point2,
                        u1),
                new PointLog(
                        new Timestamp(System.currentTimeMillis()),
                        point3,
                        u2),
                new PointLog(
                        new Timestamp(System.currentTimeMillis()),
                        point4,
                        u2)
        );

        logs.forEach(logService::save);

        log.info("Add point log to user");
        PointLog pointLog1 = logService.findByDTO(logService.findById(1L));
        PointLog pointLog2 = logService.findByDTO(logService.findById(2L));
        PointLog pointLog3 = logService.findByDTO(logService.findById(3L));
        PointLog pointLog4 = logService.findByDTO(logService.findById(4L));

        userService.addLog(u1, pointLog1);
        userService.addLog(u1, pointLog2);
        userService.addLog(u2, pointLog3);
        userService.addLog(u2, pointLog4);

        log.info("Add point log to point");
        pointService.addLog(point1, pointLog1);
        pointService.addLog(point2, pointLog2);
        pointService.addLog(point3, pointLog3);
        pointService.addLog(point4, pointLog4);

        log.info("logs setup completed.");

        log.info("Retrieving logs: ");
        logService.findAll()
                .forEach(System.out::println);

        log.info("Retrieving users: ");
        userService.findAll()
                .forEach(System.out::println);

        log.info("Retrieving points: ");
        pointService.findAll()
                .forEach(System.out::println);
    }

    @SneakyThrows
    public void testV3LPR() {
        String imgFile = "toll-imgs/IMG-20230731-WA0004.jpg";
        Path filePath = Paths.get(imgFile);
        if (Files.exists(filePath)) {
            System.out.println("File exists and is readable.");
        } else {
            System.out.println("File does not exist or is not readable.");
        }

        byte[] fileContent = Files.readAllBytes(filePath);
        //byte[] decodedBytes = Base64.getDecoder().decode(Base64.getEncoder().encodeToString(fileContent));
        //Files.write(Paths.get("decoded_image.jpg"), decodedBytes);

        eagleService.detectVehicleV3(new EagleRequestV2(
                1L,
                Base64.getEncoder().encode(fileContent),
                new Date(System.currentTimeMillis())));
    }
}
