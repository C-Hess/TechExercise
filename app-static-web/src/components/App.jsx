import React, { Component } from "react";
import Login from "./Login";
import Forum from "./Forum";
import AuthTokenUtils from "../AuthTokenUtils"

class App extends React.Component {
  authToken = AuthTokenUtils.getAuthTokenFromStorage();

  state = {
    isLoggedIn: this.authToken !== null
  }

  getAuthToken = () => {
    if(this.authToken === null ) {
      this.authToken = null;
      if(this.state.isLoggedIn) this.setState({isLoggedIn: false});
    }
    return this.authToken;
  }

  handleLoginSuccess = (newToken) => {
    this.authToken = newToken;
    AuthTokenUtils.writeAuthTokenToStorage(newToken);
    this.setState({isLoggedIn: true});
  }

  handleLogout = () => {
    AuthTokenUtils.writeAuthTokenToStorage(null);
    this.authToken = null;
    this.setState({isLoggedIn: false});
  }

  render() {
    return (
      <React.Fragment>
        {this.state.isLoggedIn ? <Forum onLogout={this.handleLogout} tokenProvider={this.getAuthToken}/> : <Login onLoginSuccess={this.handleLoginSuccess}/>}
      </React.Fragment>
    );
  }
}


export default App;
