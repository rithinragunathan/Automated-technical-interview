import client from './client';

export const getProblems = (params) => {
  return client.get('/problems', { params });
};

export const getProblem = (id) => {
  return client.get(`/problems/${id}`);
};
