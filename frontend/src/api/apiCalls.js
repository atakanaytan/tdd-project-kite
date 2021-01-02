  
import axios from 'axios';

export const signup = (user) => {
  return axios.post('/api/1.0/users', user);
};

export const login = (user) => {
  return axios.post('/api/1.0/login', {}, { auth: user });
};

export const setAuthorizationHeader = ({username, password, isLoggedIn}) => {
  if (isLoggedIn) {
    axios.defaults.headers.common['Authorization'] = `Basic ${btoa( 
      username + ':'   + password
    )}`;
  } else {
    delete axios.defaults.headers.common['Authorization'];
  }
};

export const listUsers = (param = {page: 0, size: 3}) => {
  const pageHasValueOrDefault = (param.page || 0);
  const sizeHasValueOrDefault = (param.size || 3);
  const path = `/api/1.0/users?page=${pageHasValueOrDefault}&size=${sizeHasValueOrDefault}`;
  return axios.get(path);
};

export const getUser = (username) => {
  return axios.get(`/api/1.0/users/${username}`);
};