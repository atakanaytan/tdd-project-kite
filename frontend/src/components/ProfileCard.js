import React from 'react';
import ProfileImageWithDefault from './ProfileImageWithDefault';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faUserEdit, faSave, faWindowClose } from '@fortawesome/free-solid-svg-icons'
import Input from './Input';
import ButtonWithProgress from './ButtonWithProgress';

const ProfileCard = (props) => {
  const { displayName, username, image } = props.user;
  
  const showEditButton = props.isEditable && !props.inEditMode;

  return ( 
    <div className="card">
       <div className="card-header text-center">
         <ProfileImageWithDefault 
           alt="profile" 
           width="200" 
           height="200" 
           image={image}
           src={props.loadedImage} 
           className="rounded-circle shadow"     
        />  
       </div>
       <div className="card-body text-center">
         {!props.inEditMode && <h4>{`${displayName}@${username}`}</h4>}
         {props.inEditMode && (
           <div className="mb-2">
             <Input 
               value={displayName} 
               label={`Change Display Name for ${username}`}
               onChange={props.onChangeDisplayName}
               hasError={props.errors.displayName && true}
               error={props.errors.displayName}   
              />
              <div className="mt-2">
                <Input 
                  type="file" 
                  onChange={props.onFileSelect}
                  hasError={props.errors.image && true}
                  error={props.errors.image} 
                />
              </div>
           </div>
          )}
         {showEditButton && (
           <button className="btn btn-outline-success" onClick={props.onClickEdit}>
             <FontAwesomeIcon 
               icon={faUserEdit} 
             /> Edit
           </button>
         )}
         {props.inEditMode && (
           <div>
             <ButtonWithProgress 
                className="btn btn-primary" 
                onClick={props.onClickSave}
                text={
                  <span>
                    <FontAwesomeIcon icon={faSave}/> Save
                  </span>
                }
                pendingApiCall={props.pendingUpdateCall}
                disabled={props.pendingUpdateCall}
              />
              <button 
                 className="btn btn-outline-secondary ml-1" 
                 onClick={props.onClickCancel}
                 disabled={props.pendingUpdateCall}
               > 
                 <FontAwesomeIcon 
                   icon={faWindowClose}
                /> Cancel
              </button>
           </div>
         )}  
       </div>   
    </div>
  );
};

ProfileCard.defaultProps = {
  errors: {}
};

export default ProfileCard;