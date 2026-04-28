document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const errorMessage = document.getElementById('errorMessage');

    try {
        const response = await fetch('http://localhost:8080/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password })
        });

        if (response.ok) {
            const data = await response.json();
            console.log('Respuesta completa:', data);
            localStorage.setItem('token', data.token);
            console.log(data);
            const roleName = data.role;

            errorMessage.textContent = `¡Bienvenido! Login exitoso. Rol: ${roleName}`;
            errorMessage.classList.remove('error-message');
            errorMessage.classList.add('show', 'success-message');

        } else {
            errorMessage.textContent = 'Correo o contraseña inválidos';
            errorMessage.classList.add('show');
        }
    } catch (error) {
        errorMessage.textContent = 'Error de conexión. Intenta más tarde.';
        console.error(error);
        errorMessage.classList.add('show');
    }
});
