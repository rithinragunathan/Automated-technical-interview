import client from './client';

export const getUsers = () => {
  return client.get('/admin/users');
};

export const createProblem = (data) => {
  return client.post('/admin/problems', data);
};

export const updateProblem = (id, data) => {
  return client.put(`/admin/problems/${id}`, data);
};

export const deleteProblem = (id) => {
  return client.delete(`/admin/problems/${id}`);
};

export const createQuestion = (data) => {
  return client.post('/admin/questions', data);
};

export const getAdminStats = () => {
  return client.get('/admin/stats');
};
