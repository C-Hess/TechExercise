import React from "react";

class Message extends React.Component {
  render() {
    return (
      <div className="card shadow-sm rounded bg-light my-3 mx-5">
        <div className="card-header">
          Posted by <strong>{this.props.userEmail}</strong> on{" "}
          <strong>{this.props.createdOn}</strong>
        </div>
        <div className="card-body">
          <p className="card-text">
            {this.props.message}
          </p>
        </div>
      </div>
    );
  }
}

export default Message;
