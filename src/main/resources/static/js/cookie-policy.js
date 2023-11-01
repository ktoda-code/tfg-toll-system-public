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
    const links = document.querySelectorAll(".left-nav a");

    links.forEach(link => {
        link.addEventListener("click", function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            window.scroll({
                top: target.offsetTop,
                left: 0,
                behavior: 'smooth'
            });
        });
    });
});
