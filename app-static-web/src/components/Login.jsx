import React, { Component } from "react";
import Axios from "axios";
import AppUtils from "../AppUtils";

class Login extends React.Component {
  state = {
    userEmailInput: "",
    userEmailInputError: false,
    userPasswordInput: "",
    userPasswordInputError: false,
    isLoginMode: true,
    userPasswordConfirmInput: "",
    userPasswordConfirmInputError: false,
    failureInfo: ""
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

  handleLogin = () => {
    const request = {
      method: "post",
      url: "http://" + AppUtils.getHostname() + ":8080/api/v1/auth/tokens",
      data: JSON.stringify({
        email: this.state.userEmailInput,
        password: this.state.userPasswordInput
      }),
      headers: {
        "Content-Type": "application/json"
      }
    };

    Axios(request).then(
      data => {
        this.setState({ failureInfo: "" });
        this.props.onLoginSuccess(data.data);
      },
      error => {
        if (error.response != undefined && error.response.data != undefined) {
          this.setState({ failureInfo: error.response.data.message });
        } else {
          console.log(error);
        }
      }
    );
  };

  handleSignUp = () => {
    this.setState({
      userEmailInputError: this.state.userEmailInput.length < 6,
      userPasswordInputError: this.state.userPasswordInput.length < 6,
      userPasswordConfirmInputError:
        this.state.userPasswordInput !== this.state.userPasswordConfirmInput
    });

    if (
      this.state.userPasswordInput === this.state.userPasswordConfirmInput &&
      this.state.userPasswordInput.length > 5 &&
      this.state.userEmailInput.length > 5
    ) {
      const request = {
        method: "post",
        url: "http://" + AppUtils.getHostname() + ":8080/api/v1/users",
        data: JSON.stringify({
          email: this.state.userEmailInput,
          password: this.state.userPasswordInput
        }),
        headers: {
          "Content-Type": "application/json"
        }
      };

      Axios(request)
        .then(data => {
          this.setState({ failureInfo: "" });
          this.handleLogin();
        }, (reason) => {
          if (reason.response != undefined && reason.response.data != undefined) {
            this.setState({ failureInfo: reason.response.data.message });
          } else {
            console.log(reason);
          }
        });
    } else {
      if (this.state.userEmailInput.length < 6) {
        this.setState({ failureInfo: "Email is too short (<6)" });
      }

      if (this.state.userPasswordInput.length < 6) {
        this.setState({ failureInfo: "Password is too short (<6)" });
      }

      if (
        this.state.userPasswordInput !== this.state.userPasswordConfirmInput
      ) {
        this.setState({ failureInfo: "Passwords do not match" });
      }
    }
  };

  toggleLoginSignUp = isLoginMode => {
    this.setState({
      userPasswordConfirmInput: "",
      isLoginMode,
      failureInfo: ""
    });
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
          <button
            onClick={this.handleSignUp}
            className="btn btn-outline-success align-right"
          >
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
            className={
              this.state.userPasswordConfirmInputError
                ? "form-control is-invalid"
                : "form-control"
            }
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

  getFailureAlert = () => {
    if (this.state.failureInfo.length > 0) {
      return <div className="alert alert-danger">{this.state.failureInfo}</div>;
    }
    return "";
  };

  render() {
    return (
      <div
        style={{ minHeight: "100vh" }}
        className="d-flex flex-col justify-content-center align-items-center"
      >
        <div style={{ minWidth: "300px" }} className="card bg-light w-25">
          <div className="card-header">
            {this.state.isLoginMode
              ? "Tech Exercise Login"
              : "Tech Exercise Sign-Up"}
          </div>
          <div className="card-body">
            {this.getFailureAlert()}
            <div className="form-group">
              <label>Email</label>
              <input
                className={
                  this.state.userEmailInputError
                    ? "form-control is-invalid"
                    : "form-control"
                }
                type="text"
                placeholder="Enter your email"
                onChange={this.handleEmailInputChange}
                value={this.state.userEmailInput}
              />
            </div>
            <div className="form-group">
              <label>Password</label>
              <input
                className={
                  this.state.userPasswordInputError
                    ? "form-control is-invalid"
                    : "form-control"
                }
                type="password"
                placeholder="Enter your password"
                onChange={this.handlePasswordInputChange}
                value={this.state.userPasswordInput}
              />
            </div>
            {this.getPasswordConfirm()}
            {this.getButtons()}
          </div>
        </div>
      </div>
    );
  }
}

export default Login;
