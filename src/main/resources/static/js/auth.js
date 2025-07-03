const AuthService = (() => {

    async function apiFetch(url, options = {}, retry = true) {
        const defaultOptions = {
            method: "GET",
            credentials: "include", // 🔐 this sends HttpOnly cookies
            headers: {
                "Content-Type": "application/json"
            }
        };

        try {
            const res = await fetch(url, { ...defaultOptions, ...options });

            if (res.status === 401 && retry) {
                const refreshCalled = getItemWithExpiry(AuthConstants.STORAGE_KEYS.LAST_REFRESH_API_CALL);
                if (refreshCalled) {
                    console.warn("Token expired. already refresh called, so stopping...");
                    return null;
                }

                console.warn("Token expired. Trying refresh...");
                let deviceId = getItemWithExpiry(AuthConstants.STORAGE_KEYS.DEVICE_ID);
                const refreshRes = await fetch(AuthConstants.API.REFRESH + "?deviceId=" + deviceId, defaultOptions);

                setItemWithExpiry(AuthConstants.STORAGE_KEYS.LAST_REFRESH_API_CALL, true, AuthConstants.TTL.REFRESH_TOKEN); // store for 1 min
                if (refreshRes.ok) {
                    console.log("Token refreshed. Retrying original request...");
                    return apiFetch(url, options, false); // retry once
                } else if (refreshRes.status === 401) {
                    const data = await refreshRes.json();
                    console.error("Refresh failed with data : " + JSON.stringify(data));
                    if (data.errorCode.startsWith("AT-")) {
                        // jwt token related error, so need to start a fresh
                        await logoutAndRedirect();
                        return null;
                    }
                }
            } else if (res.status === 401) {
                const resClone = res.clone();
                const data = await resClone.json();
                console.error("API failed with data : " + JSON.stringify(data));
                if (data.errorCode.startsWith("AT-")) {
                    // jwt token related error, so need to start a fresh
                    await logoutAndRedirect();
                    return null;
                }
            }

            return res;
        } catch (err) {
            console.error("API call failed", err);
            throw err;
        }
    }

    async function logoutAndRedirect() {
        try {
            let userId = getItemWithExpiry(AuthConstants.STORAGE_KEYS.USER_ID);
            if (userId) {
                //await AuthService.apiFetch("/api/v1/user/" + userId + "/logout", { method: "POST" });
            }
        } catch (e) {
            console.warn("Logout API failed:", e);
        }

        // Clear cookies (manual delete since HttpOnly can't be removed via JS)
        document.cookie.split(";").forEach(cookie => {
            document.cookie = cookie
                .replace(/^ +/, "")
                .replace(/=.*/, "=;expires=" + new Date(0).toUTCString() + ";path=/");
        });

        // Clear localStorage but saving deviceId for better user experience
        let deviceId = getItemWithExpiry(AuthConstants.STORAGE_KEYS.DEVICE_ID);
        localStorage.clear();
        setItemWithExpiry(AuthConstants.STORAGE_KEYS.DEVICE_ID, deviceId);

        alert("last session invalidated, so logged out. Login Afresh !!!");
        // Redirect to login
        setTimeout(() => {
            window.location.href = "/login.html";
        }, 2000); // 2 seconds delay
    }

    function getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) return parts.pop().split(';').shift();
        return null;
    }

    function getKeyFromCookie(key) {
        let keyVal = localStorage.getItem(key);
        if (!keyVal) {
            keyVal = getCookie(key);
            if (keyVal) {
                console.log("Recovering " + key + " from cookie...");
                localStorage.setItem(key, keyVal);
            }
        }
        return keyVal;
    }

    function setItemWithExpiry(key, value, ttlMillis) {
        let now;
        // ttlMillis = -1 or empty for live till session
        if (!ttlMillis) ttlMillis = -1;

        if (ttlMillis === -1) now = 0;
        else now = Date.now();

        const item = {
            value: value,
            expiry: now + ttlMillis
        };
        localStorage.setItem(key, JSON.stringify(item));
    }

    function getItemWithExpiry(key) {
        const itemStr = localStorage.getItem(key);
        if (!itemStr) return null;

        const item = JSON.parse(itemStr);
        if (item.expiry === -1) return item.value;

        const now = Date.now();
        if (now > item.expiry) {
            localStorage.removeItem(key);
            return null;
        }

        return item.value;
    }

    function removeItem(key) {
        localStorage.removeItem(key);
    }

    function getDeviceId() {
        let deviceId = getItemWithExpiry(AuthConstants.STORAGE_KEYS.DEVICE_ID);
        if (!deviceId) {
            deviceId = crypto.randomUUID();
            setItemWithExpiry(AuthConstants.STORAGE_KEYS.DEVICE_ID, deviceId);
        }
        return deviceId;
    }

    return {
        apiFetch,
        getCookie,
        getKeyFromCookie,
        logoutAndRedirect,
        setItemWithExpiry,
        getItemWithExpiry,
        getDeviceId,
        removeItem
    };
})();
