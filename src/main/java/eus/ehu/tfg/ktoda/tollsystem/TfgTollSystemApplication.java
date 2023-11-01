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

package eus.ehu.tfg.ktoda.tollsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication(scanBasePackages = {
        "eus.ehu.tfg.ktoda.tollsystem",
        "eus.ehu.tfg.ktoda.tollinit"
})
public class TfgTollSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(TfgTollSystemApplication.class, args);
        System.out.println("""
                =======================================================
                ================= Toll System Started =================
                =======================================================
                """);
        printPossibleDomains();
    }

    private static void printPossibleDomains() {
        System.out.println();
        System.out.println("http://localhost:8080");
        System.out.println("http://localhost:8080/eagle");
        System.out.println("http://localhost:8080/swagger-ui/index.html");
        System.out.println("http://localhost:8081");
    }
}
