import React, { Component } from "react";
import * as apiCalls from "../api/apiCalls";

class KiteFeed extends Component {
  componentDidMount() {
    apiCalls.loadKites(this.props.user);
  }
  render() {
    return <div>There are no kites</div>;
  }
}

export default KiteFeed;
