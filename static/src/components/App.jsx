import React, { Component } from "react";

import Login from "./Login";
import Forum from "./Forum";
import AuthTokenUtils from "../AuthTokenUtils"

class App extends React.Component {
  authToken = AuthTokenUtils.getAuthTokenFromStorage();

  state = {
    isLoggedIn: this.authToken !== null
  }

  getAuthToken() {
    if(authToken === null || AuthTokenUtils.isTokenExpired(authToken)) {
      authToken = null;
      if(this.state.isLoggedIn) this.setState({isLoggedIn: false});
    }
    return authToken;
  }

  render() {
    return (
      <React.Fragment>
        {this.state.isLoggedIn ? <Forum /> : <Login/>}
      </React.Fragment>
    );
  }
}


export default App;
