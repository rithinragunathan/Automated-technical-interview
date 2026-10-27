import client from './client';

export const getRecommendations = () => {
  return client.get('/recommendations');
};

export const updateRecommendationCompletion = (id, completed) => {
  return client.patch(`/recommendations/${id}/completion`, null, { params: { completed } });
};
