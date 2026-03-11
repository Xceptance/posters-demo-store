/**
 * Credit Card Form — Client-side validation, vendor detection,
 * auto-formatting, MM/YY input, auto-tab, and Luhn check.
 *
 * Mirrors the server-side CreditCardVendor BIN prefix table.
 * No build step — vanilla ES2020+.
 */
(function () {
    'use strict';

    // ---------------------------------------------------------------
    // BIN prefix table (mirrors CreditCardVendor.java)
    // ---------------------------------------------------------------
    const VENDORS = [
        {
            name: 'visa', display: 'Visa',
            prefixes: [{ lo: '4', hi: '4' }],
            lengths: [13, 16, 19], cvv: 3, groups: [4, 4, 4, 4]
        },
        {
            name: 'mastercard', display: 'Mastercard',
            prefixes: [{ lo: '51', hi: '55' }, { lo: '2221', hi: '2720' }],
            lengths: [16], cvv: 3, groups: [4, 4, 4, 4]
        },
        {
            name: 'amex', display: 'American Express',
            prefixes: [{ lo: '34', hi: '34' }, { lo: '37', hi: '37' }],
            lengths: [15], cvv: 4, groups: [4, 6, 5]
        },
        {
            name: 'unionpay', display: 'UnionPay',
            prefixes: [{ lo: '62', hi: '62' }],
            lengths: [16, 17, 18, 19], cvv: 3, groups: [4, 4, 4, 4]
        },
        {
            name: 'jcb', display: 'JCB',
            prefixes: [{ lo: '3528', hi: '3589' }],
            lengths: [16, 17, 18, 19], cvv: 3, groups: [4, 4, 4, 4]
        },
        {
            name: 'discover', display: 'Discover',
            prefixes: [{ lo: '6011', hi: '6011' }, { lo: '644', hi: '649' }, { lo: '65', hi: '65' }],
            lengths: [16, 17, 18, 19], cvv: 3, groups: [4, 4, 4, 4]
        },
        {
            name: 'diners', display: 'Diners Club',
            prefixes: [{ lo: '300', hi: '305' }, { lo: '36', hi: '36' }, { lo: '38', hi: '38' }],
            lengths: [14, 16], cvv: 3, groups: [4, 6, 4]
        },
        {
            name: 'maestro', display: 'Maestro',
            prefixes: [
                { lo: '5018', hi: '5018' }, { lo: '5020', hi: '5020' },
                { lo: '5038', hi: '5038' }, { lo: '5893', hi: '5893' },
                { lo: '6304', hi: '6304' }, { lo: '6759', hi: '6759' },
                { lo: '6761', hi: '6761' }, { lo: '6762', hi: '6762' },
                { lo: '6763', hi: '6763' }
            ],
            lengths: [12, 13, 14, 15, 16, 17, 18, 19], cvv: 3, groups: [4, 4, 4, 4]
        }
    ];

    // ---------------------------------------------------------------
    // Vendor detection
    // ---------------------------------------------------------------
    function prefixMatches(digits, range) {
        const len = range.lo.length;
        if (digits.length < len) {
            const pLo = range.lo.substring(0, digits.length);
            const pHi = range.hi.substring(0, digits.length);
            return digits >= pLo && digits <= pHi;
        }
        const prefix = digits.substring(0, len);
        return prefix >= range.lo && prefix <= range.hi;
    }

    function detectVendor(digits) {
        for (const vendor of VENDORS) {
            for (const range of vendor.prefixes) {
                if (prefixMatches(digits, range)) {
                    return vendor;
                }
            }
        }
        return null;
    }

    // ---------------------------------------------------------------
    // Luhn checksum
    // ---------------------------------------------------------------
    function isLuhnValid(digits) {
        if (!digits || digits.length === 0) return false;
        let sum = 0;
        let double = false;
        for (let i = digits.length - 1; i >= 0; i--) {
            let n = digits.charCodeAt(i) - 48;
            if (double) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            double = !double;
        }
        return sum % 10 === 0;
    }

    // ---------------------------------------------------------------
    // Auto-format card number with spaces
    // ---------------------------------------------------------------
    function formatCardNumber(digits, groups) {
        const g = groups || [4, 4, 4, 4];
        let result = '';
        let pos = 0;
        for (const size of g) {
            if (pos >= digits.length) break;
            if (result.length > 0) result += ' ';
            result += digits.substring(pos, pos + size);
            pos += size;
        }
        // Append any remaining digits beyond groups
        if (pos < digits.length) {
            result += ' ' + digits.substring(pos);
        }
        return result;
    }

    // ---------------------------------------------------------------
    // DOM Ready
    // ---------------------------------------------------------------
    document.addEventListener('DOMContentLoaded', function () {
        const form = document.getElementById('payment-form');
        if (!form) return;

        const cardNumberInput = document.getElementById('cardNumber');
        const expiryInput = document.getElementById('expiry');
        const cvvInput = document.getElementById('cvv');
        const vendorDisplay = document.getElementById('vendor-display');
        const vendorName = document.getElementById('vendor-name');
        const ariaLive = document.getElementById('cc-aria-live');

        let currentVendor = null;

        // ---- Card Number: input event (auto-format + vendor detect) ----
        if (cardNumberInput) {
            cardNumberInput.addEventListener('input', function (e) {
                const digits = this.value.replace(/\D/g, '');
                const vendor = detectVendor(digits);
                const groups = vendor ? vendor.groups : [4, 4, 4, 4];

                // Max digits based on vendor
                const maxLen = vendor ? Math.max(...vendor.lengths) : 19;
                const trimmed = digits.substring(0, maxLen);

                // Format
                const formatted = formatCardNumber(trimmed, groups);
                const cursorWasAtEnd = this.selectionStart === this.value.length;
                this.value = formatted;
                if (cursorWasAtEnd) {
                    this.setSelectionRange(formatted.length, formatted.length);
                }

                // Update vendor display
                updateVendorDisplay(vendor);

                // Update CVV maxlength
                if (cvvInput && vendor) {
                    cvvInput.maxLength = vendor.cvv;
                    cvvInput.placeholder = vendor.cvv === 4 ? '1234' : '123';
                }

                // Clear error on typing
                clearFieldError(cardNumberInput);

                // Auto-tab to expiry when max length reached
                if (vendor && trimmed.length === Math.max(...vendor.lengths)) {
                    if (expiryInput) expiryInput.focus();
                }
            });

            // Handle paste — strip non-digits, reformat
            cardNumberInput.addEventListener('paste', function (e) {
                e.preventDefault();
                const pasted = (e.clipboardData || window.clipboardData).getData('text');
                const digits = pasted.replace(/\D/g, '');
                this.value = digits;
                this.dispatchEvent(new Event('input', { bubbles: true }));
            });
        }

        // ---- Expiry: MM/YY with auto-slash ----
        if (expiryInput) {
            expiryInput.addEventListener('input', function (e) {
                let value = this.value.replace(/[^\d/]/g, '');
                const digits = value.replace(/\D/g, '');

                if (digits.length >= 2) {
                    value = digits.substring(0, 2) + '/' + digits.substring(2, 4);
                } else {
                    value = digits;
                }

                this.value = value;
                clearFieldError(expiryInput);

                // Auto-tab to CVV when complete
                if (digits.length === 4 && cvvInput) {
                    cvvInput.focus();
                }
            });

            // Handle backspace on the slash
            expiryInput.addEventListener('keydown', function (e) {
                if (e.key === 'Backspace' && this.value.length === 3 && this.value[2] === '/') {
                    this.value = this.value.substring(0, 2);
                    e.preventDefault();
                }
            });
        }

        // ---- CVV: restrict to digits only ----
        if (cvvInput) {
            cvvInput.addEventListener('input', function () {
                this.value = this.value.replace(/\D/g, '');
                clearFieldError(cvvInput);
            });
        }

        // ---- Vendor display update ----
        function updateVendorDisplay(vendor) {
            if (!vendorDisplay) return;

            if (vendor && vendor !== currentVendor) {
                vendorDisplay.classList.add('cc-vendor-active');
                if (vendorName) vendorName.textContent = vendor.display;
                // ARIA announcement
                if (ariaLive) ariaLive.textContent = vendor.display + ' card detected';
            } else if (!vendor && currentVendor) {
                vendorDisplay.classList.remove('cc-vendor-active');
                if (vendorName) vendorName.textContent = '';
                if (ariaLive) ariaLive.textContent = '';
            }
            currentVendor = vendor;
        }

        // ---- Validation on blur ----
        function validateField(input) {
            if (!input) return true;

            const id = input.id;
            const digits = (input.value || '').replace(/\D/g, '');

            if (id === 'cardNumber') {
                if (digits.length === 0) {
                    return showFieldError(input, 'Please enter a card number.');
                }
                if (!isLuhnValid(digits)) {
                    return showFieldError(input, 'Please enter a valid credit card number.');
                }
                const vendor = detectVendor(digits);
                if (vendor && !vendor.lengths.includes(digits.length)) {
                    return showFieldError(input, 'Card number length is invalid for ' + vendor.display + '.');
                }
            }

            if (id === 'cardName') {
                if (!input.value || input.value.trim() === '') {
                    return showFieldError(input, 'Please enter the cardholder name.');
                }
            }

            if (id === 'expiry') {
                const val = input.value || '';
                const match = val.match(/^(\d{2})\/(\d{2})$/);
                if (!match) {
                    return showFieldError(input, 'Please enter expiry in MM/YY format.');
                }
                const month = parseInt(match[1], 10);
                const year = parseInt(match[2], 10) + 2000;
                if (month < 1 || month > 12) {
                    return showFieldError(input, 'Month must be between 01 and 12.');
                }
                const now = new Date();
                const curYear = now.getFullYear();
                const curMonth = now.getMonth() + 1;
                if (year < curYear || (year === curYear && month < curMonth)) {
                    return showFieldError(input, 'Card is expired.');
                }
                if (year > curYear + 20) {
                    return showFieldError(input, 'Expiry date is too far in the future.');
                }
            }

            if (id === 'cvv') {
                const cardDigits = cardNumberInput ? cardNumberInput.value.replace(/\D/g, '') : '';
                const vendor = detectVendor(cardDigits);
                const expected = vendor ? vendor.cvv : 3;
                if (digits.length !== expected) {
                    return showFieldError(input, 'CVV must be ' + expected + ' digits.');
                }
            }

            clearFieldError(input);
            return true;
        }

        // ---- Error display helpers ----
        function showFieldError(input, message) {
            input.classList.add('is-invalid');
            let feedback = input.parentElement.querySelector('.invalid-feedback');
            if (!feedback) {
                feedback = document.createElement('div');
                feedback.className = 'invalid-feedback';
                input.parentElement.appendChild(feedback);
            }
            feedback.textContent = message;
            return false;
        }

        function clearFieldError(input) {
            if (!input) return;
            input.classList.remove('is-invalid');
            const feedback = input.parentElement.querySelector('.invalid-feedback');
            if (feedback) feedback.textContent = '';
        }

        // ---- Attach blur validation ----
        [cardNumberInput, expiryInput, cvvInput,
         document.getElementById('cardName')].forEach(function (input) {
            if (input) {
                input.addEventListener('blur', function () {
                    validateField(this);
                });
            }
        });

        // ---- Form submit: validate all + strip formatting ----
        if (form) {
            form.addEventListener('submit', function (e) {
                let valid = true;
                const fields = [cardNumberInput, document.getElementById('cardName'),
                                expiryInput, cvvInput];

                fields.forEach(function (input) {
                    if (!validateField(input)) {
                        valid = false;
                    }
                });

                if (!valid) {
                    e.preventDefault();
                    // Focus first invalid field
                    const firstInvalid = form.querySelector('.is-invalid');
                    if (firstInvalid) firstInvalid.focus();
                    return;
                }

                // Strip spaces from card number before submit
                if (cardNumberInput) {
                    cardNumberInput.value = cardNumberInput.value.replace(/\D/g, '');
                }
            });
        }
    });
})();
