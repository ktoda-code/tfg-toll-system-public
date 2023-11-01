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

package eus.ehu.tfg.ktoda.tollsystem.jwt;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/authenticate")
public class JwtController {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<String> createAuthenticationToken(@RequestBody JwtRequest jwtRequest) throws Exception {
        log.info("Token enter:");
        authenticate(jwtRequest.email(), jwtRequest.password());

        final UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.email());
        final String token = jwtService.generateToken(userDetails.getUsername());

        log.info("Token authen: {}", token);
        return ResponseEntity.ok(token);
    }

    @GetMapping
    public ResponseEntity<String> getUsernameFromToken(@RequestBody String token) throws Exception {
        return ResponseEntity.ok(jwtService.getUsernameFromToken(token));
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestBody String token) throws Exception {
        return ResponseEntity.ok(jwtService.validateToken(token));
    }

    private void authenticate(String email, String rawPassword) {
        log.info("Authentication:");
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        if (!passwordEncoder.matches(rawPassword, userDetails.getPassword())) {
            log.info("Invalid Credentials");
            throw new BadCredentialsException("Invalid Credentials");
        }
        log.info("Good Credentials");
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        provider.authenticate(new UsernamePasswordAuthenticationToken(email, rawPassword));
    }
}