const toggleBtn = document.getElementById('chat-toggle');
const chatWindow = document.getElementById('chatbot-window');
const closeBtn = document.getElementById('chat-close');
const sendBtn = document.getElementById('send-btn');
const input = document.getElementById('chat-input');
const messages = document.getElementById('chat-messages');

toggleBtn.addEventListener('click', () => {
    chatWindow.style.display = 'flex';
    toggleBtn.style.display = 'none';
});

closeBtn.addEventListener('click', () => {
    chatWindow.style.display = 'none';
    toggleBtn.style.display = 'block';
});

function addMessage(sender, text) {
    const msg = document.createElement('div');
    msg.className = sender === 'user' ? 'text-end text-primary' : 'text-start text-success';
    msg.textContent = text;
    messages.appendChild(msg);
    messages.scrollTop = messages.scrollHeight;
}

function sendMessage() {
    const text = input.value.trim();
    if (!text) return;
    addMessage('user', text);
    input.value = '';
    callChatbot(text);
}

sendBtn.addEventListener('click', () => {
    sendMessage();
});

// ✅ Send on Enter key
input.addEventListener('keydown', (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {  // allows shift+enter for new line if needed
        e.preventDefault();
        sendMessage();
    }
});

function callChatbot(text) {
    fetch('http://localhost:8000/chat', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ message: text })
    })
        .then(response => response.json())
        .then(data => {
            addMessage('bot', data.message || JSON.stringify(data));
        })
        .catch(error => console.error('Error:', error));
}
