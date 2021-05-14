import React from 'react';
import UserList from '../components/UserList';
import KiteSubmit from '../components/KiteSubmit';
import { connect } from 'react-redux';
import KiteFeed from '../components/KiteFeed';

class HomePage extends React.Component{
    render() {
        return(
            <div data-testid="homepage">
              <div className="row">
                <div className="col-8">
                    {this.props.loggedInUser.isLoggedIn && <KiteSubmit /> }
                    <KiteFeed /> 
                </div>
                <div className="col-4">
                    <UserList />    
                </div>
              </div>
            </div>
        );
    }
}

const mapStateToProps = (state) => {
    return {
        loggedInUser: state
    };
};

export default connect(mapStateToProps)(HomePage);