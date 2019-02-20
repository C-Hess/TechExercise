class AuthTokenUtils {
  static getAuthTokenFromStorage = () => {
    try {
      return JSON.parse(window.localStorage.getItem("authToken"));
    } catch (err) {
      return null;
    }
  };

  static writeAuthTokenToStorage = newToken => {
    window.localStorage.setItem("authToken", JSON.stringify(newToken));
  };

  static removeAuthTokenFromStorage = () => {
    window.localStorage.removeItem("authToken");
  };
}

export default AuthTokenUtils;
