import React from 'react';
import * as apiCalls from '../api/apiCalls';
import ProfileCard from '../components/ProfileCard';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faExclamationTriangle } from '@fortawesome/free-solid-svg-icons'


class UserPage extends React.Component{
  state = {
    user: undefined,
    userNotFound: false
  };

  componentDidMount() {
    this.loadUser();  
  };

  componentDidUpdate(prevProps) {
    if (prevProps.match.params.username !== this.props.match.params.username) {
       this.loadUser();
    } 
  };

  loadUser = () => {
    const username = this.props.match.params.username;
    if (!username) {
        return;
    }
    this.setState({ userNotFound: false });
    apiCalls
      .getUser(username)
      .then((response) => {
        this.setState({ user: response.data });  
    })
    .catch(error => {
        this.setState({
          userNotFound: true  
        })
    })  
  } 

  render() {
    if (this.state.userNotFound) {
      return (
        <div className="alert alert-danger text-center">
          <div className="alert-heading">
             <FontAwesomeIcon 
               icon={faExclamationTriangle} 
               size="3x"
             /> 
          </div>
          <h5>User not found</h5>  
        </div>  
      )  
    }  
    return(
      <div data-testid="userpage">
        {this.state.user && (<ProfileCard user={this.state.user} />) }
      </div>
    ); 
  }
}

UserPage.defaultProps = {
  match: {
    params: {

    }  
  }  
}

export default UserPage;