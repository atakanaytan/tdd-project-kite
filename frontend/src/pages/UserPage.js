import React from 'react';
import * as apiCalls from '../api/apiCalls';
import ProfileCard from '../components/ProfileCard';
import { connect } from 'react-redux';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faExclamationTriangle } from '@fortawesome/free-solid-svg-icons'

class UserPage extends React.Component{
  state = {
    user: undefined,
    userNotFound: false,
    isLoadingUser: false,
    inEditMode: false
  };

  componentDidMount() {
    this.loadUser();  
  };

  componentDidUpdate(prevProps) {
    if (prevProps.match.params.username !== this.props.match.params.username) {
       this.loadUser();
    } 
  };
  
  onClickEdit = () => {
    this.setState({
      inEditMode: true
    })
  }

  onClickCancel = () => {
    this.setState({
      inEditMode: false
    })
  }

  loadUser = () => {
    const username = this.props.match.params.username;
    if (!username) {
        return;
    }
    this.setState({ userNotFound: false,  isLoadingUser: true });
    apiCalls
      .getUser(username)
      .then((response) => {
        this.setState({ user: response.data, isLoadingUser: false });  
    })
    .catch((error) => {
        this.setState({
          userNotFound: true,
          isLoadingUser: false  
        })
    })  
  } 

  render() {
    let pageContent;
    if (this.state.isLoadingUser) {
      pageContent = (
        <div className="d-flex">
          <div className="spinner-border text-black-50 m-auto">
            <span className="sr-only">Loading...</span>
          </div>
        </div>        
      );
    } else if (this.state.userNotFound){
      pageContent = (
        <div className="alert alert-danger text-center">
          <div className="alert-heading">
             <FontAwesomeIcon 
               icon={faExclamationTriangle} 
               size="3x"
             /> 
          </div>
          <h5>User not found</h5>  
        </div>  
      );
    } else {
      const isEditable = 
        this.props.loggedInUser.username === this.props.match.params.username;
      pageContent = this.state.user && (
        <ProfileCard 
          user={this.state.user} 
          isEditable={isEditable}   
          inEditMode= {this.state.inEditMode}
          onClickEdit={this.onClickEdit}
          onClickCancel={this.onClickCancel}
        />
      ); 
    }  
    return <div data-testid="userpage">{pageContent}</div>
  }
}

UserPage.defaultProps = {
  match: {
    params: {

    }  
  }  
}

const mapStateToProps = (state) => {
  return {
    loggedInUser: state
  }
}

export default connect(mapStateToProps)(UserPage);