(function() {
    // Prevent multiple injections if it's a soft navigation in an SPA
    if (window.__aiRecorderInitialized) {
        if (typeof window.__aiRecorderUpdateState === 'function') {
            window.__aiRecorderUpdateState("PAGE_CHANGE", "Navigated to " + window.location.pathname);
        }
        return;
    }
    window.__aiRecorderInitialized = true;

    const init = () => {
        if (!document.body || !document.head) {
            requestAnimationFrame(init);
            return;
        }

        // 1. Setup CSS for visual cues
        const style = document.createElement('style');
        style.innerHTML = `
            .ai-click-marker {
                position: absolute; width: 30px; height: 30px; 
                background: rgba(255, 69, 0, 0.8); border: 2px solid white;
                border-radius: 50%; z-index: 100000; pointer-events: none;
                transform: translate(-50%, -50%); animation: pulse 0.8s forwards;
            }
            @keyframes pulse { from { scale: 0.5; opacity: 1; } to { scale: 1.5; opacity: 0; } }
            
            .ai-hud {
                position: fixed; top: 0; left: 0; width: 100%; height: 40px;
                background: #000; color: #0f0; font-family: monospace;
                z-index: 100001; display: flex; align-items: center; 
                padding: 0 15px; font-size: 14px; border-bottom: 2px solid #0f0;
            }

            .ai-input-highlight { outline: 4px solid #00ff00 !important; }
        `;
        document.head.appendChild(style);

        // 2. Create the State HUD (Heads-Up Display)
        const hud = document.createElement('div');
        hud.className = 'ai-hud';
        
        const hudText = document.createElement('span');
        hudText.id = 'ai-recording-hud-text';
        hudText.innerHTML = `STATE: IDLE | URL: ${window.location.pathname}`;
        
        const finishBtn = document.createElement('button');
        finishBtn.id = 'ai-recording-finish-btn';
        finishBtn.innerHTML = 'Stop & Generate AI Test';
        finishBtn.style.cssText = 'margin-left: auto; padding: 5px 15px; background: red; color: white; border: none; font-weight: bold; cursor: pointer; border-radius: 3px; font-size: 14px;';
        finishBtn.onclick = (e) => {
            e.stopPropagation();
            window.__aiRecorderFinished = true;
            hudText.innerHTML = 'FINALIZING VIDEO AND UPLOADING TO GEMINI AI...';
            finishBtn.style.background = 'gray';
            finishBtn.innerText = 'Processing...';
        };

        hud.appendChild(hudText);
        hud.appendChild(finishBtn);
        document.documentElement.appendChild(hud);
        
        // Push the native body down so our fixed header behaves like a safe frameset!
        document.body.style.transform = "translateY(40px)";

        let log = JSON.parse(sessionStorage.getItem('__aiActionLog') || '[]');
        const updateHUD = (action, detail = "") => {
            hudText.innerHTML = `ACTION: ${action.toUpperCase()} ${detail} | URL: ${window.location.pathname}`;
            log.push({ time: new Date().toISOString(), url: window.location.pathname, action, detail });
            sessionStorage.setItem('__aiActionLog', JSON.stringify(log));
        };
        
        window.__aiRecorderUpdateState = updateHUD;

        // 3. Reveal Passwords & Highlight Inputs
        const revealAndWatch = () => {
            document.querySelectorAll('input:not([data-ai-watched]), select:not([data-ai-watched]), textarea:not([data-ai-watched])').forEach(input => {
                // Mark this input so we don't attach listeners multiple times!
                input.setAttribute('data-ai-watched', 'true');
                
                if (input.tagName === 'INPUT' && input.type === 'password') input.type = 'text';
                
                input.addEventListener('focus', () => {
                    input.classList.add('ai-input-highlight');
                    updateHUD("Focusing Input", `#${input.id || input.name || 'unnamed'}`);
                });

                input.addEventListener('blur', () => {
                    input.classList.remove('ai-input-highlight');
                });

                input.addEventListener('input', (e) => {
                    if (e.target.tagName === 'SELECT') return;
                    if (['radio', 'checkbox', 'button', 'submit', 'color', 'file', 'image'].includes(e.target.type)) return;
                    let inputName = e.target.placeholder || e.target.getAttribute('aria-label') || e.target.name || e.target.id || 'input';
                    inputName = inputName.replace(/[-_]/g, ' ').trim();
                    updateHUD("Typing", `"${e.target.value}" into "${inputName}"`);
                });

                input.addEventListener('change', (e) => {
                    if (e.target.tagName === 'SELECT') {
                        let label = null;
                        if (e.target.id) label = document.querySelector(`label[for="${e.target.id}"]`);
                        let selectName = (label && label.innerText) ? label.innerText : (e.target.getAttribute('aria-label') || e.target.name || e.target.id || 'select');
                        selectName = selectName.replace(/[-_]/g, ' ').trim();
                        let optionText = e.target.options[e.target.selectedIndex]?.text || e.target.value;
                        updateHUD("Selecting", `"${optionText}" from "${selectName}"`);
                    } else if (e.target.tagName === 'INPUT' && (e.target.type === 'checkbox' || e.target.type === 'radio')) {
                        let label = e.target.id ? document.querySelector(`label[for="${e.target.id}"]`) : null;
                        let name = (label && label.innerText) ? label.innerText : (e.target.getAttribute('aria-label') || e.target.name || e.target.id || 'checkbox');
                        name = name.replace(/[-_]/g, ' ').trim();
                        updateHUD("Clicking", `"${name}"`);
                    }
                });
            });
        };

        const getElementIdentifier = (target) => {
            let name = "";
            if (['INPUT', 'SELECT', 'TEXTAREA'].includes(target.tagName)) {
                let label = null;
                if (target.id) {
                    label = document.querySelector(`label[for="${target.id}"]`);
                }
                if (label && label.innerText) name = label.innerText;
                else if (target.placeholder) name = target.placeholder;
                else if (target.getAttribute('aria-label')) name = target.getAttribute('aria-label');
                else if (target.title) name = target.title;
                else if (target.name) name = target.name.replace(/[-_]\d+$/, '').replace(/[-_]/g, ' '); 
                else if (target.id) name = target.id.replace(/[-_]\d+$/, '').replace(/[-_]/g, ' '); 
            } else {
                name = target.getAttribute('aria-label') || target.title || target.innerText || "";
                if (!name && target.querySelector('img')) {
                    const img = target.querySelector('img');
                    name = img.alt || img.title || "";
                }
            }
            
            if (!name && target.id) {
                 name = target.id.replace(/[-_]\d+$/, '').replace(/[-_]/g, ' ');
            }
            
            if (name) {
                name = name.replace(/\(\d+\s*[^)]*\)/g, '');
                name = name.replace(/[\n\r]+/g, ' ').replace(/\s{2,}/g, ' ').trim();
                name = name.substring(0, 40).trim();
            }
            
            let identifier = name ? `"${name}"` : target.tagName.toLowerCase();
            
            if (name) {
                let allSimilar = Array.from(document.querySelectorAll(target.tagName)).filter(el => {
                    if (el.offsetWidth === 0 && el.offsetHeight === 0 && el.getClientRects().length === 0) return false;
                    let elName = "";
                    if (['INPUT', 'SELECT', 'TEXTAREA'].includes(el.tagName)) {
                        let l = el.id ? document.querySelector(`label[for="${el.id}"]`) : null;
                        elName = (l && l.innerText) ? l.innerText : (el.placeholder || el.getAttribute('aria-label') || el.title || (el.name && el.name.replace(/[-_]\d+$/, '')) || (el.id && el.id.replace(/[-_]\d+$/, '')) || "");
                    } else {
                        elName = el.getAttribute('aria-label') || el.title || el.innerText || (el.querySelector('img') ? (el.querySelector('img').alt || el.querySelector('img').title) : "");
                    }
                    if (!elName && el.id) {
                        elName = el.id.replace(/[-_]\d+$/, '');
                    }
                    if (elName) {
                        elName = elName.replace(/[-_]/g, ' ').replace(/\(\d+\s*[^)]*\)/g, '').replace(/[\n\r]+/g, ' ').replace(/\s{2,}/g, ' ').trim().substring(0, 40);
                    }
                    return elName === name;
                });

                let currentName = name;
                if (allSimilar.length > 1) {
                    let p = target.parentElement;
                    let depth = 0;
                    
                    while (allSimilar.length > 1 && p && p !== document.body && depth < 5) {
                        let pName = "";
                        if (p.getAttribute('aria-label')) pName = p.getAttribute('aria-label');
                        else if (p.id && !p.id.includes('@')) pName = p.id.replace(/[-_]\d+$/, '').replace(/[-_]/g, ' ');
                        else if (p.className && typeof p.className === 'string' && !p.className.includes('@')) {
                            let cls = p.className.trim().split(/\s+/)[0];
                            if (cls && cls.length > 3 && !cls.includes('hover') && !cls.includes('active') && !cls.includes('col') && !cls.includes('row')) {
                                pName = cls.replace(/[-_]/g, ' ');
                            }
                        } else if (['header', 'footer', 'nav', 'form', 'section', 'article'].includes(p.tagName.toLowerCase())) {
                            pName = p.tagName.toLowerCase();
                        }
                        
                        if (pName) {
                            currentName = `${currentName} in ${pName}`;
                            
                            allSimilar = allSimilar.filter(el => {
                                if (el === target) return true;
                                let op = el.parentElement;
                                let od = 0;
                                while (op && op !== document.body && od < depth + 1) {
                                    let opName = "";
                                    if (op.getAttribute('aria-label')) opName = op.getAttribute('aria-label');
                                    else if (op.id && !op.id.includes('@')) opName = op.id.replace(/[-_]\d+$/, '').replace(/[-_]/g, ' ');
                                    else if (op.className && typeof op.className === 'string' && !op.className.includes('@')) {
                                        let cls = op.className.trim().split(/\s+/)[0];
                                        if (cls && cls.length > 3) opName = cls.replace(/[-_]/g, ' ');
                                    } else if (['header', 'footer', 'nav', 'form', 'section', 'article'].includes(op.tagName.toLowerCase())) {
                                        opName = op.tagName.toLowerCase();
                                    }
                                    if (opName === pName) return true;
                                    op = op.parentElement;
                                    od++;
                                }
                                return false;
                            });
                        }
                        p = p.parentElement;
                        depth++;
                    }
                    
                    if (allSimilar.length > 1) {
                        let index = allSimilar.indexOf(target) + 1;
                        if (index > 0) currentName = `${currentName} (match ${index} of ${allSimilar.length})`;
                    }
                }
                
                if (currentName !== name) {
                     let suffix = currentName.substring(name.length);
                     identifier = `"${name}"${suffix}`;
                } else {
                     identifier = `"${name}"`;
                }
            }
            return identifier;
        };

        // 4. Enhanced Click Handling (Visual + Delay)
        window.addEventListener('click', (e) => {
            // Let synthetically delayed clicks from our own script pass through!
            if (e.__aiDelayed) return;

            // Do not track clicks on our own recorder UI
            if (e.target.closest('#ai-recording-finish-btn') || e.target.closest('.ai-hud')) {
                return;
            }

            // Identify the element for the AI (using natural language)
            const target = e.target.closest('button, a, input, select') || e.target;
            const identifier = getElementIdentifier(target);

            // Visual Marker Creation
            const marker = document.createElement('div');
            marker.className = 'ai-click-marker';
            marker.style.left = `${e.pageX}px`;
            marker.style.top = `${e.pageY}px`;
            document.documentElement.appendChild(marker);
            setTimeout(() => marker.remove(), 800);
            
            updateHUD("Clicking", identifier);
            console.log(`AI_OBSERVER: Clicked ${identifier} at ${e.pageX}, ${e.pageY}`);

            // To ensure the video recorder captures the HUD and pulse before a fast page completely unloads, 
            // we actively intercept and delay native clicks on interactive elements for 1.5 seconds.
            if (e.isTrusted && (target.tagName === 'A' || target.tagName === 'BUTTON' || (target.tagName === 'INPUT' && (target.type === 'submit' || target.type === 'button')))) {
                e.preventDefault();
                e.stopPropagation();

                setTimeout(() => {
                    const clickEvent = new MouseEvent("click", {
                        "view": window,
                        "bubbles": true,
                        "cancelable": true,
                        "clientX": e.clientX,
                        "clientY": e.clientY
                    });
                    clickEvent.__aiDelayed = true;
                    
                    const dispatched = target.dispatchEvent(clickEvent);
                    
                    // Fallback for native navigations if the synthetic event doesn't trigger it natively
                    if (dispatched) {
                        if (target.tagName === 'A' && target.href) {
                            if (target.target === '_blank') { window.open(target.href); }
                            else { window.location.href = target.href; }
                        } else if (target.tagName === 'BUTTON' && target.type === 'submit') {
                            const form = target.closest('form');
                            if (form) form.submit();
                        } else if (target.tagName === 'INPUT' && target.type === 'submit') {
                            const form = target.closest('form');
                            if (form) form.submit();
                        }
                    }
                }, 1500); // 1.5 second UI lag injection
            }

        }, true);
        
        let hoverTimeout;
        window.addEventListener('mouseover', (e) => {
            clearTimeout(hoverTimeout);
            if (e.target.closest('#ai-recording-finish-btn') || e.target.closest('.ai-hud')) return;
            
            // Wait 800ms to see if user is actually hovering, rather than just moving mouse across screen
            hoverTimeout = setTimeout(() => {
                const target = e.target.closest('button, a, input, select, li, [role="button"], [role="menuitem"], .dropdown') || e.target;
                
                // If it's just the document body or HTML, ignore
                if (target === document.body || target === document.documentElement) return;

                const identifier = getElementIdentifier(target);
                // We only log hover if we got a reasonably decent identifier
                if (identifier && identifier !== `"${target.tagName.toLowerCase()}"`) {
                    updateHUD("Hovering", identifier);
                    console.log(`AI_OBSERVER: Hovering over ${identifier}`);
                }
            }, 800);
        }, true);

        window.addEventListener('mouseout', () => {
            clearTimeout(hoverTimeout);
        }, true);

        // 5. Enhanced Key Handling for Input Enters
        // This is necessary because pressing Enter on an input natively submits a form without a trusted click!
        window.addEventListener('keydown', (e) => {
            if (e.__aiDelayed) return;
            
            if (e.key === 'Enter') {
                const target = e.target;
                
                // Only intercept Enter if it's inside an input element that natively causes submission
                if (target.tagName === 'INPUT' && target.closest('form')) {
                    e.preventDefault();
                    e.stopPropagation();
                    
                    let name = target.placeholder || target.getAttribute('aria-label') || target.name || target.id || 'input';
                    name = name.replace(/[-_]/g, ' ').trim();
                    const identifier = `"${name}"`;
                    updateHUD("Pressing Enter", `in ${identifier}`);
                    console.log(`AI_OBSERVER: Pressed Enter in ${identifier}`);
                    
                    setTimeout(() => {
                        const kw = new KeyboardEvent('keydown', { key: 'Enter', code: 'Enter', keyCode: 13, bubbles: true, cancelable: true });
                        kw.__aiDelayed = true;
                        
                        // Fire synthetic event for framework listeners, then forcefully submit form if not prevented
                        if(target.dispatchEvent(kw)) {
                            target.closest('form').submit();
                        }
                    }, 1500);
                }
            }
        }, true);

        // Initial run and watch for DOM changes (for SPAs)
        revealAndWatch();
        const observer = new MutationObserver((mutations) => {
            let shouldCheck = false;
            for (let m of mutations) {
                if (m.addedNodes && m.addedNodes.length > 0) {
                    shouldCheck = true;
                    break;
                }
            }
            if (shouldCheck) {
                revealAndWatch();
            }
        });
        observer.observe(document.body, { childList: true, subtree: true });

        console.log("Gemini Video Analysis Helper Active.");
    };
    
    init();
})();
