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
    return msg;
}

function sendMessage() {
    const text = input.value.trim();
    if (!text) return;
    addMessage('user', text);
    input.value = '';
    callChatbot_streamResponse(text);
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

async function callChatbot_streamResponse(text) {
    try {
        const response = await fetch('http://localhost:8000/chat/stream', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ message: text })
        });

        const botMsg = addMessage('bot', '');
        let tokenQueue = [];
        let isTyping = false;

        function typeWriter() {
            if (tokenQueue.length > 0) {
                isTyping = true;
                const nextChar = tokenQueue.shift();
                botMsg.textContent += nextChar;
                messages.scrollTop = messages.scrollHeight;
                setTimeout(typeWriter, 20); // Adjust speed here (20ms per char)
            } else {
                isTyping = false;
            }
        }

        const reader = response.body.getReader();
        const decoder = new TextDecoder();
        let buffer = '';

        while (true) {
            const { done, value } = await reader.read();
            if (done) {
                // Ensure typing finishes even if stream ends
                while (tokenQueue.length > 0) {
                    await new Promise(r => setTimeout(r, 50));
                }
                break;
            }

            buffer += decoder.decode(value, { stream: true });
            const lines = buffer.split('\n');
            buffer = lines.pop();

            for (const line of lines) {
                if (line.startsWith('data: ')) {
                    const dataStr = line.substring(6).trim();
                    if (!dataStr) continue;

                    try {
                        const data = JSON.parse(dataStr);
                        if (data.token) {
                            tokenQueue.push(...data.token.split(''));
                            if (!isTyping) typeWriter();
                        }
                        if (data.error) {
                            console.error('Stream error:', data.error);
                        }
                    } catch (e) {
                        console.error('Error parsing JSON:', e);
                    }
                }
            }
        }
    } catch (error) {
        console.error('Error:', error);
        addMessage('bot', 'Sorry, something went wrong.');
    }
}

