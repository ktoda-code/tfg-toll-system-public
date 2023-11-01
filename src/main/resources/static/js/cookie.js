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

document.addEventListener("DOMContentLoaded", function () {
    const consentModal = new bootstrap.Modal(document.getElementById('cookieConsentModal'), {
        keyboard: false,
        backdrop: 'static'
    });


    const acceptButton = document.getElementById("acceptCookies");
    const rejectButton = document.getElementById("rejectCookies");

    // Check if the user has already made a choice
    const userChoice = localStorage.getItem("cookieConsent");

    if (!userChoice) {
        consentModal.show(); // Show modal if no choice was made before
    }

    acceptButton.addEventListener("click", function () {
        localStorage.setItem("cookieConsent", "accepted");
        // Here, you can also set non-essential cookies since the user accepted
    });

    rejectButton.addEventListener("click", function () {
        localStorage.setItem("cookieConsent", "rejected");
        // Here, ensure that non-essential cookies are NOT set, as the user declined
    });
});
