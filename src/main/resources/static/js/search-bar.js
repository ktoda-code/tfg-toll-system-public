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

class TrieNode {
    constructor() {
        this.children = {}; // Holds the child nodes (characters)
        this.isEndOfWord = false; // To indicate the end of a word
    }
}

class Trie {
    constructor() {
        this.root = new TrieNode();
    }

    // Inserts a word into the trie
    insert(word) {
        let node = this.root;
        for (let char of word) {
            if (!node.children[char]) {
                node.children[char] = new TrieNode();
            }
            node = node.children[char];
        }
        node.isEndOfWord = true;
    }

    // Searches the trie and returns suggestions
    search(prefix) {
        let node = this.root;
        for (let char of prefix) {
            if (!node.children[char]) {
                return [];
            }
            node = node.children[char];
        }

        let results = [];
        this._findWordsWithPrefix(node, prefix, results);
        return results;
    }

    // Helper function to find words with a given prefix
    _findWordsWithPrefix(node, currentWord, results) {
        if (node.isEndOfWord) {
            results.push(currentWord);
        }
        for (let char in node.children) {
            this._findWordsWithPrefix(
                node.children[char],
                currentWord + char,
                results
            );
        }
    }
}

const words = [
    "Home", "API Docs", "About", "Sections", "Sign",
    "Profile", "Dashboard", "Cookies", "Term & Privacy",
    "Journeys", "Transactions"];
const wordDescriptions = {
    Home: "Return to Home page",
    "API Docs": "Explore our comprehensive API documentation",
    Sections: "Discover sections nationwide with ease",
    Profile: "Manage and update your personal profile",
    About: "Learn about our organization's mission and values",
    Sign: "Join us in driving innovation at auToll",
    Dashboard: "Access a centralized view of your auToll data",
    Cookies: "Learn about our cookie usage policy",
    "Term & Privacy": "Review our terms of service and privacy policy",
    Journeys: "Track and review all your journeys with auToll",
    Transactions: "View a detailed log of all your financial transactions"
};

const wordUrls = {
    Home: "http://localhost:8080",
    "API Docs": "http://localhost:8080/docs",
    Sections: "http://localhost:8080/sections",
    Profile: "http://localhost:8080/profile",
    About: "http://localhost:8080/about",
    Sign: "http://localhost:8080/sign",
    Dashboard: "http://localhost:8080/dashboard",
    Cookies: "http://localhost:8080/cookie-policy",
    "Term & Privacy": "http://localhost:8080/terms-privacy",
    Journeys: "http://localhost:8080/journeys",
    Transactions: "http://localhost:8080/transactions"
};

const trie = new Trie();
words.forEach((word) => {
    trie.insert(word.toLowerCase());
    trie.insert(word);
});

function showSuggestions(query) {// Log the input

    let suggestionsList = document.getElementById("suggestions-list");
    suggestionsList.innerHTML = "";

    if (!query.trim()) {
        return;
    }

    let results = trie.search(query.toLowerCase());

    let filteredResults = results.filter((result, index) => {
        return (
            results.indexOf(result) === index &&
            words.some((word) => word.toLowerCase() === result)
        );
    });

    filteredResults.forEach((result) => {
        let originalWord = words.find((word) => word.toLowerCase() === result);
        let a = document.createElement("a");
        a.innerText = originalWord;
        a.href = wordUrls[originalWord];
        a.onclick = function () {
            handleSuggestionClick(originalWord);
        };

        let description = document.createElement("span");
        description.innerText = wordDescriptions[originalWord];
        description.className = "description"; // to style it separately if needed
        // description.style.color = "grey";

        let container = document.createElement("li");
        container.style.marginLeft = "10px";
        container.style.padding = "10px";
        container.appendChild(a);
        container.appendChild(description);

        suggestionsList.appendChild(container);
    });
}

function handleSuggestionClick(word) {
    console.log(`Clicked on: ${word}`);
    // Handle the click event here (e.g., navigate to another page, fill the search box, etc.)
}
