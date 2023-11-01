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

package eus.ehu.tfg.ktoda.tollsystem.geoLocation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class GeoLocationExceptionHandler {
    @ExceptionHandler(GeoLocationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleGeoLocationNotFound(GeoLocationNotFoundException e) {
        GeoLocationException exception = new GeoLocationException(
                e.getMessage(),
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleGeoLocationBadRequest(Exception e) {
        GeoLocationException exception = new GeoLocationException(
                e.getMessage(),
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception);
    }
}
