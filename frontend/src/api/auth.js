import client from './client';

export const login = (email, password) => {
  return client.post('/auth/login', { email, password });
};

export const register = (userData) => {
  return client.post('/auth/register', userData);
};
