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

package eus.ehu.tfg.ktoda.tollsystem.error;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class CustomErrorController implements ErrorController {
    private static final Map<Integer, ErrorMessage> errorMessages = new HashMap<>();

    static {
        errorMessages.put(HttpStatus.NOT_FOUND.value(), new ErrorMessage("404", "Content Not Found"));
        errorMessages.put(HttpStatus.INTERNAL_SERVER_ERROR.value(), new ErrorMessage("500", "Internal Server Error"));
        errorMessages.put(HttpStatus.BAD_REQUEST.value(), new ErrorMessage("400", "Bad Request"));
        errorMessages.put(HttpStatus.UNAUTHORIZED.value(), new ErrorMessage("401", "Unauthorized"));
        errorMessages.put(HttpStatus.FORBIDDEN.value(), new ErrorMessage("403", "Forbidden"));
        errorMessages.put(HttpStatus.METHOD_NOT_ALLOWED.value(), new ErrorMessage("405", "Method Not Allowed"));
        errorMessages.put(HttpStatus.REQUEST_TIMEOUT.value(), new ErrorMessage("408", "Request Timeout"));
        errorMessages.put(HttpStatus.CONFLICT.value(), new ErrorMessage("409", "Conflict"));
        errorMessages.put(HttpStatus.PRECONDITION_FAILED.value(), new ErrorMessage("412", "Precondition Failed"));
        errorMessages.put(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), new ErrorMessage("415", "Unsupported Media Type"));
        errorMessages.put(HttpStatus.TOO_MANY_REQUESTS.value(), new ErrorMessage("429", "Too Many Requests"));
        errorMessages.put(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS.value(), new ErrorMessage("451", "Unavailable For Legal Reasons"));
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            ErrorMessage errorMessage = errorMessages.get(statusCode);

            if (errorMessage != null) {
                model.addAttribute("errorMessage", errorMessage.code());
                model.addAttribute("errorDescription", errorMessage.description());
            } else {
                model.addAttribute("errorMessage", statusCode);
                model.addAttribute("errorDescription", "We encountered an error processing your request");
            }


        } else {
            model.addAttribute("errorMessage", "ERROR");
            model.addAttribute("errorDescription", "Unexpected error occurred");
        }
        return "error";
    }

    private record ErrorMessage(String code, String description) {
    }
}
