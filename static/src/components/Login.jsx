import React, { Component } from "react";

class Login extends React.Component {
  state = {
    userEmailInput: "",
    userPasswordInput: "",
    keepMeLoggedIn: false,
    isLoginMode: true,
    userPasswordConfirmInput: ""
  };

  handleEmailInputChange = e => {
    this.setState({ userEmailInput: e.target.value });
  };

  handlePasswordInputChange = e => {
    this.setState({ userPasswordInput: e.target.value });
  };

  handlePasswordConfirmInputChange = e => {
    this.setState({ userPasswordConfirmInput: e.target.value });
  };

  handleRememberMeCheckboxChange = e => {
    this.setState({ keepMeLoggedIn: e.target.checked });
  };

  handleLogin = () => {};

  handleSignUp = () => {};

  toggleLoginSignUp = isLoginMode => {
    this.setState({ userPasswordConfirmInput: "", isLoginMode });
  };

  getButtons = () => {
    if (this.state.isLoginMode) {
      return (
        <React.Fragment>
          <button
            className="btn btn-outline-success align-right"
            onClick={this.handleLogin}
          >
            Login
          </button>
          <span className="card-text ml-2">
            or{" "}
            <a
              href="#"
              className="card-link"
              onClick={() => {
                this.toggleLoginSignUp(false);
              }}
            >
              Sign Up
            </a>
          </span>
        </React.Fragment>
      );
    } else {
      return (
        <React.Fragment>
          <button className="btn btn-outline-success align-right">
            Sign Up
          </button>
          <span className="card-text ml-2">
            or{" "}
            <a
              href="#"
              className="card-link"
              onClick={() => {
                this.toggleLoginSignUp(true);
              }}
            >
              Login
            </a>
          </span>
        </React.Fragment>
      );
    }
  };

  getPasswordConfirm = () => {
    if (!this.state.isLoginMode) {
      return (
        <div className="form-group">
          <label>Confirm Password</label>
          <input
            className="form-control"
            type="password"
            placeholder="Enter your password again"
            onChange={this.handlePasswordConfirmInputChange}
            value={this.state.userPasswordConfirmInput}
          />
        </div>
      );
    }
    return "";
  };

  render() {
    return (
      <div
        style={{ minHeight: "100vh" }}
        className="d-flex flex-col justify-content-center align-items-center"
      >
        <div style={{minWidth: "300px"}} className="card bg-light w-25">
          <div className="card-header">
            {this.state.isLoginMode
              ? "Tech Exercise Login"
              : "Tech Exercise Sign-Up"}
          </div>
          <div className="card-body">
            <div className="form-group">
              <label>Email</label>
              <input
                className="form-control"
                type="text"
                placeholder="Enter your email"
                onChange={this.handleEmailInputChange}
                value={this.state.userEmailInput}
              />
            </div>
            <div className="form-group">
              <label>Password</label>
              <input
                className="form-control"
                type="password"
                placeholder="Enter your password"
                onChange={this.handlePasswordInputChange}
                value={this.state.userPasswordInput}
              />
            </div>
            {this.getPasswordConfirm()}
            <div className="form-check mb-3">
              <input
                className="form-check-input"
                type="checkbox"
                checked={this.state.keepMeLoggedIn}
                onChange={this.handleRememberMeCheckboxChange}
              />
              <label className="form-check-label">Keep me logged in</label>
            </div>

            {this.getButtons()}
          </div>
        </div>
      </div>
    );
  }
}

export default Login;
