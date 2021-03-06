import React, { Component } from "react";
import * as apiCalls from "../api/apiCalls";
import Spinner from "../components/Spinner";
import KiteView from "./KiteView";

class KiteFeed extends Component {
  state = {
    page: {
      content: [],
    },
    isLoadingKites: false,
  };

  componentDidMount() {
    this.setState({ isLoadingKites: true });
    apiCalls.loadKites(this.props.user).then((response) => {
      this.setState({ page: response.data, isLoadingKites: false });
    });
  }

  render() {
    if (this.state.isLoadingKites) {
      return <Spinner />;
    }

    if (this.state.page.content.length === 0) {
      return (
        <div className="card card-header text-center">There are no kites</div>
      );
    }

    return (
      <div>
        {this.state.page.content.map((kite) => {
          return <KiteView key={kite.id} kite={kite} />;
        })}
      </div>
    );
  }
}

export default KiteFeed;
