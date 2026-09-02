export const LANGUAGES = [
  { id: 'JAVA', name: 'Java', monacoId: 'java', extension: 'java' },
  { id: 'PYTHON', name: 'Python', monacoId: 'python', extension: 'py' },
  { id: 'CPP', name: 'C++', monacoId: 'cpp', extension: 'cpp' },
  { id: 'JAVASCRIPT', name: 'JavaScript', monacoId: 'javascript', extension: 'js' }
];

export const DIFFICULTIES = [
  { id: 'EASY', name: 'Easy' },
  { id: 'MEDIUM', name: 'Medium' },
  { id: 'HARD', name: 'Hard' }
];

export const INTERVIEW_TYPES = [
  { id: 'CODING', name: 'Coding Assessment', description: 'Solve algorithmic problems in an IDE environment.' },
  { id: 'TECHNICAL_QA', name: 'Technical Q&A', description: 'Answer conceptual questions about frameworks, tools, and best practices.' },
  { id: 'MIXED', name: 'Mixed Format', description: 'A combination of coding and conceptual questions.' }
];

export const TOPICS = [
  { id: 'DATA_STRUCTURES', name: 'Data Structures' },
  { id: 'ALGORITHMS', name: 'Algorithms' },
  { id: 'SYSTEM_DESIGN', name: 'System Design' },
  { id: 'JAVA_SPRING', name: 'Java & Spring Boot' },
  { id: 'DATABASE', name: 'Databases & SQL' },
  { id: 'FRONTEND', name: 'Frontend (React/JS)' }
];

export const SUBMISSION_STATUS = {
  IDLE: 'IDLE',
  SUBMITTING: 'SUBMITTING',
  COMPILING: 'COMPILING',
  RUNNING: 'RUNNING',
  EVALUATING: 'EVALUATING',
  DONE: 'DONE',
  ERROR: 'ERROR'
};
