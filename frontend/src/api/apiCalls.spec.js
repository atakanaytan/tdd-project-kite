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
});