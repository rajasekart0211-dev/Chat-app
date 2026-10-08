let nameBox = document.querySelector('#name-box');
let chatBox = document.querySelector('#chat-box');

let nameSubmit = document.querySelector('.name-submit');
let nameInput = document.querySelector('#name');

let messageBox = document.querySelector('#messages');
let sendBtn = document.querySelector('#send');
let msgInput = document.querySelector('#m');

let name = null;
let stomp = null;

// Temporary test IDs
const senderId = 1;
const receiverId = 3;
let conversationId = null;


// ==============================
// JOIN CHAT
// ==============================

nameSubmit.addEventListener('click', (e) => {
    e.preventDefault();

    name = nameInput.value.trim();

    if (name.length === 0) return;

    nameBox.classList.add('hidden');

    chatBox.classList.remove('hidden');
    chatBox.classList.add('flex');

    getConversation();
});


// ==============================
// SEND BUTTON
// ==============================

sendBtn.addEventListener('click', (e) => {
    e.preventDefault();

    sendMessage();
});


// ==============================
// ENTER KEY
// ==============================

msgInput.addEventListener('keydown', (e) => {

    if (e.key === 'Enter') {
        e.preventDefault();
        sendMessage();
    }

});

const getConversation = async () => {

    try {

        const response = await fetch(
            `/api/chat/personal?user1Id=${senderId}&user2Id=${receiverId}`
        );

        // No conversation exists yet
        if (response.status === 404 || response.status === 204) {
            console.log("No conversation exists yet");

            conversationId = null;

            connect();

            return;
        }

        if (!response.ok) {
            throw new Error(
                `Failed to fetch conversation: ${response.status}`
            );
        }

        const text = await response.text();

        // Empty response
        if (!text) {
            console.log("No conversation exists yet");

            conversationId = null;

            connect();

            return;
        }

        const conversation = JSON.parse(text);

        conversationId = conversation.id;

        console.log("Conversation ID:", conversationId);

        await loadMessages();

        connect();

    } catch (error) {

        console.error("Error fetching conversation:", error);

    }
};
// ==============================
// CONNECT TO WEBSOCKET
// ==============================

const connect = () => {

    const socket = new SockJS("/ws");

    stomp = Stomp.over(socket);

    stomp.connect({}, () => {

        console.log("WebSocket connected");

        subscribeToUserMessages();

        if (conversationId) {

            stomp.subscribe(
                `/topic/conversation/${conversationId}`,
                (receivedMsg) => {

                    const msg = JSON.parse(receivedMsg.body);

                    console.log("Conversation message:", msg);

                    displayMessage(msg);
                }
            );
        }

    }, (error) => {

        console.error("WebSocket connection error:", error);

    });
};


// ==============================
// SEND MESSAGE
// ==============================

const sendMessage = () => {

    const text = msgInput.value.trim();

    if (text.length === 0) return;

    if (!stomp || !stomp.connected) {
        console.error("WebSocket is not connected");
        return;
    }

    const message = {
        senderId: senderId,
        receiverId: receiverId,
        content: text
    };

    console.log("Sending:", message);

    stomp.send(
        "/chat/personal",
        {},
        JSON.stringify(message)
    );

    msgInput.value = "";
};


// ==============================
// DISPLAY MESSAGE
// ==============================

const displayMessage = (message) => {

    const li = document.createElement('li');

    li.classList.add(
        'p-3',
        'rounded-xl',
        'bg-slate-800',
        'border',
        'border-slate-700'
    );

    const sentTime = new Date(message.sentAt).toLocaleTimeString([], {
        hour: '2-digit',
        minute: '2-digit'
    });

    li.innerHTML = `
        <div class="flex items-center justify-between">

            <span class="font-semibold text-blue-400">
                ${message.senderName}
            </span>

            <span class="text-xs text-slate-500">
                ${sentTime}
            </span>

        </div>

        <p class="mt-1 text-slate-200">
            ${message.content}
        </p>
    `;

    messageBox.appendChild(li);

    messageBox.parentElement.scrollTop =
        messageBox.parentElement.scrollHeight;
};

const loadMessages = async () => {

    if (!conversationId) {
        console.log("No conversation yet");
        return;
    }

    try {

        const response = await fetch(
            `/api/chat/conversations/${conversationId}/messages`
        );

        if (!response.ok) {
            throw new Error("Failed to load messages");
        }

        const messages = await response.json();

        console.log("Message history:", messages);

        messages.forEach(message => {
            displayMessage(message);
        });

    } catch (error) {

        console.error("Error loading messages:", error);

    }
};

const subscribeToUserMessages = () => {

    stomp.subscribe(
        `/topic/user/${senderId}`,
        (receivedMsg) => {

            const message = JSON.parse(receivedMsg.body);

            console.log("User message:", message);

            handleIncomingMessage(message);
        }
    );
};

const handleIncomingMessage = (message) => {

    // New conversation
    if (!conversationId) {

        conversationId = message.conversationId;

        console.log(
            "New conversation created:",
            conversationId
        );

        stomp.subscribe(
            `/topic/conversation/${conversationId}`,
            (receivedMsg) => {

                const msg = JSON.parse(receivedMsg.body);

                console.log("Conversation message:", msg);

                displayMessage(msg);
            }
        );

        displayMessage(message);

        return;
    }

    // Existing conversation:
    // conversation topic will handle the display.
};