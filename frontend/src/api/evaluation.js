import client from './client';

export const submitAnswer = (interviewId, questionId, answerText) => {
  return client.post(`/evaluation/interviews/${interviewId}/questions/${questionId}`, { answerText });
};
