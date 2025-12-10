/* chat.js */
async function sendMessage() {
  const inputField = document.getElementById('userInput');
  const sendBtn = document.getElementById('sendBtn');
  const chatBox = document.getElementById('chatBox');
  const loading = document.getElementById('loading');

  const text = inputField.value.trim();
  if (!text) return;

  // 1. 내 메시지 추가
  addMessage(text, 'user-message');
  inputField.value = '';
  inputField.disabled = true;
  sendBtn.disabled = true;

  // 2. 로딩 표시
  chatBox.appendChild(loading);
  loading.style.display = 'block';
  chatBox.scrollTop = chatBox.scrollHeight;

  try {
    // 3. API 호출
    const response = await fetch('/api/ai/sql', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ input: text })
    });

    if (!response.ok) throw new Error('Network response was not ok');

    const data = await response.json();
    const aiResponse = data.llmResponse ? data.llmResponse : JSON.stringify(data);

    // 4. AI 응답 추가
    loading.style.display = 'none';
    addMessage(aiResponse, 'ai-message');

  } catch (error) {
    loading.style.display = 'none';
    addMessage("오류가 발생했습니다: " + error.message, 'ai-message');
  } finally {
    inputField.disabled = false;
    sendBtn.disabled = false;
    inputField.focus();
  }
}

function addMessage(text, className) {
  const chatBox = document.getElementById('chatBox');
  const msgDiv = document.createElement('div');
  msgDiv.className = `message ${className}`;
  msgDiv.textContent = text;
  chatBox.appendChild(msgDiv);
  chatBox.scrollTop = chatBox.scrollHeight;
}

function handleKeyPress(event) {
  if (event.key === 'Enter') {
    sendMessage();
  }
}