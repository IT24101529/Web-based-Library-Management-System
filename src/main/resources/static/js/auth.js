window.Qux = (function(){
    const $ = (sel) => document.querySelector(sel);
    const $$ = (sel) => document.querySelectorAll(sel);

    // Enhanced toggle with smooth animations
    function toggle(mode) {
        const login = $('#loginForm');
        const reg = $('#registerForm');
        const tLogin = $('#tabLogin');
        const tReg = $('#tabRegister');

        if (!login || !reg || !tLogin || !tReg) return;

        // Add slide-out animation to current form
        const currentForm = login.classList.contains('d-none') ? reg : login;
        currentForm.classList.add('form-slide-out');

        setTimeout(() => {
            if (mode === 'login') {
                login.classList.remove('d-none', 'form-slide-out');
                reg.classList.add('d-none');
                tLogin.classList.add('active');
                tReg.classList.remove('active');

                // Trigger slide-in animation
                login.style.animation = 'none';
                login.offsetHeight; // Trigger reflow
                login.style.animation = 'formSlideIn 0.5s cubic-bezier(0.34, 1.56, 0.64, 1)';
            } else {
                login.classList.add('d-none');
                reg.classList.remove('d-none', 'form-slide-out');
                tLogin.classList.remove('active');
                tReg.classList.add('active');

                // Trigger slide-in animation
                reg.style.animation = 'none';
                reg.offsetHeight; // Trigger reflow
                reg.style.animation = 'formSlideIn 0.5s cubic-bezier(0.34, 1.56, 0.64, 1)';
            }
        }, 300);
    }

    function getCsrf(){
        const token = document.querySelector('meta[name="_csrf"]')?.content;
        const header = document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN';
        return { token, header };
    }

    // Enhanced toast with animation
    function showToast(message, type='error'){
        let el = (type === 'success') ? document.getElementById('quxToastSuccess') : document.getElementById('quxToast');
        let body = (type === 'success') ? document.getElementById('quxToastSuccessBody') : document.getElementById('quxToastBody');
        if(body) body.textContent = message;
        if(el){
            // Reset animation
            el.style.animation = 'none';
            el.offsetHeight; // Trigger reflow
            el.style.animation = 'toastSlideIn 0.4s cubic-bezier(0.34, 1.56, 0.64, 1)';

            const toast = bootstrap.Toast.getOrCreateInstance(el);
            toast.show();
        }
    }

    // Enhanced register handler with loading animation
    function registerHandler(e){
        e.preventDefault();
        const submitBtn = document.getElementById('registerBtn');
        const originalText = submitBtn.textContent;

        // Add loading state
        submitBtn.classList.add('loading');
        submitBtn.textContent = 'Creating...';
        submitBtn.disabled = true;

        const username = document.getElementById('regUsername').value.trim();
        const fullName = document.getElementById('regFullName').value.trim();
        const email = document.getElementById('regEmail').value.trim();
        const phone = document.getElementById('regPhone').value.trim();
        const password = document.getElementById('regPassword').value;
        const confirmPassword = document.getElementById('regConfirm').value;

        if(!username || !fullName || !email || !phone || !password || !confirmPassword){
            resetButton();
            showToast('All fields are required', 'error'); 
            return;
        }
        if(password.length < 8){
            resetButton();
            showToast('Password must be at least 8 characters', 'error'); 
            return;
        }
        if(!/^(?=.*[A-Za-z])(?=.*\d).+$/.test(password)){
            resetButton();
            showToast('Password must contain at least one letter and one number', 'error'); 
            return;
        }
        if(password !== confirmPassword){
            resetButton();
            showToast('Passwords do not match', 'error'); 
            return;
        }

        const csrf = getCsrf();
        fetch('/auth/register', {
            method:'POST',
            headers:{
                'Content-Type':'application/json',
                'Accept':'application/json',
                [csrf.header]: csrf.token
            },
            body: JSON.stringify({ username, fullName, email, phone, password, confirmPassword })
        }).then(async res => {
            const data = await res.json().catch(()=>({success:false, message:'Unexpected response'}));
            resetButton();

            if(res.ok && data.success){
                showToast(data.message || 'Registration successful', 'success');

                // Clear form with animation
                const inputs = $$('#registerForm input');
                inputs.forEach(input => {
                    input.style.transform = 'scale(0.95)';
                    setTimeout(() => {
                        input.value = '';
                        input.style.transform = 'scale(1)';
                    }, 150);
                });

                setTimeout(() => toggle('login'), 1500);
            }else{
                showToast(data.message || 'Registration failed', 'error');
            }
        }).catch(() => {
            resetButton();
            showToast('Network error', 'error');
        });

        function resetButton() {
            submitBtn.classList.remove('loading');
            submitBtn.textContent = originalText;
            submitBtn.disabled = false;
        }
    }

    // Enhanced login handler with loading animation
    function loginHandler(e) {
        const submitBtn = e.target.querySelector('button[type="submit"]');
        if (submitBtn) {
            submitBtn.classList.add('loading');
            submitBtn.textContent = 'Signing in...';
            submitBtn.disabled = true;
        }
    }

    // Add floating animations to form inputs
    function addInputAnimations() {
        const inputs = $$('input.form-control');
        inputs.forEach(input => {
            input.addEventListener('focus', function() {
                this.style.transform = 'translateY(-2px)';
                this.style.boxShadow = '0 8px 25px rgba(59, 130, 246, 0.15)';
            });

            input.addEventListener('blur', function() {
                if (!this.value) {
                    this.style.transform = 'translateY(0)';
                    this.style.boxShadow = '';
                }
            });

            // Add typing animation
            input.addEventListener('input', function() {
                this.style.transform = 'scale(1.02)';
                setTimeout(() => {
                    this.style.transform = this === document.activeElement ? 'translateY(-2px)' : 'translateY(0)';
                }, 100);
            });
        });
    }

    // Create floating particles
    function createParticles() {
        const introWrap = $('.intro-wrap');
        if (!introWrap) return;

        const particles = document.createElement('div');
        particles.className = 'particles';

        for (let i = 0; i < 9; i++) {
            const particle = document.createElement('div');
            particle.className = 'particle';
            particle.style.left = (10 + i * 10) + '%';
            particle.style.animationDelay = i + 's';
            particle.style.animationDuration = (6 + Math.random() * 4) + 's';
            particles.appendChild(particle);
        }

        introWrap.appendChild(particles);
    }

    // Add hover effects to interactive elements
    function addHoverEffects() {
        const interactiveElements = $$('button, .btn, input, .logo-img, .small-logo');
        interactiveElements.forEach(el => {
            el.classList.add('interactive-hover');
        });
    }

    // Add ripple effect to buttons
    function addRippleEffect() {
        const buttons = $$('button, .btn');
        buttons.forEach(button => {
            button.addEventListener('click', function(e) {
                const ripple = document.createElement('span');
                const rect = this.getBoundingClientRect();
                const size = Math.max(rect.width, rect.height);
                const x = e.clientX - rect.left - size / 2;
                const y = e.clientY - rect.top - size / 2;

                ripple.style.cssText = `
                    position: absolute;
                    border-radius: 50%;
                    background: rgba(255, 255, 255, 0.6);
                    transform: scale(0);
                    animation: ripple 0.6s ease-out;
                    left: ${x}px;
                    top: ${y}px;
                    width: ${size}px;
                    height: ${size}px;
                    pointer-events: none;
                `;

                this.style.position = 'relative';
                this.style.overflow = 'hidden';
                this.appendChild(ripple);

                setTimeout(() => {
                    ripple.remove();
                }, 600);
            });
        });

        // Add ripple CSS
        const style = document.createElement('style');
        style.textContent = `
            @keyframes ripple {
                to {
                    transform: scale(2);
                    opacity: 0;
                }
            }
        `;
        document.head.appendChild(style);
    }

    // Enhanced modal show/hide with animations
    function enhanceModal() {
        const modal = document.getElementById('authModal');
        if (!modal) return;

        modal.addEventListener('show.bs.modal', function () {
            const modalContent = this.querySelector('.modal-content');
            modalContent.style.animation = 'modalSlideIn 0.5s cubic-bezier(0.34, 1.56, 0.64, 1)';
        });

        modal.addEventListener('hidden.bs.modal', function () {
            // Reset form animations
            const forms = $$('#loginForm, #registerForm');
            forms.forEach(form => {
                form.style.animation = 'none';
                form.classList.remove('form-slide-out');
            });
        });
    }

    function init() {
        const tabLogin = document.getElementById('tabLogin');
        const tabRegister = document.getElementById('tabRegister');
        const regForm = document.getElementById('registerForm');
        const loginForm = document.getElementById('loginForm');
        const switchToRegister = document.getElementById('switchToRegister');
        const switchToLogin = document.getElementById('switchToLogin');

        if (tabLogin) tabLogin.addEventListener('click', () => toggle('login'));
        if (tabRegister) tabRegister.addEventListener('click', () => toggle('register'));
        if (switchToRegister) switchToRegister.addEventListener('click', (e) => {
            e.preventDefault();
            toggle('register');
        });
        if (switchToLogin) switchToLogin.addEventListener('click', (e) => {
            e.preventDefault();
            toggle('login');
        });
        if (regForm) regForm.addEventListener('submit', registerHandler);
        if (loginForm) loginForm.addEventListener('submit', loginHandler);

        // Initialize enhancements
        addInputAnimations();
        createParticles();
        addHoverEffects();
        addRippleEffect();
        enhanceModal();

        // Add entrance animation to main elements
        setTimeout(() => {
            const mainElements = $$('.logo-title, #openAuthBtn');
            mainElements.forEach((el, index) => {
                setTimeout(() => {
                    el.style.opacity = '0';
                    el.style.transform = 'translateY(30px)';
                    el.style.transition = 'all 0.8s cubic-bezier(0.34, 1.56, 0.64, 1)';

                    requestAnimationFrame(() => {
                        el.style.opacity = '1';
                        el.style.transform = 'translateY(0)';
                    });
                }, index * 200);
            });
        }, 300);
    }

    window.addEventListener('DOMContentLoaded', init);

    return { toggle, showToast };
})();