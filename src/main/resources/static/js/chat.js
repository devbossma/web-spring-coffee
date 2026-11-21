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

    // A minimal STOMP 1.2 client over the raw WebSocket: just enough of the text frame format
    // (CONNECT/CONNECTED, SUBSCRIBE, SEND, MESSAGE) to talk to Spring's own simple broker, with
    // no external library. Format: "COMMAND\nheader:value\n\nbody\0".
    // See https://stomp.github.io/stomp-specification-1.2.html#STOMP_Frames

    function sendFrame(socket, command, headers, body) {
        let frame = command + '\n';
        for (const key in headers) {
            frame += key + ':' + headers[key] + '\n';
        }
        frame += '\n' + (body || '') + '\0';
        socket.send(frame);
    }

    function parseFrame(raw) {
        const headerEnd = raw.indexOf('\n\n');
        const headPart = headerEnd === -1 ? raw : raw.substring(0, headerEnd);
        const body = headerEnd === -1 ? '' : raw.substring(headerEnd + 2);
        const lines = headPart.split('\n');
        const headers = {};
        for (let i = 1; i < lines.length; i++) {
            const idx = lines[i].indexOf(':');
            if (idx > -1) {
                headers[lines[i].substring(0, idx)] = lines[i].substring(idx + 1);
            }
        }
        return {command: lines[0], headers: headers, body: body};
    }

    const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:';
    const socket = new WebSocket(protocol + '//' + location.host + '/ws/chat');
    const destination = '/topic/chat/' + ORDER_ID;

    socket.onopen = function () {
        sendFrame(socket, 'CONNECT', {'accept-version': '1.2', host: location.host});
    };

    socket.onmessage = function (event) {
        const raw = String(event.data);
        raw.split('\0').forEach(function (chunk) {
            const trimmed = chunk.replace(/^\n+/, '');
            if (!trimmed) {
                return; // heartbeat, or trailing empty chunk after the last frame's null byte
            }
            const frame = parseFrame(trimmed);
            if (frame.command === 'CONNECTED') {
                sendFrame(socket, 'SUBSCRIBE', {id: 'sub-chat', destination: destination});
            } else if (frame.command === 'MESSAGE') {
                const payload = JSON.parse(frame.body);
                appendBubble(payload.sender, payload.text);

                const text = payload.text.toLowerCase();
                if (SENDER === 'CUSTOMER' && payload.sender === 'SYSTEM'
                    && (text.includes('ready') || text.includes('cancelled'))) {
                    setTimeout(function () {
                        window.location.href = '/orders';
                    }, 2500);
                }
            }
        });
    };

    if (form) {
        form.addEventListener('submit', function (event) {
            event.preventDefault();
            const text = input.value.trim();
            if (!text || socket.readyState !== WebSocket.OPEN) {
                return;
            }
            sendFrame(socket, 'SEND', {destination: '/app/chat/' + ORDER_ID, 'content-type': 'application/json'},
                JSON.stringify({sender: SENDER, text: text}));
            input.value = '';
        });
    }
})();
