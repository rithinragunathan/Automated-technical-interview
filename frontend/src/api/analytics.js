import client from './client';

export const getDashboard = () => {
  return client.get('/analytics/dashboard');
};
