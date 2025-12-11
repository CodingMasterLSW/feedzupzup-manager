/* login.js */
async function handleLogin(event) {
  event.preventDefault();

  const username = document.getElementById('username').value.trim();
  const password = document.getElementById('password').value.trim();
  const errorMessage = document.getElementById('errorMessage');
  const loginBtn = document.getElementById('loginBtn');

  // 초기화
  errorMessage.textContent = '';
  loginBtn.disabled = true;

  try {
    const response = await fetch('/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        id: username,
        password: password
      })
    });

    if (response.ok) {
      // 로그인 성공 - 메인 페이지로 이동
      window.location.href = '/';
    } else if (response.status === 401) {
      errorMessage.textContent = '아이디 또는 비밀번호가 올바르지 않습니다.';
    } else {
      errorMessage.textContent = '로그인 중 오류가 발생했습니다.';
    }
  } catch (error) {
    errorMessage.textContent = '서버와 통신 중 오류가 발생했습니다.';
    console.error('Login error:', error);
  } finally {
    loginBtn.disabled = false;
  }
}