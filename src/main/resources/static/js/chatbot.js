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
    setTimeout(() => addMessage('bot', 'This is a demo reply.'), 800);
}

sendBtn.addEventListener('click', () => {
    const text = input.value.trim();
    if (!text) return;
    addMessage('user', text);
    input.value = '';
    setTimeout(() => addMessage('bot', 'This is a demo reply.'), 800);
});

// ✅ Send on Enter key
input.addEventListener('keydown', (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {  // allows shift+enter for new line if needed
        e.preventDefault();
        sendMessage();
    }
});
