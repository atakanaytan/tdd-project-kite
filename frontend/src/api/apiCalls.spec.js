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
    });
  });
  describe('listUser', () => {
    it('calls /api/1.0/users?page=0&size=3 when no param provided for listUsers', () => {
      const mockListUsers = jest.fn();
      axios.get = mockListUsers;
      apiCalls.listUsers();
      expect(mockListUsers).toBeCalledWith('/api/1.0/users?page=0&size=3');
    });

    it('calls /api/1.0/users?page=5&size=10 when corresponding provided for listUsers', () => {
      const mockListUsers = jest.fn();
      axios.get = mockListUsers;
      apiCalls.listUsers({page: 5, size: 10});
      expect(mockListUsers).toBeCalledWith('/api/1.0/users?page=5&size=10');
    });

    it('calls /api/1.0/users?page=5&size=10 when only page param provided for listUsers', () => {
      const mockListUsers = jest.fn();
      axios.get = mockListUsers;
      apiCalls.listUsers({page: 5});
      expect(mockListUsers).toBeCalledWith('/api/1.0/users?page=5&size=3');
    });

    it('calls /api/1.0/users?page=0&size=5 when only size param provided for listUsers', () => {
      const mockListUsers = jest.fn();
      axios.get = mockListUsers;
      apiCalls.listUsers({size: 5});
      expect(mockListUsers).toBeCalledWith('/api/1.0/users?page=0&size=5');
    });
  });
  describe('getUser', () => {
    it('calls /api/1.0/users/user5 when user5 is provided for getUser()', () => {
      const mockGetUser = jest.fn();
      axios.get = mockGetUser;
      apiCalls.getUser('user5');
      expect(mockGetUser).toBeCalledWith('/api/1.0/users/user5');
    });
  });
  describe('updateUser', () => {
    it('calls /api/1.0/users/5 when 5 is provided for updateUser', () => {
      const mockUpdateUser = jest.fn();
      axios.put = mockUpdateUser;
      apiCalls.updateUser('5');
      const path = mockUpdateUser.mock.calls[0][0];
      expect(path).toBe('/api/1.0/users/5');
    });
  });
  describe('postKites', () => {
    it('calls /api/1.0/kites', () => {
      const mockPostKite = jest.fn();
      axios.post = mockPostKite;
      apiCalls.postKite();
      const path = mockPostKite.mock.calls[0][0];
      expect(path).toBe('/api/1.0/kites');
    });
  });
  describe('loadHoaxes', () => {
    it('calls api/1.0/kites?page=0&size=5&sort=id,desc when no param provided', () => {
      const mockGetKites = jest.fn();
      axios.get = mockGetKites;
      apiCalls.loadKites();
      expect(mockGetKites).toBeCalledWith(
        '/api/1.0/kites?page=0&size=5&sort=id,desc'
      );
    });
    it('calls api/1.0/users/users1/kites?page=0&size=5&sort=id,desc when user param provided', () => {
      const mockGetKites = jest.fn();
      axios.get = mockGetKites;
      apiCalls.loadKites('user1');
      expect(mockGetKites).toBeCalledWith(
        '/api/1.0/users/user1/kites?page=0&size=5&sort=id,desc'
      );
    });
  })
});