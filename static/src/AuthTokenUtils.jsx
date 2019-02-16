class AuthTokenUtils {
    static getAuthTokenFromStorage = () => {
        const authTokenID = window.localStorage.getItem("authTokenID");
        if(authTokenID === null) {
            return null;
        }

        const authToken = window.localStorage.getItem(authTokenID);
        if(authToken === null) {
            return null;
        }

        const authExpiration = window.localStorage.getItem("authTokenExpiration");
        if(authExpiration === null) {
            return null;
        }
        const authExpirationDate = new Date(authExpiration);

        const token = {
            authTokenID, authToken, authExpirationDate
        };
        if(this.isTokenExpired(token)) {
            return null;
        } else {
            return token;
        }
    }


    static isTokenExpired = (authToken) => {
        return Date.now() - authToken.authExpirationDate <= 0;
    }
}

export default AuthTokenUtils;