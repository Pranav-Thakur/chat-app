
const AuthConstants = {
    STORAGE_KEYS: {
        PEER_ID: "peerId",
        USER_ID: "userId",
        CURRENT_USER_DATA: "currentUserData",
        PEER_USER_DATA: "peerUserData",
        SESSION_ID: "sessionId",
        DEVICE_ID: "deviceId",
        LAST_CHECK_SESSION_API_CALL: "lastTimeCheckSessionApiCall",
        LAST_REFRESH_API_CALL: "lastTimeRefreshApiCall"
    },
    TTL: {
        SESSION_TOKEN: 900000, // 15 mins
        REFRESH_TOKEN: 300000, // 5 mins
        CHECK_SESSION: 60000 // 1 min
    },
    API: {
        LOGIN: "/api/v1/auth/login",
        VERIFY: "/api/v1/auth/verify",
        REFRESH: "/api/v1/auth/refresh",
        REGISTER: "/api/v1/user/register",
        CHECK_SESSION: "/api/v1/user/{userId}/check-session",
        FIND: "/api/v1/user/{userId}/find",
        CHAT_LIST: "/api/v1/user/{userId}/chats",
        CHAT_HISTORY: "/api/v1/user/{userId}/chat-history/{otherId}",
        SEND_MSG: "/app/chat.send",
        DELIVERY_ACK: "/app/chat.delivered",
        RECEIVE_MSG: "/topic/messages/{userId}",
        LOGOUT: "/api/v1/user/{userId}/logout"
    },
    COOKIE_KEYS: {
        REFRESH_TOKEN: "refresh_token"
    }
};

// You can optionally freeze it to prevent accidental mutation
Object.freeze(AuthConstants);