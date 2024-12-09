// Initialize WebSocket connection
const socket = new SockJS('/chat-websocket');  // Your WebSocket endpoint
let stompClient = Stomp.over(socket);
let userId = getUserIdFromToken();  // Get user ID from JWT token
let currentRoomId = null;
let currentSubscription = null;

// Function to decode JWT and get userId
function getUserIdFromToken() {
    const token = localStorage.getItem("jwtToken");
    if (token) {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.userId;
    }
    return null;
}

function connect() {
    var socket = new SockJS('/chat-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, subscribeToRoom, onError);
}

function onError(error) {
    console.log('Could not connect to WebSocket server. Please refresh this page to try again!');
}

function subscribeToRoom() {
    if (currentSubscription) {
        currentSubscription.unsubscribe();
        console.log(`Unsubscribed from /topic/${currentRoomId}`);
    }

    // regis new topic
    currentSubscription = stompClient.subscribe(`/topic/` + currentRoomId, function(response) {
        const message = JSON.parse(response.body);
        addMessageToChat(message);
    });


}

// Set the user's avatar dynamically if available
function loadUserAvatar() {
    const userAvatar = document.getElementById("userAvatar");

    // Example API call to fetch user info
    axios.get(`/api/user/`, { params: { userId: userId } })
        .then(response => {
            const user = response.data;
            if (user.avatar) {
                userAvatar.src = user.avatar;
            }
        })
        .catch(error => {
            console.error("Error fetching user info:", error);
            // Use default avatar if fetching fails
            userAvatar.src = "default-avatar.png";
        });
}

// Load chat history for a specific room
function loadChatHistory(roomId) {
    currentRoomId = roomId;
    subscribeToRoom(roomId);

    const chatHistoryContainer = document.querySelector("#chatHistory");
    chatHistoryContainer.innerHTML = ''; // Clear previous chat history

    axios.get(`/message/room-id/${roomId}`)
        .then(response => {
            const messages = response.data;
            messages.forEach(message => {
                addMessageToChat(message);
            });
            chatHistoryContainer.scrollTop = chatHistoryContainer.scrollHeight;
            connect();
        })
        .catch(error => {
            console.error("Error loading chat history:", error);
        });
}

// Add message to the chat
function addMessageToChat(message) {
    const chatHistoryContainer = document.querySelector("#chatHistory");

    // Tạo container cha cho tin nhắn
    const messageWrapper = document.createElement("div");
    messageWrapper.classList.add("message-wrapper");

     // Tạo container cho tên người gửi
    const nameDiv = document.createElement("div");
    nameDiv.classList.add("message-name");
    nameDiv.textContent = message.name;

    // Tạo container cho nội dung tin nhắn
    const messageDiv = document.createElement("div");
    messageDiv.classList.add("message-content");

    // Kiểm tra xem tin nhắn là của bạn hay của người khác
    if (message.userId === userId) {
        messageWrapper.classList.add("sent"); // Tin nhắn của bạn
    } else {
        messageWrapper.classList.add("received"); // Tin nhắn của người khác
    }

    messageDiv.textContent = message.content;

    messageWrapper.appendChild(nameDiv);
    messageWrapper.appendChild(messageDiv);

    chatHistoryContainer.appendChild(messageWrapper);

    // Auto-scroll to the bottom
    chatHistoryContainer.scrollTop = chatHistoryContainer.scrollHeight;
}

// Send a new message via WebSocket
function sendMessage() {
    const messageInput = document.querySelector("#messageInput");
    const messageContent = messageInput.value.trim();

    if (messageContent && currentRoomId) {
        const message = {
            content: messageContent,
        };

        // Send the message over STOMP to the backend
        stompClient.send("/app/chat.sendMessage/" + currentRoomId, {}, JSON.stringify(message));

        messageInput.value = '';
    }
}

// Load rooms and chat when the page is loaded
function loadRooms() {
    if (!userId) return;

    axios.get(`/api/room/joined-room/${userId}`)
        .then(response => {
            const rooms = response.data;
            const sidebar = document.querySelector("#sidebar .list-group");

            rooms.forEach(room => {
                // Create a wrapper for the room tab and menu
                const roomWrapper = document.createElement("div");
                roomWrapper.classList.add("room-wrapper");
                roomWrapper.style.position = "relative";

                // Three-dot menu button
                const menuButton = document.createElement("span");
                menuButton.classList.add("menu-button");
                menuButton.textContent = "⋮";

                // Dropdown menu
                const menuDropdown = document.createElement("div");
                menuDropdown.classList.add("menu-dropdown");




                // Fetch the user's role in the room
                axios.get(`/api/user-room/room-role`, { params: { userId, roomId: room.id } })
                    .then(roleResponse => {
                        const role = roleResponse.data; // "HOST" or "MEMBER"

                        // Populate menu based on role
                        if (role === "HOST") {
                            menuDropdown.innerHTML = `
                                <div class="menu-item rename-room">Rename</div>
                                <div class="menu-item delete-room">Delete</div>
                            `;
                        } else if (role === "MEMBER") {
                            menuDropdown.innerHTML = `
                                <div class="menu-item leave-room">Leave</div>
                            `;
                        }

                        // Add event listeners for menu actions
                        if (role === "HOST") {
                            menuDropdown.querySelector(".rename-room").addEventListener("click", () => {
                                const newName = prompt(`Enter a new name for room "${room.name}":`);
                                if (newName) renameRoom(room.id, newName);
                            });

                            menuDropdown.querySelector(".delete-room").addEventListener("click", () => {
                                if (confirm(`Are you sure you want to delete room "${room.name}"?`)) {
                                    deleteRoom(room.id);
                                }
                            });
                        } else if (role === "MEMBER") {
                            menuDropdown.querySelector(".leave-room").addEventListener("click", () => {
                                if (confirm(`Are you sure you want to leave room "${room.name}"?`)) {
                                    leaveRoom(room.id);
                                }
                            });
                        }
                    })
                    .catch(roleError => {
                        console.error(`Error fetching role for room ${room.name}:`, roleError);
                    });



                // Close dropdown on clicking outside
                document.addEventListener("click", () => {
                    menuDropdown.style.display = "none";
                });

                // Room tab element
                const roomTab = document.createElement("a");
                roomTab.href = "#";
                roomTab.classList.add("list-group-item", "list-group-item-action");
                roomTab.textContent = room.name;
                roomTab.dataset.roomId = room.id;
                roomTab.onclick = () => {
                    //check if clicked this room
                    if (currentRoomId === room.id) {
                        return; // Do nothing if this room is already active
                    }

                    currentRoomId = room.id;
                    showChatView(room.name);
                    loadChatHistory(room.id);
                    loadUserList();
                    updateRoomCode(room.code);

                    document.querySelectorAll(".room-wrapper").forEach(tab => tab.classList.remove("active"));
                    roomWrapper.classList.add("active");
                };

                // Toggle dropdown menu on click
                menuButton.addEventListener("click", (e) => {
                    e.stopPropagation(); // Prevent closing from global click handler
                    const isVisible = menuDropdown.style.display === "block";
                    document.querySelectorAll(".menu-dropdown").forEach(menu => menu.style.display = "none");
                    menuDropdown.style.display = isVisible ? "none" : "block";
                });

                // Append elements
                roomWrapper.appendChild(roomTab);
                roomWrapper.appendChild(menuButton);
                roomWrapper.appendChild(menuDropdown);
                sidebar.appendChild(roomWrapper);
            });

        })
        .catch(error => {
            console.error("Error fetching rooms:", error);
        });
}

// Helper functions
function renameRoom(roomId, newName) {
    axios.put(`/api/room/rename`, null, {
            params: { roomId: roomId, roomName: newName },
        })
        .then(() => {
            alert("Room renamed successfully.");
            location.reload(); // Reload to reflect changes
        })
        .catch(error => {
            console.error("Error renaming room:", error);
        });
}
function deleteRoom(roomId) {
    axios.delete(`/api/room`, {
            params: { roomId: roomId },
        })
        .then(() => {
            alert("Room deleted successfully.");
            location.reload();
        })
        .catch(error => {
            console.error("Error deleting room:", error);
        });
}
function leaveRoom(roomId) {
    axios.delete(`/api/user-room/leave-room`, {
            params: { userId: userId, roomId: roomId },
        })
        .then(() => {
            alert("You have left the room.");
            location.reload();
        })
        .catch(error => {
                    console.error("Error leaving room:", error);
                    if (error.response) {
                        alert(error.response.data.message || "Failed to leave room.");
                    }
                });
}

function updateRoomCode(roomCode) {
    const roomCodeElement = document.getElementById("roomCode");
    roomCodeElement.textContent = `Room Code: ${roomCode}`;
}

//load user list
let currentAbortController = null;
let lastRoomId = null;
async function loadUserList() {

    const userListContainer = document.getElementById("userList");

    // Cancel any ongoing request
    if (currentAbortController) {
        currentAbortController.abort();
    }

    // Create a new AbortController for the current request
    currentAbortController = new AbortController();
    const { signal } = currentAbortController;

    userListContainer.innerHTML = "";

    if (lastRoomId === currentRoomId) {
        return;
    }
    lastRoomId = currentRoomId;

    try {

        // Fetch host of the room
        const hostResponse = await axios.get(`/api/user-room/host`,{
            params: { roomId: currentRoomId },
            signal,
        });
        const hostId = hostResponse.data.id;

        const isHost = await isUserHost();
        const response = await axios.get(`/api/user/room-id`, {
            params: {roomId : currentRoomId},
            signal,
        });
        const users = response.data;

        // Sort users to place the host on top
        users.sort((a, b) => {
            if (a.id === hostId) return -1; // Host first
            if (b.id === hostId) return 1;
            return a.name.localeCompare(b.name); // Sort the rest alphabetically
        });

        users.forEach(user => {
            const userDiv = document.createElement("div");
            userDiv.style.display = "flex";
            userDiv.style.alignItems = "center";
            userDiv.style.marginBottom = "10px";

            const avatarImg = document.createElement("img");
            avatarImg.src = user.avatar;
            avatarImg.alt = user.name;
            avatarImg.style.width = "40px";
            avatarImg.style.height = "40px";
            avatarImg.style.borderRadius = "50%";
            avatarImg.style.marginRight = "10px";

            const userName = document.createElement("span");
            userName.textContent = user.name;

            userDiv.appendChild(avatarImg);
            userDiv.appendChild(userName);

            // Add crown icon for the host
            if (hostId === user.id) {
                const crownIcon = document.createElement("img");
                crownIcon.src = "https://cdn-icons-png.flaticon.com/128/91/91202.png";
                crownIcon.alt = "Host";
                crownIcon.style.width = "20px";
                crownIcon.style.height = "20px";
                crownIcon.style.marginLeft = "10px";

                userDiv.appendChild(crownIcon);
            }

            if (isHost && user.id !== userId) {
                // Add trash icon
                const trashIcon = document.createElement("span");
                trashIcon.innerHTML = "🗑️";
                trashIcon.classList.add("trash-icon");
                trashIcon.style.marginLeft = "10px";
                trashIcon.style.cursor = "pointer";
                trashIcon.style.visibility = "hidden"; // Hide by default

                // Show on hover
                userDiv.addEventListener("mouseover", () => {
                    trashIcon.style.visibility = "visible";
                });
                userDiv.addEventListener("mouseout", () => {
                    trashIcon.style.visibility = "hidden";
                });

                // Delete confirmation box
                trashIcon.addEventListener("click", () => {
                    const confirmDelete = confirm(`Are you sure you want to remove ${user.name} from the room?`);
                    if (confirmDelete) {
                        removeUserFromRoom(user.id);
                    }
                });

                userDiv.appendChild(trashIcon);
            }

            userListContainer.appendChild(userDiv);
        });
    } catch (error) {
        if (error.name === "CanceledError") {
            console.warn("Request canceled:", error.message);
        } else {
            console.error("Failed to load user list:", error);
            userListContainer.innerHTML = `<p style="color: red;">Failed to load users</p>`;
        }
    }
}

async function removeUserFromRoom(userIdToRemove) {
    try {
        await axios.delete(`/api/user-room`, {
            params: {
                userId: userIdToRemove,
                roomId: currentRoomId,
            },
        });
        loadUserList(); // Refresh the user list after removal
    } catch (error) {
        console.error(`Failed to remove user with ID ${userIdToRemove}:`, error);
        alert("Failed to remove the user. Please try again.");
    }
}

async function isUserHost() {
    try {
        const response = await axios.get(`/api/user-room/room-role`, {
            params: { userId: userId, roomId: currentRoomId },
        });
        return response.data === "HOST";
    } catch (error) {
        console.error("Error checking user role:", error);
        return false;
    }
}



// Function to initialize the Join Room feature
function initializeJoinRoom() {
    const joinRoomBtn = document.getElementById("joinRoomBtn");
    const joinRoomModal = document.getElementById("joinRoomModal");
    const modalOverlay = document.getElementById("modalOverlay");
    const closeJoinRoomModal = document.getElementById("closeJoinRoomModal");
    const joinRoomSubmitBtn = document.getElementById("joinRoomSubmitBtn");
    const notification = document.getElementById("joinRoomNotification");

    // Show the modal when the "Join Room" button is clicked
    joinRoomBtn.addEventListener("click", function () {
        joinRoomModal.style.display = "block";
        modalOverlay.style.display = "block";
    });

    // Hide the modal when the "Cancel" button is clicked
    closeJoinRoomModal.addEventListener("click", function () {
        joinRoomModal.style.display = "none";
        modalOverlay.style.display = "none";
        notification.textContent = ""; // Clear notifications when modal is closed
    });

    // Handle the "Join Room" submission
    joinRoomSubmitBtn.addEventListener("click", async function () {
        const roomCode = document.getElementById("roomCodeInput").value.trim();

        if (!roomCode) {
            notification.style.color = "red";
            notification.textContent = "Please enter a room code.";
            return;
        }

        joinRoomByCode(roomCode);
    });
}

async function joinRoomByCode(roomCode) {
    const notification = document.getElementById("joinRoomNotification");
    try {
        const response = await axios.post(`/api/room/join-room`, null, {
            params: { code: roomCode, userId },
        });

        const responseMessage = response.data.roomStatus;
        console.log("response room status : " + responseMessage);

        if (responseMessage === "private") {
            // Room is private, notify the user
            notification.style.color = "green"; // Success color
            notification.textContent = "Request to join room sent successfully.";
        } else {
            const joinedRoom = response.data; // Room data from API
            addRoomToSidebar(joinedRoom);

            // Hide the join room modal and overlay
            const joinRoomModal = document.getElementById("joinRoomModal");
            const modalOverlay = document.getElementById("modalOverlay");
            joinRoomModal.style.display = "none";
            modalOverlay.style.display = "none";
        }
    } catch (error) {
         if (error.response) {
             // Extract message from API response
             const errorMessage = error.response.data?.message || "An unexpected error occurred.";
             notification.style.color = "red";
             notification.textContent = errorMessage;
         } else {
             console.error("Unexpected error:", error);
             notification.style.color = "red";
             notification.textContent = "An unexpected error occurred.";
        }
    }
}

// Function to initialize the Create Room feature
function initializeCreateRoom() {
    const createRoomBtn = document.getElementById("createRoomBtn");
    const createRoomModal = document.getElementById("createRoomModal");
    const modalOverlay = document.getElementById("modalOverlay");
    const closeCreateRoomModal = document.getElementById("closeCreateRoomModal");
    const createRoomSubmitBtn = document.getElementById("createRoomSubmitBtn");

    // Show the modal when the "Create Room" button is clicked
    createRoomBtn.addEventListener("click", function () {
        createRoomModal.style.display = "block";
        modalOverlay.style.display = "block";
    });

    // Hide the modal when the "Cancel" button is clicked
    closeCreateRoomModal.addEventListener("click", function () {
        createRoomModal.style.display = "none";
        modalOverlay.style.display = "none";
    });

    // Handle the "Create Room" submission
    createRoomSubmitBtn.addEventListener("click", async function () {
        const roomName = document.getElementById("roomNameInput").value.trim();
        const roomType = document.getElementById("roomType").value;
        const notification = document.getElementById("createRoomNotification");

        if (!roomName) {
            notification.textContent = "Room name is required.";
            notification.style.color = "red";
            return;
        }
        createRoomAndAddToSidebar(roomName,roomType)
    });
}

// Function for creating a room and then adding it to the sidebar
function createRoomAndAddToSidebar(roomName, roomType) {
    axios.post(`/api/room/create-room`, null, {
        params: {roomName, userId, roomType}
    })
    .then(response => {
        const newRoom = response.data; // Room data from API
        addRoomToSidebar(newRoom);

        // Hide the create room modal and overlay
        const createRoomModal = document.getElementById("createRoomModal");
        const modalOverlay = document.getElementById("modalOverlay");
        createRoomModal.style.display = "none";
        modalOverlay.style.display = "none";

    })
    .catch(error => {
        console.error("Error creating room:", error);
    });
}

//helper function to add room to side bar :
function addRoomToSidebar(room) {
    const sidebar = document.querySelector("#sidebar");

    // Create a wrapper for the room tab and menu
    const roomWrapper = document.createElement("div");
    roomWrapper.classList.add("room-wrapper");
    roomWrapper.style.position = "relative";

    // Three-dot menu button
    const menuButton = document.createElement("span");
    menuButton.classList.add("menu-button");
    menuButton.textContent = "⋮";

    // Dropdown menu
    const menuDropdown = document.createElement("div");
    menuDropdown.classList.add("menu-dropdown");

    // Fetch the user's role in the room
    axios.get(`/api/user-room/room-role`, { params: { userId, roomId: room.id } })
        .then(roleResponse => {
            const role = roleResponse.data; // "HOST" or "MEMBER"

            if (role === "HOST") {
                menuDropdown.innerHTML = `
                    <div class="menu-item rename-room">Rename Room</div>
                    <div class="menu-item delete-room">Delete Room</div>
                `;
                menuDropdown.querySelector(".rename-room").addEventListener("click", () => {
                    const newName = prompt(`Enter a new name for room "${room.name}":`);
                    if (newName) renameRoom(room.id, newName);
                });

                menuDropdown.querySelector(".delete-room").addEventListener("click", () => {
                    if (confirm(`Are you sure you want to delete room "${room.name}"?`)) {
                        deleteRoom(room.id);
                    }
                });
            } else if (role === "MEMBER") {
                menuDropdown.innerHTML = `
                    <div class="menu-item leave-room">Leave Room</div>
                `;
                menuDropdown.querySelector(".leave-room").addEventListener("click", () => {
                    if (confirm(`Are you sure you want to leave room "${room.name}"?`)) {
                        leaveRoom(room.id);
                    }
                });
            }
        })
        .catch(error => console.error(`Error fetching role for room ${room.name}:`, error));

    // Close dropdown on clicking outside
    document.addEventListener("click", () => {
        menuDropdown.style.display = "none";
    });

    // Room tab element
    const roomTab = document.createElement("a");
    roomTab.href = "#";
    roomTab.classList.add("list-group-item", "list-group-item-action");
    roomTab.textContent = room.name;

    roomTab.onclick = () => {
        if (currentRoomId === room.id) {
            console.log("Already in the selected room:", room.name);
            return;
        }

        currentRoomId = room.id;
        showChatView(room.name);
        loadChatHistory(room.id);
        loadUserList();
        updateRoomCode(room.code);

        // Highlight the active room and remove highlight from others
        document.querySelectorAll(".room-wrapper").forEach(tab => tab.classList.remove("active"));
        roomWrapper.classList.add("active");
    };

    // Toggle dropdown menu on click
    menuButton.addEventListener("click", (e) => {
        e.stopPropagation();
        const isVisible = menuDropdown.style.display === "block";
        document.querySelectorAll(".menu-dropdown").forEach(menu => menu.style.display = "none");
        menuDropdown.style.display = isVisible ? "none" : "block";
    });

    // Append elements
    roomWrapper.appendChild(roomTab);
    roomWrapper.appendChild(menuButton);
    roomWrapper.appendChild(menuDropdown);
    sidebar.appendChild(roomWrapper);

    // Automatically select the new/joined room
    roomTab.click();
}

function initializeUserMenu() {
    const userAvatarContainer = document.getElementById("userAvatarContainer");
    const userOptionsMenu = document.getElementById("userOptionsMenu");

    if (!userAvatarContainer || !userOptionsMenu) {
        console.error("User menu elements not found. Make sure the HTML structure is correct.");
        return;
    }

    // Show menu on hover
    userAvatarContainer.addEventListener("mouseover", () => {
        userOptionsMenu.style.display = "block";
    });

    // Hide menu when mouse leaves both avatar and menu
    userAvatarContainer.addEventListener("mouseout", () => {
        setTimeout(() => {
            if (!userOptionsMenu.matches(':hover') && !userAvatarContainer.matches(':hover')) {
                userOptionsMenu.style.display = "none";
            }
        }, 100);
    });

    userOptionsMenu.addEventListener("mouseout", () => {
        setTimeout(() => {
            if (!userOptionsMenu.matches(':hover') && !userAvatarContainer.matches(':hover')) {
                userOptionsMenu.style.display = "none";
            }
        }, 100);
    });

    // Event listeners for menu options
    document.getElementById("changeAvatarOption").addEventListener("click", () => {
        const avatarFileInput = document.createElement("input");
        avatarFileInput.type = "file";
        avatarFileInput.accept = "image/*";
        avatarFileInput.style.display = "none";

        avatarFileInput.addEventListener("change", async () => {
            const file = avatarFileInput.files[0];
            if (!file) return;

            const formData = new FormData();
            formData.append("file", file);
            formData.append("userId", userId);

            try {
                const response = await axios.put("/api/user/avatar-upload", formData, {
                    headers: {
                        "Content-Type": "multipart/form-data",
                    },
                });

                // Update avatar dynamically
                const userAvatar = document.getElementById("userAvatar");
                userAvatar.src = response.data.avatar; // Assuming the API returns the updated avatar URL
                alert("Avatar updated successfully!");
            } catch (error) {
                console.error("Error updating avatar:", error);
                alert("Failed to update avatar. Please try again.");
            }
        });

        document.body.appendChild(avatarFileInput);
        avatarFileInput.click();
    });

    document.getElementById("changeNameOption").addEventListener("click", () => {
        const newName = prompt("Enter your new name:");
        if (!newName || newName.trim() === "") {
            alert("Name cannot be empty!");
            return;
        }

        axios.put("/api/user/update-name", null, {
            params: {
                userId,
                name: newName.trim(),
            },
        }).then(() => {
            alert("Name updated successfully!");
            // Optionally update name in UI if displayed elsewhere
        }).catch(error => {
            if (error.response) {
                // Handle validation errors from the backend
                const errorMessage = error.response.data.message || "Failed to update name.";
                alert(`Error: ${errorMessage}`);
            } else {
                // Handle other errors (e.g., network issues)
                console.error("Error updating name:", error);
                alert("An unexpected error occurred. Please try again.");
            }
        });
    });

    document.getElementById("logoutOption").addEventListener("click", () => {
        // Send the logout request to the backend API
        fetch('/logout', {
            method: 'POST',  // You can use POST or GET based on your setup
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('jwtToken')  // Assuming you're storing the token in localStorage
            }
        })
        .then(response => {
            if (response.ok) {
                // If the logout request is successful, redirect to the home page
                window.location.href = '/login';
            } else {
                // Handle error case
                alert('Logout failed');
            }
        })
        .catch(error => {
            console.error('Logout error:', error);
            alert('Logout error');
        });
    });
}

