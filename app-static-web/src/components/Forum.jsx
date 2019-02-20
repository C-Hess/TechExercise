import React from "react";
import { sha256 } from "js-sha256";
import dateFormat from "dateformat";
import Axios from "axios";
import Message from "./Message";
import AppUtils from "../AppUtils";

class Forum extends React.Component {
  lastAlertID = 0;

  state = {
    newPostInput: "",
    alerts: [],
    messageCache: null
  };

  hexToBase64 = hexstring => {
    return btoa(
      hexstring
        .match(/\w{2}/g)
        .map(function(a) {
          return String.fromCharCode(parseInt(a, 16));
        })
        .join("")
    );
  };

  authenticateRequest = (request, token, uri) => {
    const reqStr = token.token + "+" + request.method.toUpperCase() + "+" + uri;
    const digest = this.hexToBase64(sha256(reqStr));

    if (request.headers == undefined) {
      request.headers = [];
    }
    request.headers.Authorization = "digest " + token.tokenID + ":" + digest;
  };

  reloadMessages = () => {
    const token = this.props.tokenProvider();
    const request = {
      method: "get",
      url:
        "http://" + AppUtils.getHostname() + ":8080/api/v1/messages",
      data: JSON.stringify({
        message: this.state.newPostInput
      }),
      headers: {
        "Content-Type": "application/json"
      }
    };

    this.authenticateRequest(request, token, "/api/v1/messages");
    Axios(request).then(
      data => {
        this.setState({ messageCache: data.data.messages });
        console.log(data.data.messages);
      },
      error => {
        if(error.response.data.statusCode == 401) {
          this.props.onLogout();
        }
        this.createAlert("An error occurred!", error.response.data.message);
      }
    );
  };

  getMessages = () => {
    if (this.state.messageCache == null) {
      this.reloadMessages();
    } else {
      if (this.state.messageCache.length == 0) {
        return (
          <div className="alert alert-info mt-3 mx-5">
            No messages have been posted yet.
          </div>
        );
      } else {
        return this.state.messageCache.map(message => {
          let date = new Date(message.createdOn + " UTC");
          let dateStr = dateFormat(date, "ddd, mmm dS, yyyy h:MM:ss TT");

          return (
            <Message
              key={message.messageID}
              userEmail={message.userEmail}
              message={message.message}
              createdOn={dateStr}
            />
          );
        });
      }
    }
  };

  createAlert = (boldedMessage, message) => {
    const id = this.lastAlertID++;

    setTimeout(() => {
      this.closeAlert(id);
    }, 5000);
    const newAlerts = [...this.state.alerts];
    newAlerts.push({ id, boldedMessage, message });
    this.setState({ alerts: newAlerts });
  };

  closeAlert = id => {
    const newAlerts = [...this.state.alerts];
    for (let i = 0; i < newAlerts.length; i++) {
      if (newAlerts[i].id === id) {
        newAlerts.splice(i, 1);
        this.setState({ alerts: newAlerts });
        return;
      }
    }
  };

  getAlerts = () => {
    return this.state.alerts.map(alert => {
      return (
        <div
          style={{ top: "0px", zIndex: 3000 }}
          className="position-sticky text-center"
          key={alert.id}
        >
          <div className="alert alert-danger d-inline-block mt-3 w-50">
            <strong>{alert.boldedMessage}</strong>
            {" " + alert.message}
            <button
              type="button"
              className="close"
              onClick={() => {
                this.closeAlert(alert.id);
              }}
            >
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
        </div>
      );
    });
  };

  handlePostMessage = () => {
    if (this.state.newPostInput.length < 5) {
      this.createAlert("Cannot post!", "Post length is too short.");
      return;
    }
    if (this.state.newPostInput.length > 1000) {
      this.createAlert(
        "Cannot post!",
        "Post length is too long. Must not exceed 1000 characters"
      );
      return;
    }

    const token = this.props.tokenProvider();
    if (token != null) {
      const request = {
        method: "post",
        url:
          "http://" + AppUtils.getHostname() + ":8080/api/v1/messages",
        data: JSON.stringify({
          message: this.state.newPostInput
        }),
        headers: {
          "Content-Type": "application/json"
        }
      };

      this.authenticateRequest(request, token, "/api/v1/messages");
      Axios(request).then(
        data => {
          console.log(data);
          this.setState({ newPostInput: "" });
          this.reloadMessages();
        },
        error => {
          if(error.response.data.statusCode == 401) {
            this.props.onLogout();
          }
          this.createAlert("An error occurred!", error.response.data.message);
        }
      );
    }
  };

  handleNewPostInputChange = e => {
    this.setState({ newPostInput: e.target.value });
  };

  render() {
    return (
      <React.Fragment>
        <nav className="navbar navbar-dark bg-dark navbar-expand-lg">
          <a className="navbar-brand" href="#">
            Chat Forum
          </a>
          <ul className="nav navbar-nav ml-auto">
            <li className="nav-item">
              <a className="nav-link" href="#" onClick={this.props.onLogout}>
                Logout <i className="fas fa-sign-out-alt" />
              </a>
            </li>
          </ul>
        </nav>
        {this.getAlerts()}

        {this.getMessages()}
        <div style={{ bottom: "0px" }} className="position-sticky text-center">
          <div className="w-75 d-inline-block my-3 shadow">
            <div className="input-group">
              <textarea
                style={{ resize: "none" }}
                className="form-control"
                rows="3"
                placeholder="Create a new post"
                onChange={this.handleNewPostInputChange}
                value={this.state.newPostInput}
              />
              <div className="input-group-append">
                <button
                  className="btn btn-success"
                  onClick={this.handlePostMessage}
                >
                  <i className="fas fa-plus-circle fa-4x" />
                </button>
              </div>
            </div>
          </div>
        </div>
      </React.Fragment>
    );
  }
}

export default Forum;
