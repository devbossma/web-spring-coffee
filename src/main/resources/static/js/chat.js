(function () {
    if (!ORDER_ID) {
        return;
    }

    const transcript = document.getElementById('transcript');
    const form = document.getElementById('chat-form');
    const input = document.getElementById('chat-text');

    function scrollToBottom() {
        transcript.scrollTop = transcript.scrollHeight;
    }

    function appendBubble(sender, text) {
        const bubble = document.createElement('div');
        bubble.className = 'bubble ' + sender;
        const who = document.createElement('span');
        who.className = 'who';
        who.textContent = sender;
        const body = document.createElement('span');
        body.textContent = text;
        bubble.appendChild(who);
        bubble.appendChild(body);
        transcript.appendChild(bubble);
        scrollToBottom();
    }

    scrollToBottom();

    const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:';
    const socket = new WebSocket(protocol + '//' + location.host + '/ws/chat?orderId=' + ORDER_ID);

    socket.onmessage = function (event) {
        const payload = JSON.parse(event.data);
        appendBubble(payload.sender, payload.text);

        const text = payload.text.toLowerCase();
        if (SENDER === 'CUSTOMER' && payload.sender === 'SYSTEM'
            && (text.includes('ready') || text.includes('cancelled'))) {
            setTimeout(function () {
                window.location.href = '/orders';
            }, 2500);
        }
    };

    if (form) {
        form.addEventListener('submit', function (event) {
            event.preventDefault();
            const text = input.value.trim();
            if (!text || socket.readyState !== WebSocket.OPEN) {
                return;
            }
            socket.send(JSON.stringify({sender: SENDER, text: text}));
            input.value = '';
        });
    }
})();
