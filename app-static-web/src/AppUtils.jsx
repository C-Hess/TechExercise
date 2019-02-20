class AppUtils {
  static getHostname() {
    if(window.location.hostname === "") {
      return "localhost";
    } else {
      return window.location.hostname;
    }
  }
}
  
  export default AppUtils;
  