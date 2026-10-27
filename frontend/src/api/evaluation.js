import client from './client';

export const submitAnswer = (interviewId, questionId, answerText) => {
  return client.post(`/interviews/${interviewId}/questions/${questionId}/answer`, { answerText });
};
