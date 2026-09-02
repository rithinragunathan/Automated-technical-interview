import client from './client';

export const getRecommendations = () => {
  return client.get('/recommendations');
};