function showRequestList(){
    const showRequestButton = document.getElementById('show-requests-btn');
    const requestsList = document.getElementById('join-requests-list');

    showRequestButton.addEventListener('click', function() {
        fetch(`/api/join-form/pending?userId=${userId}`)
            .then(response => response.json())
            .then(requests => {
                const requestsList = document.getElementById('join-requests-list');
                requestsList.innerHTML = ''; // Clear previous requests

                if (requests.length === 0) {
                    requestsList.innerHTML = 'No pending requests.';
                } else {
                    requests.forEach(request => {
                        const requestItem = document.createElement('div');
                        requestItem.classList.add('request-item');
                        requestItem.innerHTML = `
                            <p><strong>${request.sender}</strong> wants to join your room <strong>${request.roomName}</strong></p>
                            <button class="accept-btn" data-request-id="${request.id}">Accept</button>
                            <button class="deny-btn" data-request-id="${request.id}">Deny</button>
                        `;
                        requestsList.appendChild(requestItem);
                    });
                }
                // Toggle the visibility of the request list
                requestsList.style.display = requestsList.style.display === 'none' ? 'block' : 'none';
            })
            .catch(error => {
                console.error('Error fetching join requests:', error);
            });
    });

    // Hide dropdown when clicking outside
    document.addEventListener('click', function () {
        requestsList.style.display = 'none';
    });

    // Handle accept and deny button clicks
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('accept-btn') || e.target.classList.contains('deny-btn')) {
            const joinFormId = e.target.getAttribute('data-request-id');
            const action = e.target.classList.contains('accept-btn') ? 'accept' : 'deny';
            handleRequestAction(joinFormId, action);
        }
    });
}


function handleRequestAction(joinFormId, action) {
    // Send request to backend to handle accept/deny action
    fetch(`/api/join-form/handle-form?joinFormId=${joinFormId}&action=${action}`, {
        method: 'POST',  // Or GET depending on your API
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => {
        if (response.ok) {
            alert(`${action.charAt(0).toUpperCase() + action.slice(1)}ed the request.`);
        } else {
            alert('Error handling the request');
        }
    })
    .catch(error => {
        console.error('Error handling request action:', error);
    });
}



loadRooms();