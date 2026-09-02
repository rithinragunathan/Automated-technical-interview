export const getProficiencyLabel = (score) => {
  if (score >= 90) return 'Expert';
  if (score >= 75) return 'Advanced';
  if (score >= 60) return 'Intermediate';
  if (score >= 40) return 'Beginner';
  return 'Novice';
};

export const getScoreColor = (score) => {
  if (score >= 80) return '#2ea043'; // Green
  if (score >= 60) return '#d29922'; // Yellow/Orange
  return '#f85149'; // Red
};

export const getDifficultyColor = (difficulty) => {
  switch (difficulty?.toUpperCase()) {
    case 'EASY': return 'success'; // maps to badge variant
    case 'MEDIUM': return 'warning';
    case 'HARD': return 'error';
    default: return 'neutral';
  }
};

export const getStatusColor = (status) => {
  switch (status?.toUpperCase()) {
    case 'COMPLETED':
    case 'PASSED':
      return 'success';
    case 'IN_PROGRESS':
    case 'RUNNING':
      return 'info';
    case 'FAILED':
    case 'ERROR':
      return 'error';
    case 'PENDING':
    default:
      return 'neutral';
  }
};
