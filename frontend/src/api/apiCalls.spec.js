import axios from 'axios';
import * as apiCalls from './apiCalls';

describe('apiCalls', () => {
  describe('signup', () => {
    it('calls /api/1.0/users', () => {
      const mockSignup = jest.fn();
      axios.post = mockSignup;
      apiCalls.signup();

      //first [0], returns first call from the call history 
      //second [0], returns first paramater  
      const path = mockSignup.mock.calls[0][0];
      expect(path).toBe('/api/1.0/users');
    });
  });

  describe('login', () => {
    it('calls /api/1.0/login', () => {
      const mockLogin = jest.fn();
      axios.post = mockLogin;
      apiCalls.login({ username: 'test-user', password: 'P4ssword' });
      const path = mockLogin.mock.calls[0][0];
      expect(path).toBe('/api/1.0/login');
    })
  })
});