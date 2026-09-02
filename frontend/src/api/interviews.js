import client from './client';

export const createInterview = (data) => {
  return client.post('/interviews', data);
};

export const getInterviews = () => {
  return client.get('/interviews');
};

export const getInterview = (id) => {
  return client.get(`/interviews/${id}`);
};

export const startInterview = (id) => {
  return client.post(`/interviews/${id}/start`);
};

export const finishInterview = (id) => {
  return client.post(`/interviews/${id}/finish`);
};

export const getReport = (id) => {
  return client.get(`/interviews/${id}/report`);
};
