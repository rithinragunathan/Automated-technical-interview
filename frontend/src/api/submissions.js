import client from './client';

export const createSubmission = (data) => {
  return client.post('/submissions', data);
};

export const getSubmission = (id) => {
  return client.get(`/submissions/${id}`);
};

export const runCode = (data) => {
  return client.post('/execution/run', data);
};
