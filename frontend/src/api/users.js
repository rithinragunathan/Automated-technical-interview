import client from './client';

export const getMe = () => {
  return client.get('/users/me');
};

export const updateProfile = (data) => {
  return client.put('/users/me', data);
};

export const getDashboard = () => {
  return client.get('/users/dashboard');
};
