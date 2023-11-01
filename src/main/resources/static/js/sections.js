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

document.addEventListener('DOMContentLoaded', function () {
    const enterPoints = document.getElementById('enterPoints');
    const enterPointsCaption = document.getElementById('captionEnterPoints');
    const enterPointsCaptionText = enterPointsCaption.textContent;
    const exitPoints = document.getElementById('exitPoints');
    const exitPointsCaption = document.getElementById('captionExitPoints');
    const exitPointsCaptionText = exitPointsCaption.textContent;
    const sectionButtonsContainer = document.getElementById('sectionTable');
    const fileFieldContainer = document.getElementById('file-field');
    const detectField = document.getElementById('detectEnter');
    const detectFieldExit = document.getElementById('detectExit');
    let base64BytesFile;
    let token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    let header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');


    sectionButtonsContainer.addEventListener('click', handleSectionButtonClick);
    setupEnterTableListener();
    setupExitTableListener();
    setupFileInputListener();
    setupEnterDateTimeListener();
    setupDetectEnterListener();
    setupDetectExitListener();
    setupDetectEnterListenerV3();
    setupDetectExitListenerV3();

    function handleSectionButtonClick(event) {
        if (event.target.classList.contains('selectSection')) {
            const sectionId = event.target.getAttribute('data-id');
            fetch('eagle/' + sectionId + '/enter')
                .then(response => response.text())
                .then(fragment => {
                    enterPoints.classList.remove('invisible');
                    enterPointsCaption.innerText = `${enterPointsCaptionText} ${sectionId}`;
                    document.querySelector('#enterTable tbody').innerHTML = fragment;
                    document.querySelector('#enterTable').dataset.sectionId = sectionId;

                    resetExitTableContent();
                });
        }
    }

    function resetExitTableContent() {
        exitPoints.classList.add('invisible');
        fileFieldContainer.classList.add('invisible');
        detectField.classList.add('invisible');
        detectFieldExit.classList.add('invisible');
        document.querySelector('#exitTable tbody').innerHTML = '';
    }

    function setupEnterTableListener() {
        const enterTable = document.getElementById('enterTable');
        enterTable.addEventListener('click', function (event) {
            if (event.target.classList.contains('selectPoint')) {
                const sectionId = enterTable.dataset.sectionId;
                const enterId = event.target.getAttribute('data-id');
                fetch(`eagle/${sectionId}/exit`)
                    .then(response => response.text())
                    .then(fragment => {
                        exitPoints.classList.remove('invisible');
                        exitPointsCaption.innerText = `${exitPointsCaptionText} ${sectionId}`;
                        document.querySelector('#exitTable tbody').innerHTML = fragment;
                        document.querySelector('#exitTable').dataset.sectionId = sectionId;
                        document.querySelector('#exitTable').dataset.enterId = enterId;
                    });
            }
        });
    }

    function setupExitTableListener() {
        const exitTable = document.getElementById('exitTable');
        exitTable.addEventListener('click', function (event) {
            if (event.target.classList.contains('selectPoint')) {
                const sectionId = exitTable.dataset.sectionId;
                const enterId = exitTable.dataset.enterId;
                const exitId = event.target.getAttribute('data-id');
                const detectButton = document.querySelector('#detectButtonEnter');
                const detectButtonV3 = document.querySelector('#detectButtonEnterV3');
                const fileField = document.querySelector('#file-field');

                [detectButton, fileField, detectButtonV3].forEach(element => {
                    element.dataset.enterId = enterId;
                    element.dataset.exitId = exitId;
                    element.dataset.sectionId = sectionId;
                });

                fileFieldContainer.classList.remove('invisible');
            }
        });
    }

    function setupFileInputListener() {
        const fileField = document.getElementById('file-field');
        fileFieldContainer.addEventListener('change', function (event) {
            const files = event.target.files;

            if (files.length > 0) {
                const selectedFile = files[0]; // Get the first selected file

                const reader = new FileReader();
                reader.onload = function (event) {
                    const dataUrl = event.target.result;
                    base64BytesFile = dataUrl.split(',')[1];
                };

                let section = document.getElementById('sectionDef');
                let enter = document.getElementById('enterDef');
                let exit = document.getElementById('exitDef');

                section.innerText = `From section: ${fileField.dataset.sectionId}`;
                enter.innerText = `ENTER: ${fileField.dataset.enterId}`;
                exit.innerText = `EXIT: ${fileField.dataset.exitId}`;

                reader.readAsDataURL(selectedFile);
                detectField.classList.remove('invisible');
            }
        });
    }

    function setupEnterDateTimeListener() {
        const enterDateInput = document.getElementById('enterDate');
        const exitDateInput = document.getElementById('exitDate');

        enterDateInput.addEventListener('change', function () {
            const selectedEnterDate = new Date(enterDateInput.value);
            const minExitDate = new Date(selectedEnterDate);
            minExitDate.setHours(selectedEnterDate.getHours() + 4);
            exitDateInput.min = minExitDate.toISOString().slice(0, -8);
        });
    }

    function setupDetectEnterListener() {
        document.querySelector("#detectButtonEnter")
            .addEventListener("click", function (event) {
                detectFieldExit.classList.remove('invisible');

                document.querySelector('#detectButtonExit').dataset.exitId = event.target.dataset.exitId;
                document.querySelector('#detectButtonExitV3').dataset.exitId = event.target.dataset.exitId;

                const pointId = event.target.dataset.enterId;
                const base64File = base64BytesFile.toString();
                const timestamp = document.querySelector("#enterDate").value;

                const data = {
                    pointId: pointId,
                    file_uri: base64File,
                    timestamp: timestamp
                };

                console.log(data);

                fetch("eagle/v2/enter", {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        [header]: token
                    },
                    body: JSON.stringify(data)
                })
                    .then(response => {
                        if (!response.ok) {
                            return response.text().then(errText => {
                                throw new Error(errText);
                            });
                        }
                        return response.json();
                    })
                    .then(data => {
                        alert("Detected OK! ")
                    })
                    .catch(error => {
                        console.log(error);
                        alert("Check logs for errors!" + error.message);
                    });
            });
    }

    function setupDetectExitListener() {
        document.querySelector("#detectButtonExit")
            .addEventListener("click", function (event) {
                const pointId = event.target.dataset.exitId;
                const base64File = base64BytesFile;
                const timestamp = document.querySelector("#exitDate").value;

                const data = {
                    pointId: pointId,
                    file_uri: base64File,
                    timestamp: timestamp
                };

                console.log(data);

                fetch("eagle/v2/exit", {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        [header]: token
                    },
                    body: JSON.stringify(data)
                })
                    .then(response => {
                        if (!response.ok) {
                            return response.text().then(errText => {
                                throw new Error(errText);
                            });
                        }
                        return response.json();
                    })
                    .then(data => {
                        if (data.transactionId) {
                            alert("Transaction Complete! " + data.transactionId);
                        } else {
                            alert("Transaction Incomplete! ");
                        }
                    })
                    .catch(error => {
                        console.log(error.message);
                        alert("Check logs for errors!" + error.message)
                    });
            });
    }

    function setupDetectEnterListenerV3() {
        document.querySelector("#detectButtonEnterV3")
            .addEventListener("click", function (event) {
                detectFieldExit.classList.remove('invisible');

                document.querySelector('#detectButtonExit').dataset.exitId = event.target.dataset.exitId;
                document.querySelector('#detectButtonExitV3').dataset.exitId = event.target.dataset.exitId;

                const pointId = event.target.dataset.enterId;
                const base64File = base64BytesFile.toString();
                const timestamp = document.querySelector("#enterDate").value;

                const data = {
                    pointId: pointId,
                    file_uri: base64File,
                    timestamp: timestamp
                };

                console.log(data);

                fetch("eagle/v3/enter", {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        [header]: token
                    },
                    body: JSON.stringify(data)
                })
                    .then(response => {
                        if (!response.ok) {
                            return response.text().then(errText => {
                                throw new Error(errText);
                            });
                        }
                        return response.json();
                    })
                    .then(data => {
                        alert("Detected OK! ")
                    })
                    .catch(error => {
                        console.log(error);
                        alert("Check logs for errors! " + error.message);
                    });
            });
    }

    function setupDetectExitListenerV3() {
        document.querySelector("#detectButtonExitV3")
            .addEventListener("click", function (event) {
                const pointId = event.target.dataset.exitId;
                const base64File = base64BytesFile;
                const timestamp = document.querySelector("#exitDate").value;

                const data = {
                    pointId: pointId,
                    file_uri: base64File,
                    timestamp: timestamp
                };

                console.log(data);

                fetch("eagle/v3/exit", {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        [header]: token
                    },
                    body: JSON.stringify(data)
                })
                    .then(response => {
                        if (!response.ok) {
                            return response.text().then(errText => {
                                throw new Error(errText);
                            });
                        }
                        return response.json();
                    })
                    .then(data => {
                        if (data.transactionId) {
                            alert("Transaction Complete! " + data.transactionId);
                        } else {
                            alert("Transaction Incomplete! ");
                        }
                    })
                    .catch(error => {
                        console.log(error.message);
                        alert("Check logs for errors! " + error.message)
                    });
            });
    }

});